package com.paymill.android.samples.vouchermill.ui.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.Voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StandardVoucherFragment extends SherlockFragment implements
		VoucherInfo {
	private static final String VOUCHER_TYPE_KEY = "TYPE";
	private Voucher.Type voucherType;

	public static StandardVoucherFragment instance(Voucher.Type type) {
		StandardVoucherFragment fragment = new StandardVoucherFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(VOUCHER_TYPE_KEY, type.getId());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getArguments();
		voucherType = Voucher.Type.fromDescription(Integer.toString(bundle
				.getInt(VOUCHER_TYPE_KEY)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.standard_voucher_fragment,
				container, false);

		ImageView iv = (ImageView) v.findViewById(R.id.voucherImage);
		iv.setImageResource(voucherType.getImageId());

		TextView amount = (TextView) v.findViewById(R.id.amountText);
		amount.setText(Double.toString(voucherType.getAmount() / 100));

		TextView currency = (TextView) v.findViewById(R.id.currencyText);
		currency.setText(voucherType.getCurrency());

		TextView description = (TextView) v.findViewById(R.id.descriptionText);
		description.setText(voucherType.getDescription());
		return v;
	}

	@Override
	public PMPaymentParams getInfo() throws VoucherInfoValidationException {
		return voucherType.genPaymentParams();
	}
}
