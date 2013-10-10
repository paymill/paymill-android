package com.paymill.android.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.paymill.android.factory.PMFactory;
import com.paymill.android.factory.PMPaymentMethod;
import com.paymill.android.payment.CardTypeParser.CardType;
import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.R;

public class CreditCardFragment extends Fragment {

	protected static final String FRAGMENT_TAG = "CreditCardFragment";
	// we need maximum 6 numbers to recognize a card type
	public static final int MAX_NEEDED_NUMBERS = 6;
	public static final int milleniumPrefix = 2;
	public static final int centuryPrefix = 0;
	PMPaymentMethod CCard;
	CardType cardType = CardType.YetUnknown;
	String firstNumbers;
	Settings pmSettings;

	EditText creditCardNumber;
	ImageView ccImage;
	TextView dateLabel;
	EditText dateText;
	String month;
	String year;
	TextView verificationLabel;
	EditText verification;
	TextView nameLabel;
	EditText name;
	Button triggerButton;

	public static CreditCardFragment instance(Settings settings) {
		CreditCardFragment fragment = new CreditCardFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(PaymentActivity.ARGUMENT_SETTINGS, settings);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		pmSettings = getArguments().getParcelable(
				PaymentActivity.ARGUMENT_SETTINGS);

		View v = inflater.inflate(R.layout.pm_credit_card_fragment, container,
				false);

		creditCardNumber = (EditText) v.findViewById(R.id.cardNumberText);
		ccImage = (ImageView) v.findViewById(R.id.cc_imageview);
		ccImage.setVisibility(View.GONE);
		dateLabel = (TextView) v.findViewById(R.id.dateLabel);
		dateText = (EditText) v.findViewById(R.id.dateText);

		verificationLabel = (TextView) v.findViewById(R.id.verificationLabel);
		verification = (EditText) v.findViewById(R.id.verificationText);
		nameLabel = (TextView) v.findViewById(R.id.nameLabel);
		name = (EditText) v.findViewById(R.id.nameText);
		triggerButton = (Button) v.findViewById(R.id.cc_trigger_btn);

		triggerButton.setText(((PaymentActivity) getActivity())
				.getTriggerButtonText());
		triggerButton.setOnClickListener(sendBtnListener);

		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(4);
		verification.setFilters(filters);

		// Load predefined data
		if (pmSettings.getAccountHolder() != null) {
			name.setText(pmSettings.getAccountHolder());
		}

		if (pmSettings.getCardNumber() != null) {
			creditCardNumber.setText(pmSettings.getCardNumber());
		}

		if (pmSettings.getExpiryMonth() != null
				&& pmSettings.getExpiryYear() != null) {
			dateText.setText(pmSettings.getExpiryMonth() + "/"
					+ pmSettings.getExpiryYear());
		}

		if (pmSettings.getVerification() != null) {
			verification.setText(pmSettings.getVerification());
		}

		return v;
	}

	View.OnClickListener sendBtnListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (creditCardNumber.getText().toString().length() > MAX_NEEDED_NUMBERS)
				cardType = CardTypeParser.CardType
						.getCardType(creditCardNumber.getText().toString(),
								pmSettings.getAllowedCardTypes());

			if (CreditCardValidator.validate(getActivity(), name,
					creditCardNumber, dateText, verification, cardType,
					pmSettings.getAllowedCardTypes())) {
				month = dateText.getText().toString().substring(0, 2);
				year = dateText.getText().toString().substring(3, 5);
				CCard = PMFactory.genCardPayment(
						name.getText().toString(),
						creditCardNumber.getText().toString()
								.replaceAll("\\s", ""),
						month,
						Integer.toString(milleniumPrefix)
								+ Integer.toString(centuryPrefix) + year,
						verification.getText().toString());
				triggerButton.setEnabled(false);
				((PaymentActivity) getActivity()).startRequest(CCard);
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		creditCardNumber.addTextChangedListener(CCTextWatcher);
		dateText.addTextChangedListener(dateTextWatcher);
		verification.addTextChangedListener(validationTextWatcher);

	}

	@Override
	public void onPause() {
		super.onPause();
		creditCardNumber.removeTextChangedListener(CCTextWatcher);
		dateText.removeTextChangedListener(dateTextWatcher);
		verification.removeTextChangedListener(validationTextWatcher);
	};

