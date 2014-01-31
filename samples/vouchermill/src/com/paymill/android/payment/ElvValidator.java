package com.paymill.android.payment;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.paymill.android.samples.vouchermill.R;

public class ElvValidator {


	private static boolean validateIsEmpty(String field) {
		return TextUtils.isEmpty(field);
	}

	public static boolean validate(Context context, EditText name,
			EditText accountNumber, EditText bankNumber) {
		boolean valid = true;
		valid = validateName(context, name) && valid;
		valid = validateAccountNumber(context, accountNumber) && valid;
		valid = validateBankNumber(context, bankNumber) && valid;
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

	public static boolean validateAccountNumber(Context context,
			EditText accountNumber) {
		accountNumber.setError(null);
		if (validateIsEmpty(accountNumber.getText().toString())) {
			accountNumber.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		return true;
	}

	public static boolean validateBankNumber(Context context,
			EditText bankNumber) {
		bankNumber.setError(null);
		if (validateIsEmpty(bankNumber.getText().toString())) {
			bankNumber.setError(context.getString(R.string.pm_emptyMSG));
			return false;
		}
		return true;
	}
}
