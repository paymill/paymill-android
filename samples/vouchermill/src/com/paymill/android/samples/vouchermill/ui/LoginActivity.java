package com.paymill.android.samples.vouchermill.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.LoginFragment;

public class LoginActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	SherlockFragment getFragment() {
		return new LoginFragment();
	}
}
