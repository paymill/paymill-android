package com.paymill.android.samples.vouchermill.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.R;

public abstract class SingleFragmentActivity extends BaseActivity {

	private static final String FRAGMENT_TAG = "SINGLEFRAGMENT";

	private Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singlefragment);
		if (savedInstanceState == null) {
			fragment = getFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragmentHolder, fragment, FRAGMENT_TAG).commit();
		} else {
			fragment = getSupportFragmentManager().findFragmentByTag(
					FRAGMENT_TAG);
		}
	}

	abstract SherlockFragment getFragment();
}
