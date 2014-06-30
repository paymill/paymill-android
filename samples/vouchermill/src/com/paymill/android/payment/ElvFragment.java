package com.paymill.android.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paymill.android.factory.PMFactory;
import com.paymill.android.factory.PMPaymentMethod;
import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.R;

public class ElvFragment extends Fragment {

	Button triggerButton;
	EditText accountHolder;
	EditText accountNumber;
	EditText bankNumber;
	String directDebitCountry;
	Settings pmSettings;

	public static final String FRAGMENT_TAG = "ELV";

	public static ElvFragment instance(Settings settings) {
		ElvFragment fragment = new ElvFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(PaymentActivity.ARGUMENT_SETTINGS, settings);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pmSettings = getArguments().getParcelable(
				PaymentActivity.ARGUMENT_SETTINGS);
		directDebitCountry = pmSettings.getDirectDebitCountry();

		final View v = inflater.inflate(R.layout.pm_elv_fragment, container,
				false);

		accountHolder = (EditText) v.findViewById(R.id.nameText);
		accountNumber = (EditText) v.findViewById(R.id.accountNumberText);
		bankNumber = ((EditText) v.findViewById(R.id.bankCodeText));
		triggerButton = (Button) v.findViewById(R.id.elv_trigger_btn);
		triggerButton.setText(((PaymentActivity) getActivity())
				.getTriggerButtonText());
		triggerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ElvValidator.validate(getActivity(), accountHolder,
						accountNumber, bankNumber)) {
					PMPaymentMethod ELV = PMFactory.genDirectDebitPayment(
							accountHolder.getText().toString(), accountNumber
									.getText().toString(), bankNumber.getText()
									.toString(), directDebitCountry);
					triggerButton.setEnabled(false);
					((PaymentActivity) getActivity()).startRequest(ELV);
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

		return v;
	}
}
