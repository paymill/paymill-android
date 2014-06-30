package com.paymill.android.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.paymill.android.api.Payment;
import com.paymill.android.listener.PMPaymentSavedListener;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class ChooseSavePaymentFragment extends Fragment {

	protected static final String FRAGMENT_TAG = "FRAGMENT_TAG";
	private Payment payment;
	String password;
	private EditText passwordView;
	ProgressDialogFragment progress;

	public static ChooseSavePaymentFragment instance(Payment payment) {
		ChooseSavePaymentFragment fragment = new ChooseSavePaymentFragment();
		Bundle bundle = new Bundle();
		fragment.payment = payment;
		fragment.setArguments(bundle);
		return fragment;
	}

	private PMPaymentSavedListener paymentSavedListener = new PMPaymentSavedListener() {

		@Override
		public void onPaymentSavedFailed(PMError error) {
			progress.dismiss();
			Toast f = Toast.makeText(getActivity().getApplicationContext(),
					"Payment save failed, try again.", Toast.LENGTH_LONG);
			((PaymentActivity) getActivity()).password = null;
			f.show();
		}

		@Override
		public void onPaymentSaved(Payment payment) {
			progress.dismiss();
			Toast f = Toast.makeText(getActivity().getApplicationContext(),
					"Payment saved", Toast.LENGTH_SHORT);
			if (((PaymentActivity) getActivity()).password == null) {
				((PaymentActivity) getActivity()).password = password;
			}
			f.show();
			((PaymentActivity) getActivity()).finishSuccess();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pm_choose_save_payment_fragment,
				container, false);
		PMManager.addListener(paymentSavedListener);
		passwordView = (EditText) v.findViewById(R.id.savePaymentPin);
		if (((PaymentActivity) getActivity()).password != null) {
			password = ((PaymentActivity) getActivity()).password;
			passwordView.setVisibility(View.GONE);
		}
		((Button) v.findViewById(R.id.yesSavePayment))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (((PaymentActivity) getActivity()).password != null) {
							password = ((PaymentActivity) getActivity()).password;
						} else {
							password = (passwordView.getText().toString());
						}
						progress = new ProgressDialogFragment();
						progress.show(
								getActivity().getSupportFragmentManager(),
								ProgressDialogFragment.TAG);
						PMManager.savePayment(getActivity()
								.getApplicationContext(), payment, password);
					}
				});
		((Button) v.findViewById(R.id.noSavePayment))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((PaymentActivity) getActivity()).finishSuccess();
					}
				});
		addPaymentUiItem(payment,
				(LinearLayout) v.findViewById(R.id.savePaymentHolder));
		return v;
	}

	private void addPaymentUiItem(Payment payment, LinearLayout parentView) {
		LinearLayout paymentItemLayout = (LinearLayout) getActivity()
				.getLayoutInflater().inflate(R.layout.pm_payment_save_item,
						null);
		TextView cardNumberView = (TextView) paymentItemLayout
				.findViewById(R.id.cardNumberSave);
		ImageView cardLogoView = (ImageView) paymentItemLayout
				.findViewById(R.id.cardLogoSave);
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
		parentView.addView(paymentItemLayout);
	}

	@Override
	public void onDestroyView() {
		PMManager.removeListener(paymentSavedListener);
		super.onDestroyView();
	}
}
