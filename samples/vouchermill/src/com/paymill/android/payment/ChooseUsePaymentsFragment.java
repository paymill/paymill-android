package com.paymill.android.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paymill.android.samples.vouchermill.R;

public class ChooseUsePaymentsFragment extends Fragment {

	protected static final String FRAGMENT_TAG = "ChooseUsePaymentsFragment";

	private EditText passwordView;

	public static ChooseUsePaymentsFragment instance() {
		ChooseUsePaymentsFragment fragment = new ChooseUsePaymentsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pm_choose_use_payments_fragment,
				container, false);
		Button yesButton = (Button) v.findViewById(R.id.yesUsePayments);
		Button noButton = (Button) v.findViewById(R.id.noUsePayments);
		Button resetButton = (Button) v.findViewById(R.id.resetUsePayments);

		passwordView = (EditText) v.findViewById(R.id.savedPaymentsPin);
		if (((PaymentActivity)getActivity()).password != null) {
			passwordView.setVisibility(View.GONE);
		}

		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String passwordString = passwordView.getText().toString();
				if (((PaymentActivity)getActivity()).password == null) {
					((PaymentActivity)getActivity()).password = passwordString;
				}
				((PaymentActivity) getActivity()).loadPaymentsList();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((PaymentActivity) getActivity()).addNewPaymentFragment();
			}
		});

		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((PaymentActivity)getActivity()).password = null;
				((PaymentActivity) getActivity()).resetSafeStorePayments();
			}
		});
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((PaymentActivity) getActivity()).addNewPaymentFragment();
			}
		});
		return v;
	}
}
