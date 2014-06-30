package com.paymill.android.samples.vouchermill.ui.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;

public abstract class VoucherDetailsFragment extends SherlockFragment {

	public abstract VoucherDetailsFragment getInstance(DetailsObject details);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		DetailsObject detailsObject = getArguments().getParcelable(
				DetailsObject.DETAILS_KEY);

		getActivity().setTitle(R.string.DetailScreenTitle);
		View view = inflater.inflate(R.layout.voucher_details_fragment,
				container, false);

		int amount = Integer.parseInt(detailsObject.getAmount());
		float value = ((float) amount / 100);
		String voucherPrice = String.valueOf(value) + " "
				+ detailsObject.getCurrency();

		// display voucher information
		((TextView) view.findViewById(R.id.amountText)).setText(voucherPrice);

		Long unixTime = detailsObject.getCreatedAt().getTime();
		Date date = new Date((long) unixTime * 1000);
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss");
		format.setTimeZone(TimeZone.getDefault());

		((TextView) view.findViewById(R.id.createdAtText)).setText(format
				.format(date));

		((TextView) view.findViewById(R.id.descriptionText))
				.setText(detailsObject.getDescription());

		((TextView) view.findViewById(R.id.statusCodeText))
				.setText(detailsObject.getStatusCode());
		if (detailsObject.getPaymentType() != null) {
			// display payment information labels and text for credit card
			if (detailsObject.getPaymentType().equals("creditcard")) {
				((TextView) view.findViewById(R.id.field1Label))
						.setText("Credit Card:");

				if (detailsObject.getCardType().equals("diners")) {
					((TextView) view.findViewById(R.id.field1Text))
							.setText("xxxx-xxxxxx-"
									+ detailsObject.getCreditCardLast4());
				} else if (detailsObject.getCardType().equals("amex")) {
					((TextView) view.findViewById(R.id.field1Text))
							.setText("xxxx-xxxxxx-x"
									+ detailsObject.getCreditCardLast4());
				} else if (detailsObject.getCardType()
						.equals("china_union_pay")) {
					((TextView) view.findViewById(R.id.field1Text))
							.setText("xxxxxx-xxxxxxxxx"
									+ detailsObject.getCreditCardLast4());
				} else {
					((TextView) view.findViewById(R.id.field1Text))
							.setText("xxxx-xxxx-xxxx-"
									+ detailsObject.getCreditCardLast4());
				}

				((TextView) view.findViewById(R.id.field2Label))
						.setText("Type:");
				((TextView) view.findViewById(R.id.field2Text))
						.setText(detailsObject.getCardType());
			}

			// display payment information labels and text for debit
			if (detailsObject.getPaymentType().equals("debit")) {
				((TextView) view.findViewById(R.id.field1Label))
						.setText("Account:");
				((TextView) view.findViewById(R.id.field1Text))
						.setText(detailsObject.getAccountNumber());

				((TextView) view.findViewById(R.id.field2Label))
						.setText("Bank code:");
				((TextView) view.findViewById(R.id.field2Text))
						.setText(detailsObject.getBankCode());
			}
		}

		return view;

	}

}
