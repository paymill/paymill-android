package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.entities.Voucher.Type;

public class VoucherListDetailsFragment extends VoucherDetailsFragment {

	@Override
	public VoucherListDetailsFragment getInstance(DetailsObject details) {
		VoucherListDetailsFragment fragment = new VoucherListDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(DetailsObject.DETAILS_KEY, details);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		Bundle arguments = getArguments();
		DetailsObject details = arguments
				.getParcelable(DetailsObject.DETAILS_KEY);

		ImageView imageView = new ImageView(getActivity());
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT));

		imageView.setImageResource((Type.fromDescription(details
				.getDescription())).getImageId());

		LinearLayout contentHolder = (LinearLayout) view
				.findViewById(R.id.contentHolderLayout);
		contentHolder.addView(imageView);
		contentHolder.invalidate();
		contentHolder.requestLayout();

		return view;
	}
}
