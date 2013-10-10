package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;

public class VoucherResultDetailsFragment extends VoucherDetailsFragment {

	@Override
	public VoucherResultDetailsFragment getInstance(DetailsObject details) {
		VoucherResultDetailsFragment fragment = new VoucherResultDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(DetailsObject.DETAILS_KEY, details);
		fragment.setArguments(bundle);
		return fragment;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		TextView textView = new TextView(getActivity());
		
		textView.setText(getResources().getString(R.string.DetailResultScreenSuccessText));
		textView.setTextColor(getResources().getColor(R.color.White));
		
		//textView.setWidth(LayoutParams.WRAP_CONTENT);
		//textView.setHeight(LayoutParams.MATCH_PARENT);
		
		float density = getResources().getDisplayMetrics().density;
		textView.setWidth((int) density*380);
		textView.setHeight((int) density*120);
		
		textView.setGravity(Gravity.CENTER);
		
		
		textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.success_message_background));
		
		LinearLayout contentHolder = (LinearLayout) view
				.findViewById(R.id.contentHolderLayout);
		textView.invalidate();
		contentHolder.addView(textView);
		contentHolder.invalidate();
		contentHolder.requestLayout();

		return view;
	}
}
