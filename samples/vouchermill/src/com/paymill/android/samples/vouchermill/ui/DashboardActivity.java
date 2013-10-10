package com.paymill.android.samples.vouchermill.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.DashboardFragment;

public class DashboardActivity extends SingleFragmentActivity {

	@Override
	SherlockFragment getFragment() {
		return new DashboardFragment();
	}
}
