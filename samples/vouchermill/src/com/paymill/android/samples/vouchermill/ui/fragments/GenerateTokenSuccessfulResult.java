package com.paymill.android.samples.vouchermill.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.R;

public class GenerateTokenSuccessfulResult extends SherlockFragment {

	private static final String RESULT_KEY = "result";

	private String token;

	public static GenerateTokenSuccessfulResult instance(String result) {
		GenerateTokenSuccessfulResult fragment = new GenerateTokenSuccessfulResult();
		Bundle bundle = new Bundle();
		bundle.putString(RESULT_KEY, result);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		token = getArguments().getString(RESULT_KEY);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.generate_token_result_succ,
				container, false);

		((TextView) v.findViewById(R.id.tokenId)).setText(token);
		return v;
	}
}
