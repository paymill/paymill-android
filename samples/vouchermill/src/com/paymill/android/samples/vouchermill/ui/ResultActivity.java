package com.paymill.android.samples.vouchermill.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.payment.PaymentActivity.Result;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.ui.fragments.ErrorResultFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.GenerateTokenSuccessfulResult;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherResultDetailsFragment;

public class ResultActivity extends SingleFragmentActivity {

	public static final String RESULT_KEY = "result";

	private Result result;
	private SherlockFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle data = getIntent().getExtras();
		result = data.getParcelable(RESULT_KEY);
		if (result == null) {
			finish();
			return;
		} else if (result.isError()) {
			fragment = ErrorResultFragment.instance(result);
		} else {
			switch (result.getType()) {
			case TOKEN:
				fragment = GenerateTokenSuccessfulResult.instance(result
						.getResultToken());
				break;
			case TRANSACTION:
				VoucherResultDetailsFragment detailFragment = new VoucherResultDetailsFragment();
				fragment = detailFragment.getInstance(DetailsObject
						.fromTransaction(result.getResult()));
				break;
			default:
				break;
			}
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	SherlockFragment getFragment() {
		return fragment;
	}
}
