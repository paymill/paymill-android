package com.paymill.android.samples.vouchermill.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

public class VoucherProgressFragment extends DialogFragment {

	public static final String TAG="voucher_progress";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		// Disable the back button
		OnKeyListener keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		};
		dialog.setOnKeyListener(keyListener);
		return dialog;
	}
	public static void dismiss(FragmentManager fm) {
		VoucherProgressFragment progress=((VoucherProgressFragment) fm.findFragmentByTag(
				TAG));
		if (progress!=null) {
			progress.dismiss();
		}
	}
}