package com.paymill.android.samples.vouchermill.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paymill.android.samples.vouchermill.R;

public class DashboardAdapter extends BaseAdapter {
	private Context context;
	LayoutInflater li;

	public DashboardAdapter(Context c) {
		this.context = c;
		this.li = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return menuItemImages.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;

		if (convertView == null) {
			v = li.inflate(R.layout.dashboard_cell, parent, false);
		} else {
			v = convertView;
		}

		ImageView imageView = (ImageView) v
				.findViewById(R.id.menu_cell_imageview);
		imageView.setImageResource(menuItemImages[position]);
		TextView titleView = (TextView) v.findViewById(R.id.title_view);
		titleView.setText(menuItemTexts[position]);
		return v;

	}

	private Integer[] menuItemImages = { R.drawable.buy_voucher,
			R.drawable.online_vouchers, R.drawable.offline_vouchers,
			R.drawable.not_consumed };
	private String[] menuItemTexts = { "Buy Voucher", "Online Vouchers",
			"Offline Vouchers", "Not Consumed" };

}
