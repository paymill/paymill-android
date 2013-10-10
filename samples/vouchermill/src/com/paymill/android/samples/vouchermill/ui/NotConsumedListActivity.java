package com.paymill.android.samples.vouchermill.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.NotConsumedTransactionListFragment;

public class NotConsumedListActivity extends SingleFragmentActivity {


	@Override
	SherlockFragment getFragment() {
		return new NotConsumedTransactionListFragment();
	}

}