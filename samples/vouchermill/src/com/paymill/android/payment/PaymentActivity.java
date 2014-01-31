package com.paymill.android.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.paymill.android.api.Preauthorization;
import com.paymill.android.api.Transaction;
import com.paymill.android.factory.PMPaymentMethod;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.listener.PMGenerateTokenListener;
import com.paymill.android.listener.PMPreauthListener;
import com.paymill.android.listener.PMTransListener;
import com.paymill.android.payment.CardTypeParser.CardType;
import com.paymill.android.payment.PaymentActivity.Result.Type;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;
import com.paymill.android.service.PMService;

/**
 * Start this Activity with an intent created from the
 * {@link PaymentActivity.Factory} and you will receive a
 * {@link PaymentActivity.Result} in onActivityResult().
 */
public class PaymentActivity extends FragmentActivity {

	public static final int REQUEST_CODE = 7281;
	public static final String ARGUMENT_PARAM = "param";
	public static final String ARGUMENT_CONSUMABLE = "consumable";
	public static final String ARGUMENT_TYPE = "type";
	public static final String ARGUMENT_SETTINGS = "settings";
	public static final String ARGUMENT_MERCHANT_PUBLIC_KEY = "merchantPublicKey";
	public static final String ARGUMENT_MODE = "mode";
	public static final String RETURN_RESULT = "result";
	public static final String RETURN_ERROR = "error";
	public static final String RETURN_TYPE = "returnType";
	public static final int RESULT_OK = Activity.RESULT_OK;
	public static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
	public static final int RESULT_ERROR = -2;

	/**
	 * The type of payment that should be triggered.
	 */
	public enum PaymentType implements Parcelable {
		/**
		 * For token generation, when using the mode and public key specified
		 * during initialization.
		 */
		TOKEN,
		/**
		 * For token generation, when using specific mode and public key
		 * specified during initialization.
		 */
		TOKEN_WITH_PARAMS,
		/**
		 * For transactions.
		 */
		TRANSACTION,
		/**
		 * For preauthorizations.
		 */
		PREAUTHORIZATION;

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeInt(ordinal());
		}

