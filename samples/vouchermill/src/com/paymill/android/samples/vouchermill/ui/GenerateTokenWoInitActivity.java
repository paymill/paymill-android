package com.paymill.android.samples.vouchermill.ui;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.payment.PaymentActivity;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.fragments.LoginFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherInfo;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherInfo.VoucherInfoValidationException;
import com.paymill.android.samples.vouchermill.util.Util;
import com.paymill.android.service.PMService.ServiceMode;

public class GenerateTokenWoInitActivity extends VoucherPagerActivity {

	private ServiceMode mode;
	private String publicKey;

	@Override
	protected void onCreate(Bundle savedInsanceState) {
		super.onCreate(savedInsanceState);
		Bundle data = getIntent().getExtras();
		publicKey = data.getString(LoginFragment.PUBLICKEY_KEY);
		try {
			mode = Util.serviceModeFromOrdinal(data
					.getInt(LoginFragment.SERVICE_MODE_KEY));
		} catch (Exception e) {
			finish();
		}
		if (publicKey == null || mode == null) {
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.token, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings) {
			// TODO show settings activity
			return true;
		}
		PMPaymentParams params = null;
		try {
			params = ((VoucherInfo) adapter.instantiateItem(viewPager,
					viewPager.getCurrentItem())).getInfo();
		} catch (VoucherInfoValidationException e) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.triggerToken:
			Intent tokenIntent = PaymentActivity.Factory.getTokenIntent(
					getApplicationContext(), params, mode, publicKey);
			startActivityForResult(tokenIntent, PaymentActivity.REQUEST_CODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Class<? extends ResultActivity> getResultActivityClass() {
		return GenerateTokenWoInitResultActivity.class;
	}

}