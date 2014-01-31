package com.paymill.android.payment;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paymill.android.factory.PMFactory;
import com.paymill.android.factory.PMPaymentMethod;
import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.R;

public class DirectDebitFragment extends Fragment {

	Button triggerButton;
	EditText accountHolder;
	EditText accountNumber;
	EditText bankNumber;
	String directDebitCountry;
	Settings pmSettings;

	private static final List<Integer> SPACES_POSITIONS_DEFAULT = Arrays
			.asList(4, 8, 12, 16, 20, 24, 28, 32);
	private static final List<Integer> MARKERS_POSITIONS_DEFAULT = Arrays
			.asList(5, 10, 15, 20, 25, 30, 35, 40);
	private static final int MAX_IBAN_WITH_SPACES = 42;
	private static final int MAX_BIC = 11;
	public static final String FRAGMENT_TAG = "DD";

	public static DirectDebitFragment instance(Settings settings) {
		DirectDebitFragment fragment = new DirectDebitFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(PaymentActivity.ARGUMENT_SETTINGS, settings);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pmSettings = getArguments().getParcelable(
				PaymentActivity.ARGUMENT_SETTINGS);
		directDebitCountry = pmSettings.getDirectDebitCountry();

		final View v = inflater.inflate(R.layout.pm_direct_debit_fragment,
				container, false);

		accountHolder = (EditText) v.findViewById(R.id.nameText);
		accountNumber = (EditText) v.findViewById(R.id.accountNumberText);
		bankNumber = ((EditText) v.findViewById(R.id.bankCodeText));
		triggerButton = (Button) v.findViewById(R.id.elv_trigger_btn);
		triggerButton.setText(((PaymentActivity) getActivity())
				.getTriggerButtonText());
		triggerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (IbanBicValidator.validate(getActivity(), accountHolder,
						accountNumber, bankNumber)) {
					PMPaymentMethod ibanBic = PMFactory.genIbanBicPayment(
							accountHolder.getText().toString(),
							accountNumber.getText().toString()
									.replaceAll("\\s", ""), bankNumber
									.getText().toString());
					triggerButton.setEnabled(false);
					((PaymentActivity) getActivity()).startRequest(ibanBic);
				}
			}

		});
		// Load predefined data
		if (pmSettings.getAccountNumber() != null) {
			((EditText) v.findViewById(R.id.accountNumberText))
					.setText(pmSettings.getAccountNumber());
		}

		if (pmSettings.getBankNumber() != null) {
			((EditText) v.findViewById(R.id.bankCodeText)).setText(pmSettings
					.getBankNumber());
		}

		if (pmSettings.getAccountHolder() != null) {
			((EditText) v.findViewById(R.id.nameText)).setText(pmSettings
					.getAccountHolder());
		}
		InputFilter[] filtersIban = new InputFilter[1];
		filtersIban[0] = new InputFilter.LengthFilter(MAX_IBAN_WITH_SPACES);
		accountNumber.setFilters(filtersIban);
		InputFilter[] filtersBic = new InputFilter[1];
		filtersBic[0] = new InputFilter.LengthFilter(MAX_BIC);
		bankNumber.setFilters(filtersBic);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		accountNumber.addTextChangedListener(ibanTextWatcher);
	}

	@Override
	public void onPause() {
		super.onPause();
		accountNumber.removeTextChangedListener(ibanTextWatcher);
	};
	
	TextWatcher ibanTextWatcher = new TextWatcher() {

		int ibanLength = 0;
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
			accountNumber.setTextColor(getResources().getColor(
					R.color.defaultTextColor));
			String ibanNumber = accountNumber.getText().toString();
			ibanNumber = showCCardWithIntervals(ibanNumber
					.replaceAll("\\s", ""));
			// we use isDelete in order to know if the cursor is moving forward
			// or backwards
			if (ibanLength < ibanNumber.length()) {
				isDelete = false;
			} else {
				isDelete = true;
			}
			accountNumber.removeTextChangedListener(this);
			int start = getCCardMarkerPosition(
					accountNumber.getSelectionStart(), isDelete);
			accountNumber.setText(ibanNumber);
			accountNumber.setSelection(start < ibanNumber.length() ? start
					: ibanNumber.length());
			accountNumber.addTextChangedListener(this);

			ibanLength = ibanNumber.length();
		}

		int getCCardMarkerPosition(int oldPosition, boolean isDelete) {
			int newPosition = oldPosition;

			if (MARKERS_POSITIONS_DEFAULT.contains(oldPosition))
				if (isDelete)
					return newPosition--;
				else
					newPosition++;
			return newPosition++;
		}

		String showCCardWithIntervals(String noIntervalsString) {
			String result = "";
			int index = 1;
			for (char c : noIntervalsString.toCharArray()) {
				result += c;
				if (SPACES_POSITIONS_DEFAULT.contains(index))
					result += " ";
				index++;
			}
			return result;
		}
	};
}
