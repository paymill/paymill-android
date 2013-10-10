package com.paymill.android.samples.vouchermill.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.ui.fragments.VoucherListDetailsFragment;

public class FromListDetailActivity extends SingleFragmentActivity {

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
	}

	@Override
	SherlockFragment getFragment() {
		return fragment;
	}

}
