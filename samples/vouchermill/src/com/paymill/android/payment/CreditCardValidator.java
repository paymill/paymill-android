package com.paymill.android.payment;

import java.util.Calendar;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.paymill.android.payment.CardTypeParser.CardType;
import com.paymill.android.samples.vouchermill.R;

@SuppressLint("ResourceAsColor")
public class CreditCardValidator {

	private static final String TAG = "CreditCardValidator";
	static boolean valid;

	private static boolean validateIsEmpty(String field) {
		return TextUtils.isEmpty(field);
	}

	public static boolean validate(Context context, EditText name,
			EditText creditCardNumber, EditText dateText, EditText checkNumber,
			CardType cardType, Collection<CardType> allowedCardTypes) {
		valid = true;
		valid = validateName(context, name) && valid;
		valid = validateCreditCardNumber(context, creditCardNumber, cardType,
				allowedCardTypes) && valid;
		valid = validateDate(context, dateText) && valid;
		valid = validateCheckNumber(context, checkNumber, cardType) && valid;
		return valid;
	}

	public static boolean validateName(Context context, EditText name) {
		if (validateIsEmpty(name.getText().toString())) {
			name.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		return true;
	}

	public static boolean isValidCardType(Context context,
			EditText creditCardNumber, CardType cardType) {
		if (cardType == CardType.Invalid) {
			Log.d(TAG, cardType.name());
			creditCardNumber.setTextColor(context.getResources().getColor(
					R.color.errorTextColor));
			return false;
		}
		return true;
	}

	public static boolean validateCreditCardNumber(Context context,
			EditText creditCardNumber, CardType cardType,
			Collection<CardType> allowedCardTypes) {
		creditCardNumber.setError(null);
		if (validateIsEmpty(creditCardNumber.getText().toString())) {
			creditCardNumber.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		String ccnumber = creditCardNumber.getText().toString();
		ccnumber = ccnumber.replaceAll("\\s", "");

		if (cardType.getMinLength() == cardType.getMaxLength()
				&& cardType.getMaxLength() != ccnumber.length()) {
			creditCardNumber.setTextColor(context.getResources().getColor(
					R.color.errorTextColor));
			return false;
		}
		if (ccnumber.length() < cardType.getMinLength()
				|| ccnumber.length() > cardType.getMaxLength()) {
			creditCardNumber.setTextColor(context.getResources().getColor(
					R.color.errorTextColor));
			return false;
		}

		if (!allowedCardTypes.contains(cardType)) {
			creditCardNumber.setTextColor(context.getResources().getColor(
					R.color.errorTextColor));
			creditCardNumber.setError(context
					.getString(R.string.pm_notAllowedCardMSG));
			return false;
		}

		if (cardType.isLuhn() && !luhnCheck(ccnumber)) {
			creditCardNumber.setTextColor(context.getResources().getColor(
					R.color.errorTextColor));
			return false;
		}

		return true;
	}

	public static boolean validateDateFormat(String date) {
		String dateRegEx = "((^0(?![^1-9])[1-9]?)|(^1(?![^012])[012]?))(?![^\\/])\\/?[0-9]{0,2}$";
		Pattern datePattern = Pattern.compile(dateRegEx);
		Matcher matcher;
		matcher = datePattern.matcher(date);
		boolean find = matcher.find();
		Log.d(TAG, Boolean.toString(find));
		return find;
	}

	public static boolean validateDate(Context context, EditText dateText) {
		dateText.setError(null);
		if (validateIsEmpty(dateText.getText().toString())) {
			dateText.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		String date = dateText.getText().toString();
		// date is validated only after we have the whole 5 symbols in format:
		// MM/YY which is guaranteed with regEx check in creditCardFragment
		// before this one
		if (date.length() > 4) {

			String month = date.substring(0, 2);
			String year = date.substring(3, 5);

			boolean notNumber = false;
			Calendar now = Calendar.getInstance();
			Calendar ccdate = Calendar.getInstance();
			try {
				ccdate.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			} catch (NumberFormatException e) {
				dateText.setError(context.getString(R.string.pm_emptyMSG));
				notNumber = true;
			}
			try {
				ccdate.set(Calendar.YEAR, Integer.parseInt(year)
						+ CreditCardFragment.milleniumPrefix * 1000
						+ CreditCardFragment.centuryPrefix * 100);
			} catch (NumberFormatException e) {
				dateText.setError(context.getString(R.string.pm_emptyMSG));
				notNumber = true;
			}
			if (notNumber) {
				return false;
			}
			// CCs are valid until the end of the month
			ccdate.set(Calendar.DAY_OF_MONTH,
					ccdate.getActualMaximum(Calendar.DAY_OF_MONTH));
			ccdate.set(Calendar.HOUR_OF_DAY, 23);
			ccdate.set(Calendar.MINUTE, 59);
			if (ccdate.before(now)) {
				dateText.setTextColor(context.getResources().getColor(
						R.color.errorTextColor));
				return false;
			}
		}
		return true;
	}

	public static boolean validateCheckNumber(Context context,
			EditText checkNumber, CardType type) {
		checkNumber.setError(null);
		if (validateIsEmpty(checkNumber.getText().toString())) {
			checkNumber.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		if (type == CardType.AmericanExpress) {
			if (!(checkNumber.length() == CardType.AmericanExpress
					.getCVCLength() || checkNumber.length() == CardType.AmericanExpress
					.getCVCLength() - 1)) {
				checkNumber.setTextColor(context.getResources().getColor(
						R.color.errorTextColor));
				return false;
			}
		} else {
			if (checkNumber.getText().length() != 3) {
				checkNumber.setTextColor(context.getResources().getColor(
						R.color.errorTextColor));
				return false;
			}
		}
		return true;
	}

	/**
	 */
	/**
	 * Performs the luhn algorithm. {@link #luhnCheck(long)}
	 * 
	 * @param cardNumber
	 *            the credit card number
	 * @return true if valid credit card number
	 */
	public static boolean luhnCheck(String cardNumber) {
		if (TextUtils.isEmpty(cardNumber)) {
			return false;
		}
		try {
			return luhnCheck(Long.parseLong(cardNumber));
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Performs the luhn algorithm.</br> Source:
	 * http://www.merriampark.com/anatomycc.htm</br> Information:
	 * http://en.wikipedia.org/wiki/Luhn_algorithm
	 * 
	 * @param cardNumber
	 *            a creditcardnumber
	 * @return true if valid, false otherwise
	 */
	public static boolean luhnCheck(long cardNumber) {
		String digitsOnly = String.valueOf(cardNumber);
		int sum = 0;
		int digit = 0;
		int addend = 0;
		boolean timesTwo = false;

		for (int i = digitsOnly.length() - 1; i >= 0; i--) {
			digit = Integer.parseInt(digitsOnly.substring(i, i + 1));
			if (timesTwo) {
				addend = digit * 2;
				if (addend > 9) {
					addend -= 9;
				}
			} else {
				addend = digit;
			}
			sum += addend;
			timesTwo = !timesTwo;
		}

		int modulus = sum % 10;
		return modulus == 0;
	}

}
