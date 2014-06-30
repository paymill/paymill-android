package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.payment.CardTypeParser.CardType;
import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.helpers.SettingsHelper;
import com.paymill.android.samples.vouchermill.util.Constants;

public class SettingsFragment extends SherlockFragment {

	private static Settings settings;
	View view;
	CheckBox autoConsumeCheckBox;
	CheckBox useSafeStoreCheckBox;
	RadioGroup countriesRadioGroup;
	RadioButton germanyRadioBtn;
	CheckBox paymentMethodCCCheckBox;
	CheckBox paymentMethodDDCheckBox;
	TextView cardTypesLabel;
	CheckBox cardTypeVisaCheckBox;
	CheckBox cardTypeMasterCardCheckBox;
	CheckBox cardTypeMaestroCheckBox;
	CheckBox cardTypeAmericanExpressCheckBox;
	CheckBox cardTypeJCBCheckBox;
	CheckBox cardTypeDinersClubCheckBox;
	CheckBox cardTypeDiscoverCheckBox;
	CheckBox cardTypeUnionPayCheckBox;
	CheckBox cardTypeInstaPaymentCheckBox;
	CheckBox cardTypeLaserCheckBox;
	TextView directDebitCountryLabel;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		settings = SettingsHelper.getInstance(getActivity()).getSettings();

		view = inflater.inflate(R.layout.settings_fragment, container, false);