	TextWatcher CCTextWatcher = new TextWatcher() {

		int ccardLength = 0;
		boolean isDelete = false;

		@Override
		public void onTextChanged(CharSequence s, final int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			creditCardNumber.setTextColor(getResources().getColor(
					R.color.defaultTextColor));
			String ccnumber = creditCardNumber.getText().toString();
			ccnumber = showCCardWithIntervals(ccnumber.replaceAll("\\s", ""),
					cardType);
			// we use isDelete in order to know if the cursor is moving forward
			// or backwards
			if (ccardLength < ccnumber.length())
				isDelete = false;
			else
				isDelete = true;
			// We save the last result numbers, to avoid unnecessary checks
			String firstNumbersSaved = "";

			firstNumbersSaved = ccnumber.substring(0,
					Math.min(MAX_NEEDED_NUMBERS + 1, ccnumber.length()))
					.replaceAll("\\s", "");

			// decode card type and set filter to the edit text
			if (firstNumbers == null || !firstNumbers.equals(firstNumbersSaved)) {
				firstNumbers = firstNumbersSaved;
				cardType = CardTypeParser.CardType.getCardType(firstNumbers,
						pmSettings.getAllowedCardTypes());
				int cardNumberSize = cardType.getMaxLength()
						+ cardType.getNumberOfIntervals();
				creditCardNumber
						.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
								cardNumberSize) });
			}

			// TODO should we really do this each time someone types? isn't it
			// enough to do this when we detect the card type?
			CreditCardValidator.isValidCardType(getActivity(),
					creditCardNumber, cardType);
			if (cardType != CardType.YetUnknown && cardType != CardType.Invalid) {
				ccImage.setVisibility(View.VISIBLE);
				ccImage.setImageResource(cardType.getImageId());
			} else {
				ccImage.setVisibility(View.GONE);
			}

			int ccMaxLen = cardType.getMaxLength()
					+ cardType.getNumberOfIntervals();
			if (ccnumber.length() > ccMaxLen) {
				ccnumber = ccnumber.substring(0, ccMaxLen);
			}
			// TODO END
			// set text and cursor position:
			// should remove the listener to change the text and set it again at
			// the right position
			creditCardNumber.removeTextChangedListener(this);
			int start = getCCardMarkerPosition(
					creditCardNumber.getSelectionStart(), isDelete, cardType);
			creditCardNumber.setText(ccnumber);
			creditCardNumber.setSelection(start < ccnumber.length() ? start
					: ccnumber.length());
			creditCardNumber.addTextChangedListener(this);

			// if the card is valid move to next field
			if (cardType != CardType.Invalid
					&& ccnumber.length() == cardType.getMaxLength()
							+ cardType.getNumberOfIntervals()) {
				if (CreditCardValidator.validateCreditCardNumber(getActivity(),
						creditCardNumber, cardType,
						pmSettings.getAllowedCardTypes())) {
					dateText.requestFocus();
				}
			}

			ccardLength = ccnumber.length();

			if (cardType != CardType.Invalid) {

				InputFilter[] filters = new InputFilter[1];
				filters[0] = new InputFilter.LengthFilter(
						cardType.getCVCLength());
				verification.setFilters(filters);

				// validate CVC input if there is any
				if (verification.length() > 0) {
					CreditCardValidator.validateCheckNumber(getActivity(),
							verification, cardType);
				}
			}
		}

		int getCCardMarkerPosition(int oldPosition, boolean isDelete,
				CardType currentType) {
			int newPosition = oldPosition;

			if (currentType.getMarkersPositions().contains(oldPosition))
				if (isDelete)
					return newPosition--;
				else
					newPosition++;
			return newPosition++;
		}

		String showCCardWithIntervals(String noIntervalsString,
				CardType currentType) {
			String result = "";
			int index = 1;
			for (char c : noIntervalsString.toCharArray()) {
				result += c;
				if (currentType.getSpacesPositions().contains(index))
					result += " ";
				index++;
			}
			return result;
		}
	};

	TextWatcher dateTextWatcher = new TextWatcher() {

		int dateCursor;
		String formatedDate;
		boolean isDateDelete = false;
		int dateLength;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

			dateText.setTextColor(getResources().getColor(
					R.color.defaultTextColor));
			formatedDate = dateText.getText().toString();
			isDateDelete = (dateLength > formatedDate.length());

			dateText.removeTextChangedListener(this);
			dateCursor = dateText.getSelectionStart();
			dateFormated(isDateDelete);
			Log.d(FRAGMENT_TAG, formatedDate + " - " + dateCursor);
			dateText.setText(formatedDate);
			dateText.setSelection(dateCursor);
			dateText.addTextChangedListener(this);

			if (formatedDate.length() > 4) {
				if (CreditCardValidator.validateDateFormat(formatedDate)
						&& CreditCardValidator.validateDate(getActivity(),
								dateText)) {
					verification.requestFocus();

				} else {
					dateText.setTextColor(getResources().getColor(
							R.color.errorTextColor));
				}
			}
			dateLength = formatedDate.length();
		}

		void dateFormated(boolean isDelete) {

			formatedDate = formatedDate.replaceAll("/", "");
			if (formatedDate.length() == 3 && !isDelete && dateCursor == 3)
				dateCursor++;
			if (formatedDate.length() == 2 && isDelete && dateCursor > 1)
				dateCursor--;
			if (formatedDate.length() > 2) {
				formatedDate = formatedDate.substring(0, 2) + "/"
						+ formatedDate.substring(2);
			}
		}
	};

	TextWatcher validationTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			verification.setTextColor(getResources().getColor(
					R.color.defaultTextColor));

			if (cardType == CardType.YetUnknown) {
				return;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	};

}
