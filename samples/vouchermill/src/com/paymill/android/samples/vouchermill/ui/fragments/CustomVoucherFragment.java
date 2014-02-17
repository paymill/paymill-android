package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.factory.PMFactory;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.Voucher;

public class CustomVoucherFragment extends SherlockFragment implements
		VoucherInfo {

	private EditText amountEditText;
	private EditText currencyEditText;
	private EditText descriptionEditText;
	private static final int MAKE_CENTS = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.custom_voucher_fragment, container,
				false);

		ImageView iv = (ImageView) v.findViewById(R.id.voucherImage);
		iv.setImageResource(Voucher.Type.CustomVoucher.getImageId());

		amountEditText = (EditText) v.findViewById(R.id.amountEditText);
		currencyEditText = (EditText) v.findViewById(R.id.currencyEditText);
		descriptionEditText = (EditText) v
				.findViewById(R.id.descriptionEditText);
		return v;
	}

	@Override
	public PMPaymentParams getInfo() throws VoucherInfoValidationException {
		boolean valid = true;
		if (TextUtils.isEmpty(currencyEditText.getText())) {
			currencyEditText.setError(getActivity().getString(R.string.pm_emptyMSG));
			valid = false;
		}
		if (TextUtils.isEmpty(amountEditText.getText())
				|| !TextUtils.isDigitsOnly(amountEditText.getText())) {
			amountEditText.setError(getActivity().getString(R.string.pm_emptyMSG));
			valid = false;
		}
		if (TextUtils.isEmpty(descriptionEditText.getText())) {
			descriptionEditText.setError(getActivity().getString(R.string.pm_emptyMSG));
			valid = false;
		}
		if (!valid) {
			throw new VoucherInfoValidationException();
		} else {
			return PMFactory.genPaymentParams(currencyEditText.getText()
					.toString(),
					Integer.parseInt(amountEditText.getText().toString())
							* MAKE_CENTS, descriptionEditText.getText()
							.toString());
		}
	}
}