		public static final Creator<PaymentType> CREATOR = new Creator<PaymentType>() {
			@Override
			public PaymentType createFromParcel(final Parcel source) {
				return PaymentType.values()[source.readInt()];
			}

			@Override
			public PaymentType[] newArray(final int size) {
				return new PaymentType[size];
			}
		};
	}

	// arguments
	private PaymentType paymentType;
	private Type resultType;
	private PMPaymentParams params;
	private boolean consumable;
	private PMService.ServiceMode mode;
	private String merchantPublicKey;
	private Settings pmSettings;
	Bundle pmSettingsBundle;

	FragmentManager fragmentManager = null;
	private int selectedFragmentId = R.id.creditCardButton;
	private static final String SELECTED_FRAGMENT_ID_KEY = "selectedfragment";
	private RadioGroup radioGroup;
	private FrameLayout creditCardLayout;
	private FrameLayout directDebitLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getIntent().getExtras();
		// check arguments
		params = data.getParcelable(ARGUMENT_PARAM);
		if (params == null) {
			wrongParams("No parameters supplied");
		}
		paymentType = data.getParcelable(ARGUMENT_TYPE);
		if (paymentType == null) {
			wrongParams("No type supplied");
		}
		if (paymentType == PaymentType.TOKEN
				|| paymentType == PaymentType.TOKEN_WITH_PARAMS) {
			resultType = Type.TOKEN;
		} else if (paymentType == PaymentType.TRANSACTION
				|| paymentType == PaymentType.PREAUTHORIZATION) {
			resultType = Type.TRANSACTION;
		} else {
			wrongParams("No type supplied");
		}
		consumable = data.getBoolean(ARGUMENT_CONSUMABLE);
		if (paymentType == PaymentType.TOKEN_WITH_PARAMS) {
			mode = getModeFromOrdinal(data.getInt(ARGUMENT_MODE));
			if (mode == null) {
				wrongParams("Invalid mode supplied");
			}
			merchantPublicKey = data.getString(ARGUMENT_MERCHANT_PUBLIC_KEY);
			if (TextUtils.isEmpty(merchantPublicKey)) {
				wrongParams("Invalid merchant public key supplied");
			}
		}
		pmSettings = data.getParcelable(ARGUMENT_SETTINGS);
		if (pmSettings == null) {
			wrongParams("No settings supplied");
		}
		boolean showGroup = false;
		// check if we should show both
		if (pmSettings.isCreditCardPaymentAllowed()
				&& pmSettings.isDirectDebitPaymentAllowed()) {
			showGroup = true;
		}
		pmSettingsBundle = new Bundle();
		pmSettingsBundle.putParcelable(ARGUMENT_SETTINGS, pmSettings);

		// add listeners
		PMManager.addListener(generateTokenListener);
		PMManager.addListener(preauthorizeListener);
		PMManager.addListener(transactionListener);
		this.setContentView(R.layout.pm_payment);

		// set activity background
		findViewById(R.id.ScrollView).getRootView().setBackgroundColor(
				getResources().getColor(R.color.backgroundColor));

		// check if screen rotated
		fragmentManager = getSupportFragmentManager();
		// add fragments
		directDebitLayout = (FrameLayout) findViewById(R.id.directDebitLayout);
		creditCardLayout = (FrameLayout) findViewById(R.id.creditCardLayout);
		DirectDebitFragment directdebitFragment = (DirectDebitFragment) fragmentManager
				.findFragmentById(R.id.directDebitLayout);
		CreditCardFragment creditCardFragment = (CreditCardFragment) fragmentManager
				.findFragmentById(R.id.creditCardLayout);
		if (directdebitFragment == null) {
			fragmentManager
					.beginTransaction()
					.add(R.id.directDebitLayout, DirectDebitFragment.instance(pmSettings),
							DirectDebitFragment.FRAGMENT_TAG).commit();
		}
		if (creditCardFragment == null) {
			fragmentManager
					.beginTransaction()
					.add(R.id.creditCardLayout,
							CreditCardFragment.instance(pmSettings),
							CreditCardFragment.FRAGMENT_TAG).commit();
		}
		// init radio group
		radioGroup = (RadioGroup) findViewById(R.id.pmButtonGroup);
		if (!showGroup) {
			radioGroup.setVisibility(View.GONE);
			// show the correct fragment
			if (pmSettings.isCreditCardPaymentAllowed()) {
				showFragment(R.id.creditCardButton);
			} else {
				showFragment(R.id.directDebitButton);
			}
		} else {
			radioGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							showFragment(checkedId);
						}
					});
			int selected = savedInstanceState != null ? savedInstanceState
					.getInt(SELECTED_FRAGMENT_ID_KEY, R.id.creditCardButton)
					: R.id.creditCardButton;
			radioGroup.check(selected);
			showFragment(selected);
		}

	}

	private void showFragment(int fragmentId) {
		switch (fragmentId) {
		case R.id.creditCardButton:
			creditCardLayout.setVisibility(View.VISIBLE);
			directDebitLayout.setVisibility(View.GONE);
			break;
		case R.id.directDebitButton:
			creditCardLayout.setVisibility(View.GONE);
			directDebitLayout.setVisibility(View.VISIBLE);
			break;
		default:
			throw new IllegalArgumentException("Unkown id:" + fragmentId);
		}
	}

	void startRequest(PMPaymentMethod method) {
		new ProgressDialogFragment().show(getSupportFragmentManager(),
				ProgressDialogFragment.TAG);
		switch (this.paymentType) {
		case TOKEN:
			generateToken(method);
			break;
		case TOKEN_WITH_PARAMS:
			generateTokenWithParams(method);
			break;
		case PREAUTHORIZATION:
			newPreauthorization(method);
			break;
		case TRANSACTION:
			newTransaction(method);
			break;
		default:
			wrongParams("Invalid type");
		}
	}

	/**
	 * This listener handles token generation
	 */
	PMGenerateTokenListener generateTokenListener = new PMGenerateTokenListener() {

		@Override
		public void onGenerateToken(String token) {
			success(token);
		}

		@Override
		public void onGenerateTokenFailed(PMError error) {
			failure(error);
		}

	};

	public void generateToken(PMPaymentMethod method) {
		PMManager.generateToken(PaymentActivity.this, method, params);
	}

	public void generateTokenWithParams(PMPaymentMethod method) {
		PMManager.generateToken(PaymentActivity.this, method, params, mode,
				merchantPublicKey);
	}

	PMTransListener transactionListener = new PMTransListener() {

		@Override
		public void onTransaction(Transaction transaction) {
			success(transaction);
			finish();
		}

		@Override
		public void onTransactionFailed(PMError error) {
			failure(error);
			finish();
		}

	};

	public void newTransaction(PMPaymentMethod method) {
		PMManager.transaction(PaymentActivity.this, method, params, consumable);
	}

	PMPreauthListener preauthorizeListener = new PMPreauthListener() {

		@Override
		public void onPreauth(Transaction preauthorization) {
			success(preauthorization);
		}

		@Override
		public void onPreauthFailed(PMError error) {
			failure(error);
		}

	};

	public void newPreauthorization(PMPaymentMethod method) {
		PMManager.preauthorization(PaymentActivity.this, method, params,
				consumable);
	}

	private void success(Parcelable result) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(RETURN_RESULT, result);
		success(bundle);
	}

	private void success(String result) {
		Bundle bundle = new Bundle();
		bundle.putString(RETURN_RESULT, result);
		success(bundle);
	}

	private void success(Bundle bundle) {
		bundle.putParcelable(RETURN_TYPE, resultType);
		Intent data = new Intent();
		data.putExtras(bundle);
		setResult(RESULT_OK, data);
		finish();
	}

	private void failure(PMError error) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(RETURN_ERROR, error);
		bundle.putParcelable(RETURN_TYPE, resultType);
		Intent data = new Intent();
		data.putExtras(bundle);
		setResult(RESULT_ERROR, data);
		finish();
	}

	void wrongParams(String message) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(RETURN_ERROR, new PMError(
				PMError.Type.WRONG_PARAMS, message));
		bundle.putParcelable(RETURN_TYPE, resultType);
		Intent data = new Intent();
		data.putExtras(bundle);
		setResult(RESULT_ERROR, data);
		finish();
	}

	void cancel() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(RETURN_TYPE, resultType);
		Intent data = new Intent();
		data.putExtras(bundle);
		setResult(RESULT_CANCELED, data);
		finish();
	}

	private static PMService.ServiceMode getModeFromOrdinal(int ordinal) {
		for (PMService.ServiceMode mode : PMService.ServiceMode.values()) {
			if (mode.ordinal() == ordinal) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		cancel();
	}

	@Override
	protected void onDestroy() {
		// very important to remove the listeners, in order to release resources
		PMManager.removeListener(generateTokenListener);
		PMManager.removeListener(transactionListener);
		PMManager.removeListener(preauthorizeListener);
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_FRAGMENT_ID_KEY, selectedFragmentId);
	}

	public PMPaymentParams getParams() {
		return params;
	}

	public boolean isConsumable() {
		return consumable;
	}

	public String getTriggerButtonText() {
		switch (paymentType) {
		case PREAUTHORIZATION:
			return getString(R.string.pm_trigger_preauth);
		case TRANSACTION:
			return getString(R.string.pm_trigger_payment);
		default:
			return getString(R.string.pm_trigger_default);
		}
	}

	/**
	 * Use this factory to generate Intents. Start a PaymentActivity using the
	 * configured Intent and {@link PaymentActivity#REQUEST_CODE} and get the
	 * result by calling the {@link Factory#getResultFrom(int, int, Intent)}
	 * method.
	 * 
	 */
	public static class Factory {
		/**
		 * Create an intent for a new {@link Transaction}.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @param settings
		 *            additional settings.
		 * @param consumable
		 *            true if the transaction should be consumable, false
		 *            otherwise
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getTransactionIntent(Context packageContext,
				PMPaymentParams params, Settings settings, boolean consumable) {
			Bundle data = new Bundle();
			data.putParcelable(ARGUMENT_PARAM, params);
			data.putBoolean(ARGUMENT_CONSUMABLE, consumable);
			data.putParcelable(ARGUMENT_TYPE, PaymentType.TRANSACTION);
			data.putParcelable(ARGUMENT_SETTINGS, settings);
			Intent intent = new Intent(packageContext, PaymentActivity.class);
			intent.putExtras(data);
			return intent;
		}

		/**
		 * Create an intent for a new {@link Transaction}.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @param consumable
		 *            true if the transaction should be consumable, false
		 *            otherwise
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getTransactionIntent(Context packageContext,
				PMPaymentParams params, boolean consumable) {
			return getTransactionIntent(packageContext, params, new Settings(),
					consumable);
		}

		/**
		 * Create an intent for a new {@link Preauthorization}.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @param settings
		 *            additional settings.
		 * @param consumable
		 *            true if the transaction should be consumable, false
		 *            otherwise
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getPreauthorizationIntent(Context packageContext,
				PMPaymentParams params, Settings settings, boolean consumable) {
			Bundle data = new Bundle();
			data.putParcelable(ARGUMENT_PARAM, params);
			data.putBoolean(ARGUMENT_CONSUMABLE, consumable);
			data.putParcelable(ARGUMENT_TYPE, PaymentType.PREAUTHORIZATION);
			data.putParcelable(ARGUMENT_SETTINGS, settings);
			Intent intent = new Intent(packageContext, PaymentActivity.class);
			intent.putExtras(data);
			return intent;
		}

		/**
		 * Create an intent for a new {@link Preauthorization}.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @param consumable
		 *            true if the transaction should be consumable, false
		 *            otherwise
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getPreauthorizationIntent(Context packageContext,
				PMPaymentParams params, boolean consumable) {
			return getPreauthorizationIntent(packageContext, params,
					new Settings(), consumable);
		}

		/**
		 * Create an intent for a new payment token. You don't need to init the
		 * SDK to use this method.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @param settings
		 *            additional settings.
		 * @param mode
		 *            LIVE or TEST mode.
		 * @param merchantPublicKey
		 *            your PayMill <b>public</b> key for LIVE or TEST mode,
		 *            depending on the previous parameter.
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getTokenIntent(Context packageContext,
				PMPaymentParams params, Settings settings,
				final PMService.ServiceMode mode, final String merchantPublicKey) {
			Bundle data = new Bundle();
			data.putParcelable(ARGUMENT_PARAM, params);
			data.putString(ARGUMENT_MERCHANT_PUBLIC_KEY, merchantPublicKey);
			data.putInt(ARGUMENT_MODE, mode.ordinal());
			data.putParcelable(ARGUMENT_TYPE, PaymentType.TOKEN_WITH_PARAMS);
			data.putParcelable(ARGUMENT_SETTINGS, settings);
			Intent intent = new Intent(packageContext, PaymentActivity.class);
			intent.putExtras(data);
			return intent;
		}

		public static Intent getTokenIntent(Context packageContext,
				PMPaymentParams params, final PMService.ServiceMode mode,
				final String merchantPublicKey) {
			return getTokenIntent(packageContext, params, new Settings(), mode,
					merchantPublicKey);
		}

		/**
		 * Create an intent for a new payment token.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param settings
		 *            additional settings.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getTokenIntent(Context packageContext,
				PMPaymentParams params, Settings settings) {
			Bundle data = new Bundle();
			data.putParcelable(ARGUMENT_PARAM, params);
			data.putParcelable(ARGUMENT_TYPE, PaymentType.TOKEN);
			data.putParcelable(ARGUMENT_SETTINGS, settings);
			Intent intent = new Intent(packageContext, PaymentActivity.class);
			intent.putExtras(data);
			return intent;
		}

		/**
		 * Create an intent for a new payment token.
		 * 
		 * @param packageContext
		 *            a context.
		 * @param params
		 *            created with the
		 *            {@link com.paymill.android.factory.PMFactory} .
		 * @return an {@link Intent}. Use starActivityForResult with
		 *         {@link PaymentActivity#REQUEST_CODE}.
		 */
		public static Intent getTokenIntent(Context packageContext,
				PMPaymentParams params) {
			return getTokenIntent(packageContext, params, new Settings());
		}

		/**
		 * Converts the parameters of a onActivityResult() callback to a Result
		 * 
		 * @param requestCode
		 *            the onActivityResult() requestCode
		 * @param resultCode
		 *            the onActivityResult() resultCode
		 * @param data
		 *            the onActivityResult() data
		 * @return a Result object, if the callback comes from the
		 *         {@link PaymentActivity}, null otherwise
		 */
		public static Result getResultFrom(int requestCode, int resultCode,
				Intent data) {
			if (requestCode != REQUEST_CODE) {
				return null;
			}
			Bundle results = data.getExtras();
			if (results == null) {
				return null;
			}
			Result.Type type = results.getParcelable(RETURN_TYPE);
			String token = null;
			Transaction transaction = null;
			if (type == Result.Type.TOKEN) {
				token = results.getString(RETURN_RESULT);
			} else {
				transaction = (Transaction) results
						.getParcelable(RETURN_RESULT);
			}
			PMError error = results.getParcelable(RETURN_ERROR);
			return new Result(transaction, token, type, error, resultCode);
		}
	}

	/**
	 * The Result of a payment screen. Use
	 * {@link Factory#getResultFrom(int, int, Intent)} to convert an
	 * onActivityResult() callback to a result object.
	 * 
	 */
	public static class Result implements Parcelable {
		public static enum Type implements Parcelable {
			TRANSACTION, TOKEN;

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(final Parcel dest, final int flags) {
				dest.writeInt(ordinal());
			}

			public static final Creator<Type> CREATOR = new Creator<Type>() {
				@Override
				public Type createFromParcel(final Parcel source) {
					return Type.values()[source.readInt()];
				}

				@Override
				public Type[] newArray(final int size) {
					return new Type[size];
				}
			};
		}

		private Transaction result;
		private String resultToken;
		private Type type;
		private PMError error;
		private int activityResult;

		private Result(Transaction result, String resultToken, Type type,
				PMError error, int activityResult) {
			this.result = result;
			this.resultToken = resultToken;
			this.type = type;
			this.error = error;
			this.activityResult = activityResult;
		}

		/**
		 * Get the resulting Transaction object.
		 * 
		 * @return the resulting transaction, or null if the {@link Type} is not
		 *         {@link Type#TRANSACTION} or {@link Result#isSuccess()} is not
		 *         true.
		 */
		public Transaction getResult() {
			return result;
		}

		/**
		 * Get the resulting token string.
		 * 
		 * @return the token or null if the the {@link Type} is not
		 *         {@link Type#TOKEN} or {@link Result#isSuccess()} it not true.
		 */
		public String getResultToken() {
			return resultToken;
		}

		/**
		 * Get the result type.
		 * 
		 * @return either {@link Type#TOKEN} or {@link Type#TRANSACTION}
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @return true if the process is canceled
		 */
		public boolean isCanceled() {
			return activityResult == RESULT_CANCELED;
		}

		/**
		 * 
		 * @return true if an error occurred, false otherwise
		 */
		public boolean isError() {
			return activityResult == RESULT_ERROR;

		}

		/**
		 * Returns an error if such occurred.
		 * 
		 * @return the error or null if no error occurred.
		 */
		public PMError getError() {
			return error;
		}

		/**
		 * Check if the activity was successful.
		 * 
		 * @return true if a result is available, false if the activity was
		 *         canceled or an error occurred.
		 */
		public boolean isSuccess() {
			return activityResult == RESULT_OK;

		}

		@Override
		public int describeContents() {
			return 0;
		}

		public Result(Parcel source) {
			result = source.readParcelable(Transaction.class.getClassLoader());
			resultToken = source.readString();
			type = source.readParcelable(Type.class.getClassLoader());
			error = source.readParcelable(PMError.class.getClassLoader());
			activityResult = source.readInt();

		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeParcelable(result, 0);
			dest.writeString(resultToken);
			dest.writeParcelable(type, 0);
			dest.writeParcelable(error, 0);
			dest.writeInt(activityResult);
		}

		public static final Creator<Result> CREATOR = new Creator<Result>() {
			@Override
			public Result createFromParcel(final Parcel source) {
				return new Result(source);
			}

			@Override
			public Result[] newArray(final int size) {
				return new Result[size];
			}
		};
	}

	/**
	 * An instance of this class describes configuration of the payment screen.
	 * You can pre-fill some data and configure available payment methods.
	 * Object created with the default constructor will allow all credit card
	 * types and disallow direct debit. Note, that you have to specify a direct
	 * debit country if you want to use direct debit.
	 * 
	 * 
	 */
	public static class Settings implements Parcelable {

		// pre-fill data
		private String accountHolder;
		private String cardnumber;
		private String expiryMonth;
		private String expiryYear;
		private String verification;
		private String accountNumber;
		private String bankNumber;

		// cc and dd data
		private String directDebitCountry;
		private HashSet<CardType> cardTypes = new HashSet<CardTypeParser.CardType>();

		/**
		 * Default Settings.\All credit cards are allowed. Direct debit is not
		 * allowed.
		 */
		public Settings() {
			cardTypes.add(CardType.Visa);
			cardTypes.add(CardType.Maestro);
			cardTypes.add(CardType.MasterCard);
			cardTypes.add(CardType.AmericanExpress);
			cardTypes.add(CardType.DinersClub);
			cardTypes.add(CardType.Discover);
			cardTypes.add(CardType.UnionPay);
			cardTypes.add(CardType.JCB);
		}

		/**
		 * Custom Settings. Specified credit cards are allowed. Direct debit is
		 * not allowed.
		 * 
		 * @param cardTypes
		 *            allowed credit card types
		 * @throws IllegalArgumentException
		 *             if you try to add an invalid card type like Invalid or
		 *             YetUnkown.
		 */
		public Settings(CardType... cardTypes) {
			// add all card types by default
			for (CardType cardType : cardTypes) {
				checkValidCardType(cardType);
				this.cardTypes.add(cardType);
			}
		}

		/**
		 * Custom Settings.Credit cards are not allowed. Direct debit for the
		 * specified country is allowed.
		 * 
		 * @param country
		 *            ISO 3166-2 formatted country code
		 */
		public Settings(String country) {
			this.directDebitCountry = country;
		}

		public Collection<CardType> getAllowedCardTypes() {
			return cardTypes;
		}

		/**
		 * Enables a specific credit card type.
		 * 
		 * @param cardType
		 *            the credit card type.
		 * @throws IllegalArgumentException
		 *             if you try to add an invalid card type like Invalid or
		 *             YetUnkown.
		 */
		public void enableCreditCardType(CardType cardType) {
			cardTypes.add(cardType);
		}

		/**
		 * Disables a credit card type, if it was previously enabled.
		 * 
		 * @param cardType
		 *            the credit card type.
		 */
		public void disableCreditCardType(CardType cardType) {
			cardTypes.remove(cardType);
			checkConsistent();
		}

		/**
		 * Are any credit card types allowed.
		 * 
		 * @return true if credit cards are allowed, false otherwise.
		 */
		public boolean isCreditCardPaymentAllowed() {
			return cardTypes.size() > 0;
		}

		/**
		 * Is direct debit enabled.
		 * 
		 * @return true if a direct debit country is configured, false
		 *         otherwise.
		 */
		public boolean isDirectDebitPaymentAllowed() {
			return this.directDebitCountry != null;
		}

		/**
		 * Enables direct debit for the specific country. Note, this payment
		 * screen only supports one direct debit country!
		 * 
		 * @param country
		 *            ISO 3166-2 formatted country code or null to disable
		 *            direct debit.
		 * @throws IllegalArgumentException
		 *             if you try to disable both credit and direct debit
		 *             payments.
		 */
		public void setDirectDebitCountry(String country) {
			this.directDebitCountry = country;
			checkConsistent();
		}

		/**
		 * Retrieve the currently configured direct debit country.
		 * 
		 * @return the country or null if none is configured.
		 */
		public String getDirectDebitCountry() {
			return this.directDebitCountry;
		}

		/**
		 * Disables direct debit.
		 * 
		 * @throws IllegalArgumentException
		 *             if you try to disable both credit and direct debit
		 *             payments.
		 */
		public void disableDirectDebit() {
			setDirectDebitCountry(null);
		}

		/**
		 * Predefine some of the credit card values, you might have acquired
		 * trough card scanning or similar. The values will pre-fill the
		 * according EditTexts.
		 * 
		 * @param accountholder
		 * @param cardnumber
		 * @param expiryMonth
		 * @param expiryYear
		 * @param verification
		 */
		public void setCreditCardPredefinedData(String accountholder,
				String cardnumber, String expiryMonth, String expiryYear,
				String verification) {
			this.accountHolder = accountholder;
			this.cardnumber = cardnumber;
			this.expiryMonth = expiryMonth;
			this.expiryYear = expiryYear;
			this.verification = verification;
		}

		/**
		 * Predefine some of the direct debit values. The values will pre-fill
		 * the according EditTexts.
		 * 
		 * @param accountHolder
		 * @param accountNumber
		 * @param bankNumber
		 */
		public void setDirectDebitPredefinedData(String accountHolder,
				String accountNumber, String bankNumber) {
			this.accountHolder = accountHolder;
			this.accountNumber = accountNumber;
			this.bankNumber = bankNumber;
		}

		public String getAccountHolder() {
			return accountHolder;
		}

		public String getCardNumber() {
			return cardnumber;
		}

		public String getExpiryMonth() {
			return expiryMonth;
		}

		public String getExpiryYear() {
			return expiryYear;
		}

		public String getVerification() {
			return verification;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public String getBankNumber() {
			return bankNumber;
		}

		public HashSet<CardType> getCardTypes() {
			return cardTypes;
		}

		public Settings(Parcel source) {
			ArrayList<CardType> tempList = new ArrayList<CardType>();
			source.readList(tempList, null);
			cardTypes.addAll(tempList);
			directDebitCountry = source.readString();
			accountHolder = source.readString();
			cardnumber = source.readString();
			expiryMonth = source.readString();
			expiryYear = source.readString();
			verification = source.readString();
			accountNumber = source.readString();
			bankNumber = source.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ArrayList<CardType> tempList = new ArrayList<CardType>();
			tempList.addAll(cardTypes);
			dest.writeList(tempList);
			dest.writeString(directDebitCountry);
			dest.writeString(accountHolder);
			dest.writeString(cardnumber);
			dest.writeString(expiryMonth);
			dest.writeString(expiryYear);
			dest.writeString(verification);
			dest.writeString(accountNumber);
			dest.writeString(bankNumber);

		}

		public static final Parcelable.Creator<Settings> CREATOR = new Parcelable.Creator<Settings>() {

			@Override
			public Settings createFromParcel(Parcel source) {
				return new Settings(source);
			}

			@Override
			public Settings[] newArray(int size) {
				return new Settings[size];
			}
		};

		private void checkValidCardType(CardType cardType) {
			if (cardType == CardType.Invalid || cardType == CardType.YetUnknown) {
				throw new IllegalArgumentException(cardType
						+ " cannot be enabled!");
			}
		}

		private void checkConsistent() {
			if (!isCreditCardPaymentAllowed() && !isDirectDebitPaymentAllowed()) {
				throw new IllegalArgumentException(
						"Invalid settings, either direct debit or credit card must be enabled");
			}
		}

	}
}
