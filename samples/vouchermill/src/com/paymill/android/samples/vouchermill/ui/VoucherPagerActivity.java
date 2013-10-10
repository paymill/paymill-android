package com.paymill.android.samples.vouchermill.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.paymill.android.payment.PaymentActivity;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.adapters.VoucherScreenAdapter;
import com.paymill.android.samples.vouchermill.util.Util;
import com.viewpagerindicator.UnderlinePageIndicator;

public abstract class VoucherPagerActivity extends BaseActivity {

	ViewPager viewPager;
	VoucherScreenAdapter adapter;
	UnderlinePageIndicator indicator;

	@Override
	protected void onCreate(Bundle savedInsanceState) {
		super.onCreate(savedInsanceState);
		setContentView(R.layout.voucher_screen);

		// setup viewpager
		viewPager = (ViewPager) findViewById(R.id.voucherScreenViewpager);
		adapter = new VoucherScreenAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);
		indicator.setFades(false);
		indicator.setSelectedColor(getResources().getColor(R.color.Orange));

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		PaymentActivity.Result result = PaymentActivity.Factory.getResultFrom(
				requestCode, resultCode, data);
		if (result == null) {
			// this is not the result we were looking for
			return;
		} else {
			if (result.isCanceled()) {
				Util.showDialog(this, "Result", "Payment screen was canceled");
			} else {
				Bundle bundle = new Bundle();
				bundle.putParcelable(ResultActivity.RESULT_KEY, result);

				Intent intent = new Intent(this, getResultActivityClass());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	protected abstract Class<? extends ResultActivity> getResultActivityClass();
}