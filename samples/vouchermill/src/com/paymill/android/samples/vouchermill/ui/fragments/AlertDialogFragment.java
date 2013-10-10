package com.paymill.android.samples.vouchermill.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {

	public static final String TAG ="alert_dialog";
	private static final String TITLE_KEY = "title";
	private static final String BODY_KEY = "body";

	public static AlertDialogFragment newInstance(String title, String body) {
		AlertDialogFragment fragment = new AlertDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString(TITLE_KEY, title);
		bundle.putString(BODY_KEY, body);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setTitle(getArguments().getString(TITLE_KEY));
		alertDialogBuilder.setMessage(getArguments().getString(BODY_KEY));
		alertDialogBuilder.setPositiveButton("OK", null);
		return alertDialogBuilder.create();
	}
}