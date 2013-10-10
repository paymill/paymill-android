package com.paymill.android.samples.vouchermill.util;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.fragments.AlertDialogFragment;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMService;

public class Util {

	public static PMService.ServiceMode serviceModeFromOrdinal(int ordinal) {
		for (PMService.ServiceMode mode : PMService.ServiceMode.values()) {
			if (mode.ordinal() == ordinal) {
				return mode;
			}
		}
		throw new IllegalArgumentException("No servicemode with ordinal "
				+ ordinal + " exists");
	}

	public static void showFragmentDialog(Context context,
			FragmentManager fragmentManager, String title, String message) {
		AlertDialogFragment dialog = AlertDialogFragment.newInstance(title,
				message);
		dialog.show(fragmentManager, "Alert");

	}

	public static void showErrorFragmentDialog(Context context,
			FragmentManager fragmentManager, String action, PMError error) {
		showFragmentDialog(context, fragmentManager, context.getString(
				R.string.error_alert_title, action), context.getString(
				R.string.error_alert_message, action, error.getType()
						.toString(), error.getMessage()));
	}

	public static void showDialog(Context context, String title, String message) {
		showDialog(context, title, message, null);
	}

	public static void showDialog(Context context, String title,
			String message, DialogInterface.OnDismissListener dismissListener) {
		AlertDialog.Builder builder = new Builder(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.custom_alertdialog, null);
		TextView titleView = (TextView) view.findViewById(R.id.alertTitle);
		TextView messageView = (TextView) view.findViewById(R.id.message);
		titleView.setText(title);
		messageView.setText(message);
		builder.setView(view);
		builder.setNeutralButton(android.R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if (dismissListener != null) {
			builder.setOnDismissListener(dismissListener);
		}
		builder.create().show();
	}

	public static void showDialog(Context context, String action,
			PMError error, DialogInterface.OnDismissListener dismissListener) {
		showDialog(context, context.getString(R.string.error_alert_title,
				action), context.getString(R.string.error_alert_message,
				action, error.getType().toString(), error.getMessage()),
				dismissListener);
	}

	public static void showDialog(Context context, String action, PMError error) {
		showDialog(context, context.getString(R.string.error_alert_title,
				action), context.getString(R.string.error_alert_message,
				action, error.getType().toString(), error.getMessage()), null);
	}
}
