package com.paymill.android.samples.vouchermill.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.paymill.android.api.Transaction;
import com.paymill.android.listener.PMConsumeTransListener;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherListDetailsFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherProgressFragment;
import com.paymill.android.samples.vouchermill.util.Util;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class FromNCListDetailActivity extends SingleFragmentActivity {

	private SherlockFragment fragment;
	private DetailsObject details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle data = getIntent().getExtras();
		if (data == null) {
			finish();
			return;
		}

		details = data.getParcelable(DetailsObject.DETAILS_KEY);
		VoucherListDetailsFragment detailFragment = new VoucherListDetailsFragment();
		fragment = detailFragment.getInstance(details);
		super.onCreate(savedInstanceState);
		PMManager.addListener(transListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		PMManager.removeListener(transListener);
	}

	private PMConsumeTransListener transListener = new PMConsumeTransListener() {

		@Override
		public void onConsumeTransactionFailed(PMError error) {

			VoucherProgressFragment.dismiss(getSupportFragmentManager());
			Util.showDialog(FromNCListDetailActivity.this, "consume", error,
					new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							finish();
						}
					});
		}

		@Override
		public void onConsumeTransaction(Transaction transaction) {
			VoucherProgressFragment.dismiss(getSupportFragmentManager());
			Util.showDialog(FromNCListDetailActivity.this,
					"Consumption success!",
					"Transaction " + transaction.getId() + " consumed.",
					new OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							finish();
						}
					});

		}
	};

	@Override
	SherlockFragment getFragment() {
		return fragment;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.nc_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.triggerConsume) {
			new VoucherProgressFragment().show(getSupportFragmentManager(),
					VoucherProgressFragment.TAG);
			PMManager.consumeTransaction(getApplicationContext(),
					details.getTransId());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
