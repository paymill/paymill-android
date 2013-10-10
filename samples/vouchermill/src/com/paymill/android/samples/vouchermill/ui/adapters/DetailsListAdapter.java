package com.paymill.android.samples.vouchermill.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.entities.Voucher;

public class DetailsListAdapter extends BaseAdapter {

	private ArrayList<DetailsObject> details;
	private Context context;
	LayoutInflater li;

	public static final String KEY_DESCRIPTION = "desc";
	public static final String KEY_AMOUNT = "amnt";
	public static final String KEY_IMAGE = "img";

	public DetailsListAdapter(Context c, ArrayList<DetailsObject> details) {
		this.details = details;
		this.context = c;
		this.li = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return details.size();
	}

	@Override
	public Object getItem(int position) {
		return details.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position > getCount())
			return null;
		View v = convertView;
		if (convertView == null)
			v = li.inflate(R.layout.list_row, null);

		TextView description = (TextView) v.findViewById(R.id.description);
		TextView amount = (TextView) v.findViewById(R.id.amount);
		ImageView thumb_image = (ImageView) v.findViewById(R.id.list_image);

		Voucher singleVoucher = Voucher.fromDetails(details.get(position));

		description.setText(singleVoucher.getVoucherTextDescription());
		float voucherAmount = ((float) singleVoucher.getVoucherAmount() / 100);
		String voucherAmountAndCurrency = voucherAmount + " "
				+ singleVoucher.getVoucherCurrency();
		amount.setText(voucherAmountAndCurrency);
		thumb_image.setImageResource(singleVoucher.getVoucherImageId());
		return v;
	}

}
