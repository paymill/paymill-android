package com.paymill.android.samples.vouchermill.ui;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.payment.PaymentActivity;
import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherInfo;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherInfo.VoucherInfoValidationException;
import com.paymill.android.samples.vouchermill.ui.helpers.SettingsHelper;
import com.paymill.android.samples.vouchermill.ui.helpers.SherlockListNavigationHelper;
import com.paymill.android.samples.vouchermill.ui.helpers.SherlockListNavigationHelper.SupportActionBarActivity;

public class VoucherScreenActivity extends VoucherPagerActivity
		implements
		SherlockListNavigationHelper.Listener<VoucherScreenActivity.VoucherTypeListItem>,
		SupportActionBarActivity {

	SherlockListNavigationHelper<VoucherTypeListItem> navigationHelper;

	public static enum VoucherTypeListItem implements
			SherlockListNavigationHelper.Item {
		BUY(R.string.buy_voucher), RESERVE(R.string.reserve_voucher), TOKEN(
				R.string.token_voucher);
		private int stringResId;

		private VoucherTypeListItem(int stringResId) {
			this.stringResId = stringResId;
		}

		private static final VoucherTypeListItem[] all = { BUY, RESERVE, TOKEN };

		@Override
		public int getStringResource() {
			return this.stringResId;
		}
	}

	@Override
	protected void onCreate(Bundle savedInsanceState) {
		super.onCreate(savedInsanceState);
		navigationHelper = new SherlockListNavigationHelper<VoucherScreenActivity.VoucherTypeListItem>(
				this, VoucherTypeListItem.all, this,
				R.layout.custom_spiner_item,R.layout.custom_spiner_dropdown_item);
		navigationHelper.enableListNavigation();
	}

	@Override
	public boolean onNavigationItemSelected(VoucherTypeListItem listItem) {
		supportInvalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		switch (navigationHelper.getSelectedItem()) {
		case BUY:
			inflater.inflate(R.menu.transaction, menu);
			break;
		case RESERVE:
			inflater.inflate(R.menu.preauthorization, menu);
			break;
		case TOKEN:
			inflater.inflate(R.menu.token, menu);
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings) {
			Intent intent = new Intent(this,SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		PMPaymentParams params = null;
		Settings settings = SettingsHelper.getInstance(getApplicationContext())
				.getSettings();
		try {
			params = ((VoucherInfo) adapter.instantiateItem(viewPager,
					viewPager.getCurrentItem())).getInfo();
		} catch (VoucherInfoValidationException e) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.triggerPreauthorization:
			// we always want preauths to be consumable
			Intent preauthIntent = PaymentActivity.Factory
					.getPreauthorizationIntent(getApplicationContext(), params,
							settings, true);
			startActivityForResult(preauthIntent, PaymentActivity.REQUEST_CODE);
			return true;
		case R.id.triggerTransaction:
			// we always want transactions to be consumable
			Intent transactionIntent = PaymentActivity.Factory
					.getTransactionIntent(getApplicationContext(), params,
							settings, true);
			startActivityForResult(transactionIntent,
					PaymentActivity.REQUEST_CODE);
			return true;
		case R.id.triggerToken:
			Intent tokenIntent = PaymentActivity.Factory.getTokenIntent(
					getApplicationContext(), params, settings);
			startActivityForResult(tokenIntent, PaymentActivity.REQUEST_CODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Class<? extends ResultActivity> getResultActivityClass() {
		return VoucherScreenResultActivity.class;
	}
}