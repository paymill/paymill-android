package com.paymill.android.samples.vouchermill.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.OfflineTransactionListFragment;

public class OfflineListActivity extends SingleFragmentActivity {

	@Override
	SherlockFragment getFragment() {
		return new OfflineTransactionListFragment();
	}

}