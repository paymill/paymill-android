package com.paymill.android.payment;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.paymill.android.samples.vouchermill.R;

public class IbanBicValidator {

	private static boolean validateIsEmpty(String field) {
		return TextUtils.isEmpty(field);
	}

	private static final int IBAN_MIN_SIZE = 15;
	private static final int IBAN_MAX_SIZE = 34;
	private static final long IBAN_MAX = 999999999;
	private static final long IBAN_MODULUS = 97;

	public static boolean validateIban(String iban) {
		String trimmed = iban.trim();
		if (trimmed.length() < IBAN_MIN_SIZE
				|| trimmed.length() > IBAN_MAX_SIZE) {
			return false;
		}
		String reformat = trimmed.substring(4) + trimmed.substring(0, 4);
		long total = 0;
		for (int i = 0; i < reformat.length(); i++) {
			int charValue = Character.getNumericValue(reformat.charAt(i));
			if (charValue < 0 || charValue > 35) {
				return false;
			}
			total = (charValue > 9 ? total * 100 : total * 10) + charValue;
			if (total > IBAN_MAX) {
				total = (total % IBAN_MODULUS);
			}
		}
		return (total % IBAN_MODULUS) == 1;
	}

	private static boolean validateBic(String bic) {
		if (bic == null) {
			return false;
		} else {
			return bic.length() == 7 || bic.length() == 11;
		}
	}

	public static boolean validate(Context context, EditText name,
			EditText iban, EditText bic) {
		boolean valid = true;
		valid = validateName(context, name) && valid;
		valid = validateIban(context, iban) && valid;
		valid = validateBic(context, bic) && valid;
		return valid;
	}

	public static boolean validateName(Context context, EditText name) {
		name.setError(null);
		if (validateIsEmpty(name.getText().toString())) {
			name.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		return true;
	}

	public static boolean validateIban(Context context, EditText iban) {
		iban.setError(null);
		if (!validateIban(iban.getText().toString().replaceAll("\\s", ""))) {
			iban.setError(context.getString(R.string.pm_invalid_ibanMSG));
			return false;
		}
		return true;
	}

	public static boolean validateBic(Context context, EditText bic) {
		bic.setError(null);
		if (!validateBic(bic.getText().toString())) {
			bic.setError(context.getString(R.string.pm_invalid_bicMSG));
			return false;
		}
		return true;
	}
}
