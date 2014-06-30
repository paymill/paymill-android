package com.paymill.android.payment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paymill.android.api.Payment;
import com.paymill.android.listener.PMPaymentDeletedListener;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class PaymentsAvailableFragment extends Fragment {

	public static final String FRAGEMNT_TAG = "Payments available";
	private ProgressDialogFragment progress;
	private View itemToBeDeleted;
	public ArrayList<Payment> paymentsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	public static PaymentsAvailableFragment instance(ArrayList<Payment> payments) {
		PaymentsAvailableFragment fragment = new PaymentsAvailableFragment();
		fragment.paymentsList = payments;
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	private PMPaymentDeletedListener deletePaymentListener = new PMPaymentDeletedListener() {

		@Override
		public void onPaymentDeletedFailed(PMError error) {

			progress.dismiss();
			Toast f = Toast
					.makeText(
							getActivity().getApplicationContext(),
							getResources().getString(
									R.string.pm_reset_payments_failed),
							Toast.LENGTH_LONG);
			f.show();
		}

		@Override
		public void onPaymentDeleted(Payment payment) {
			itemToBeDeleted.setVisibility(View.GONE);
			progress.dismiss();
			Toast f = Toast.makeText(getActivity().getApplicationContext(),
					getResources().getString(R.string.pm_reset_payments),
					Toast.LENGTH_LONG);
			f.show();
		}
	};
	private OnClickListener paymentItemOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String paymentId = (String) v.getTag();
			((PaymentActivity) getActivity())
					.startRequestWithPayment(paymentId);
		}
	};
	private OnClickListener delPayment = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String paymentId = (String) v.getTag();
			PMManager.deletePayment(getActivity().getApplicationContext(),
					paymentId, ((PaymentActivity) getActivity()).password);
			itemToBeDeleted = (View) v.getParent().getParent();
			progress = new ProgressDialogFragment();
			progress.show(getActivity().getSupportFragmentManager(),
					ProgressDialogFragment.TAG);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pm_payments_available_fragment,
				container, false);
		((Button) v.findViewById(R.id.pm_choose_payment_new))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((PaymentActivity) getActivity())
								.addNewPaymentFragment();
					}
				});
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		PMManager.addListener(deletePaymentListener);
		LinearLayout parentView = (LinearLayout) getView().findViewById(
				R.id.listPayments);
		for (int i = 0; i < paymentsList.size(); i++) {
			addPaymentUiItem(paymentsList.get(i), parentView);
		}
		super.onViewCreated(view, savedInstanceState);
	}

	private void addPaymentUiItem(Payment payment, LinearLayout parentView) {
		LinearLayout paymentItemLayout = (LinearLayout) getActivity()
				.getLayoutInflater().inflate(R.layout.pm_payment_item, null);
		TextView cardNumberView = (TextView) paymentItemLayout
				.findViewById(R.id.cardNumber);
		ImageView cardLogoView = (ImageView) paymentItemLayout
				.findViewById(R.id.cardLogo);
		ImageView cardDelView = (ImageView) paymentItemLayout
				.findViewById(R.id.cardDelete);
		cardDelView.setTag(payment.getId());
		cardDelView.setOnClickListener(delPayment);

		if (PaymentActivity.PAYMENT_TYPE_CREDIT_CARD.equals(payment.getType())) {

			cardNumberView.setText(getResources().getString(
					R.string.pm_cc_stars)
					+ payment.getLast4());
			cardLogoView
					.setImageResource(CardTypeParser.paymentStringToImageResource
							.get(payment.getCardType()));
		} else {
			cardNumberView.setText(payment.getAccount());
			// TODO Add image for debit payments
		}
		paymentItemLayout.setTag(payment.getId());
		paymentItemLayout.setOnClickListener(paymentItemOnClick);
		parentView.addView(paymentItemLayout);
	}

	@Override
	public void onDestroyView() {
		PMManager.removeListener(deletePaymentListener);
		super.onDestroyView();
	}

}