		// Initialize fields
		autoConsumeCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxAutoConsume));
		useSafeStoreCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxUseSafeStore));
		germanyRadioBtn = ((RadioButton) view
				.findViewById(R.id.radioButtonGermany));
		countriesRadioGroup = ((RadioGroup) view
				.findViewById(R.id.countriesRadioGroup));

		cardTypesLabel = ((TextView) view.findViewById(R.id.cardTypesLabel));
		directDebitCountryLabel = ((TextView) view
				.findViewById(R.id.directDebitCountryLabel));

		paymentMethodCCCheckBox = ((CheckBox) view
				.findViewById(R.id.checkBoxCreditCard));
		paymentMethodDDCheckBox = ((CheckBox) view
				.findViewById(R.id.checkBoxDirectDebit));

		cardTypeVisaCheckBox = ((CheckBox) view.findViewById(R.id.checkboxVisa));
		cardTypeMasterCardCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxMasterCard));
		cardTypeMaestroCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxMaestro));
		cardTypeAmericanExpressCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxAmericanExpress));
		cardTypeJCBCheckBox = ((CheckBox) view.findViewById(R.id.checkboxJCB));
		cardTypeDinersClubCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxDinersClub));
		cardTypeDiscoverCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxDiscover));
		cardTypeUnionPayCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxUnionPay));
		cardTypeInstaPaymentCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxInstaPayment));
		cardTypeLaserCheckBox = ((CheckBox) view
				.findViewById(R.id.checkboxLaser));

		// Read the settings and set the check boxes and radio buttons
		// accordingly and hide country or card fields if needed

		if (!settings.isCreditCardPaymentAllowed()) {
			paymentMethodCCCheckBox.setChecked(false);
			disableCardFields(view);
		}

		if (!settings.isDirectDebitPaymentAllowed()) {
			paymentMethodDDCheckBox.setChecked(false);
			disableCountryFields(view);
		}
		if (!settings.isSafeStoreEnabled()) {
			useSafeStoreCheckBox.setChecked(false);
		}
		autoConsumeCheckBox.setChecked(SettingsHelper
				.getInstance(getActivity()).isAutoConsume());
		autoConsumeCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SettingsHelper.getInstance(getActivity())
								.setAutoConsume(isChecked);
					}
				});
		paymentMethodCCCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							enableCardFields(view);
						} else {
							disableCardFields(view);
							if (!paymentMethodDDCheckBox.isChecked()) {
								paymentMethodDDCheckBox.setChecked(true);
							}
						}
					}
				});

		paymentMethodDDCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							enableCountryFields(view);
						} else {
							disableCountryFields(view);
							if (!paymentMethodCCCheckBox.isChecked()) {
								paymentMethodCCCheckBox.setChecked(true);
								Log.d("test", "1 - "
										+ settings.getCardTypes().size());
							}
						}
					}
				});

		// set the checkboxes - all are checked in xml by default(disable all
		// that are not in the current settings)
		for (CardType card : CardType.values()) {
			if (!settings.getAllowedCardTypes().contains(card)) {
				switch (card) {
				case Visa: {
					cardTypeVisaCheckBox.setChecked(false);
					break;
				}
				case MasterCard: {
					cardTypeMasterCardCheckBox.setChecked(false);
					break;
				}
				case Maestro: {
					cardTypeMaestroCheckBox.setChecked(false);
					break;
				}
				case AmericanExpress: {
					cardTypeAmericanExpressCheckBox.setChecked(false);
					break;
				}
				case JCB: {
					cardTypeJCBCheckBox.setChecked(false);
					break;
				}
				case DinersClub: {
					cardTypeDinersClubCheckBox.setChecked(false);
					break;
				}
				case Discover: {
					cardTypeDiscoverCheckBox.setChecked(false);
					break;
				}
				case UnionPay: {
					cardTypeUnionPayCheckBox.setChecked(false);
					break;
				}
				case InstaPayment: {
					cardTypeInstaPaymentCheckBox.setChecked(false);
					break;
				}
				case Laser: {
					cardTypeLaserCheckBox.setChecked(false);
					break;
				}
				default: {
					break;
				}
				}
			}
		}

		if (settings.getDirectDebitCountry() != null
				&& settings.getDirectDebitCountry().equals(
						Constants.DIRECT_DEBIT_COUNTRY_GERMANY)) {
			germanyRadioBtn.setChecked(true);
		}

		return view;
	}

	@Override
	public void onPause() {
		// Save all the settings

		// Card Types
		// If all credit cards are disabled is credit card allowed will return
		// false. (if credit card checkbox is false all credit card types are
		// set to false automatically)
		if (cardTypeVisaCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.Visa);
		} else {
			settings.disableCreditCardType(CardType.Visa);
		}

		if (cardTypeMasterCardCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.MasterCard);
		} else {
			settings.disableCreditCardType(CardType.MasterCard);
		}

		if (cardTypeMaestroCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.Maestro);
		} else {
			settings.disableCreditCardType(CardType.Maestro);
		}

		if (cardTypeAmericanExpressCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.AmericanExpress);
		} else {
			settings.disableCreditCardType(CardType.AmericanExpress);
		}

		if (cardTypeJCBCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.JCB);
		} else {
			settings.disableCreditCardType(CardType.JCB);
		}

		if (cardTypeDinersClubCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.DinersClub);
		} else {
			settings.disableCreditCardType(CardType.DinersClub);
		}

		if (cardTypeDiscoverCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.Discover);
		} else {
			settings.disableCreditCardType(CardType.Discover);
		}

		if (cardTypeUnionPayCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.UnionPay);
		} else {
			settings.disableCreditCardType(CardType.UnionPay);
		}

		if (cardTypeInstaPaymentCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.InstaPayment);
		} else {
			settings.disableCreditCardType(CardType.InstaPayment);
		}

		if (cardTypeLaserCheckBox.isChecked()) {
			settings.enableCreditCardType(CardType.Laser);
		} else {
			settings.disableCreditCardType(CardType.Laser);
		}
		
		settings.setSafeStoreEnabled(useSafeStoreCheckBox.isChecked());
		
		if (!cardTypeVisaCheckBox.isChecked()
				&& !cardTypeMasterCardCheckBox.isChecked()
				&& !cardTypeMaestroCheckBox.isChecked()
				&& !cardTypeAmericanExpressCheckBox.isChecked()
				&& !cardTypeJCBCheckBox.isChecked()
				&& !cardTypeDinersClubCheckBox.isChecked()
				&& !cardTypeDiscoverCheckBox.isChecked()
				&& !cardTypeUnionPayCheckBox.isChecked()
				&& !cardTypeInstaPaymentCheckBox.isChecked()
				&& !cardTypeLaserCheckBox.isChecked()) {
			disableCardFields(view);
			if (!paymentMethodDDCheckBox.isChecked()) {
				paymentMethodDDCheckBox.setChecked(true);
			}
		}

		// Payment Method
		// Is credit card enabled is saved based on the card fields state
		// Is direct debit enabled is saved based on its checkbox
		if (paymentMethodDDCheckBox.isChecked() && germanyRadioBtn.isChecked()) {
			settings.setDirectDebitCountry("DE");
		} else {
			settings.disableDirectDebit();
		}

		SettingsHelper.getInstance(getActivity()).setSettings(settings);
		super.onPause();
	}

	public static Settings getSettings() {
		return settings;
	}

	// Sets country fields to visible
	private void enableCountryFields(View view) {
		directDebitCountryLabel.setVisibility(View.VISIBLE);
		countriesRadioGroup.setVisibility(View.VISIBLE);
		germanyRadioBtn.setVisibility(View.VISIBLE);
	}

	// Sets country fields to not visible
	private void disableCountryFields(View view) {
		directDebitCountryLabel.setVisibility(View.GONE);
		countriesRadioGroup.setVisibility(View.GONE);
		germanyRadioBtn.setVisibility(View.GONE);
	}

	// Sets all card types to visible and checks them
	private void enableCardFields(View view) {
		cardTypesLabel.setVisibility(View.VISIBLE);
		cardTypeVisaCheckBox.setVisibility(View.VISIBLE);
		cardTypeVisaCheckBox.setChecked(true);
		cardTypeMasterCardCheckBox.setVisibility(View.VISIBLE);
		cardTypeMasterCardCheckBox.setChecked(true);
		cardTypeMaestroCheckBox.setVisibility(View.VISIBLE);
		cardTypeMaestroCheckBox.setChecked(true);
		cardTypeAmericanExpressCheckBox.setVisibility(View.VISIBLE);
		cardTypeAmericanExpressCheckBox.setChecked(true);
		cardTypeJCBCheckBox.setVisibility(View.VISIBLE);
		cardTypeJCBCheckBox.setChecked(true);
		cardTypeDinersClubCheckBox.setVisibility(View.VISIBLE);
		cardTypeDinersClubCheckBox.setChecked(true);
		cardTypeDiscoverCheckBox.setVisibility(View.VISIBLE);
		cardTypeDiscoverCheckBox.setChecked(true);
		cardTypeUnionPayCheckBox.setVisibility(View.VISIBLE);
		cardTypeUnionPayCheckBox.setChecked(true);
		cardTypeInstaPaymentCheckBox.setVisibility(View.VISIBLE);
		cardTypeInstaPaymentCheckBox.setChecked(true);
		cardTypeLaserCheckBox.setVisibility(View.VISIBLE);
		cardTypeLaserCheckBox.setChecked(true);
	}

	// Sets all card types to not visible and unchecks them
	// so is credit card payment allowed will return false
	private void disableCardFields(View view) {
		cardTypesLabel.setVisibility(View.GONE);
		cardTypeVisaCheckBox.setVisibility(View.GONE);
		cardTypeVisaCheckBox.setChecked(false);
		cardTypeMasterCardCheckBox.setVisibility(View.GONE);
		cardTypeMasterCardCheckBox.setChecked(false);
		cardTypeMaestroCheckBox.setVisibility(View.GONE);
		cardTypeMaestroCheckBox.setChecked(false);
		cardTypeAmericanExpressCheckBox.setVisibility(View.GONE);
		cardTypeAmericanExpressCheckBox.setChecked(false);
		cardTypeJCBCheckBox.setVisibility(View.GONE);
		cardTypeJCBCheckBox.setChecked(false);
		cardTypeDinersClubCheckBox.setVisibility(View.GONE);
		cardTypeDinersClubCheckBox.setChecked(false);
		cardTypeDiscoverCheckBox.setVisibility(View.GONE);
		cardTypeDiscoverCheckBox.setChecked(false);
		cardTypeUnionPayCheckBox.setVisibility(View.GONE);
		cardTypeUnionPayCheckBox.setChecked(false);
		cardTypeInstaPaymentCheckBox.setVisibility(View.GONE);
		cardTypeInstaPaymentCheckBox.setChecked(false);
		cardTypeLaserCheckBox.setVisibility(View.GONE);
		cardTypeLaserCheckBox.setChecked(false);
	}
}