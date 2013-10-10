package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.payment.PaymentActivity.Result;
import com.paymill.android.payment.PaymentActivity.Result.Type;
import com.paymill.android.samples.vouchermill.R;

public class ErrorResultFragment extends SherlockFragment {

	private TextView errorTypeText;
	private TextView errorMessageText;
	private TextView errorText;
	private Result result;

	private static final String RESULT_KEY = "result";

	public static ErrorResultFragment instance(Result result) {
		ErrorResultFragment fragment = new ErrorResultFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(RESULT_KEY, result);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		result = getArguments().getParcelable(RESULT_KEY);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.result_error_fragment, container, false);

		errorText = (TextView) v.findViewById(R.id.errorText);
		errorText
				.setText(result.getType() == Type.TOKEN ? getString(R.string.error_for_token)
						: getString(R.string.error_for_transactions));
		errorTypeText = (TextView) v.findViewById(R.id.errorTypeText);
		errorTypeText.setText(result.getError().getType().toString());
		errorMessageText = (TextView) v.findViewById(R.id.errorMessageText);
		errorMessageText.setText(result.getError().getMessage());
		return v;
	}
}
