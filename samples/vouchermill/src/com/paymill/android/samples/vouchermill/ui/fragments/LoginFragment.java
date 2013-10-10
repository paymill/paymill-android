package com.paymill.android.samples.vouchermill.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.listener.PMBackgroundListener;
import com.paymill.android.listener.PMInitListener;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.db.BackgroundListener;
import com.paymill.android.samples.vouchermill.ui.DashboardActivity;
import com.paymill.android.samples.vouchermill.ui.GenerateTokenWoInitActivity;
import com.paymill.android.samples.vouchermill.ui.helpers.SettingsHelper;
import com.paymill.android.samples.vouchermill.util.Constants;
import com.paymill.android.samples.vouchermill.util.Util;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;
import com.paymill.android.service.PMService.ServiceMode;

public class LoginFragment extends SherlockFragment {

	public static final String PUBLICKEY_KEY = "publickey";
	public static final String SERVICE_MODE_KEY = "servicemode";
	private String publicKey;
	private ServiceMode serviceMode;
	private TextView publicKeyText;
	private PMInitListener initListener = new PMInitListener() {

		@Override
		public void onInit(String deviceId) {
			((VoucherProgressFragment) getFragmentManager().findFragmentByTag(
					VoucherProgressFragment.TAG)).dismiss();
			Intent i = new Intent(getActivity(), DashboardActivity.class);
			startActivity(i);
		}

		@Override
		public void onInitFailed(PMError error) {
			((VoucherProgressFragment) getFragmentManager().findFragmentByTag(
					VoucherProgressFragment.TAG)).dismiss();
			Util.showErrorFragmentDialog(getActivity(), getActivity()
					.getSupportFragmentManager(), "init", error);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PMManager.addListener(initListener);
		setRetainInstance(true);
		getActivity().setTitle(R.string.loginTitle);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login_fragment, container, false);
		publicKeyText = (TextView) v.findViewById(R.id.publicKeyText);
		
		// init from sharedpreferences
		publicKey = SettingsHelper.getInstance(getActivity()).getPublicKey();
		serviceMode = SettingsHelper.getInstance(getActivity())
				.getServiceMode();
		if (!TextUtils.isEmpty(publicKey)) {
			publicKeyText.setText(publicKey);
		}
		
		// test login
		Button testLoginButton = (Button) v
				.findViewById(R.id.loginWithTestAccountButton);
		testLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// please note we use the application context, and not the
				// activity, otherwise the activity's resource cannot be garbage
				// collected
				PMBackgroundListener bkglistener = new BackgroundListener(
						getActivity().getApplicationContext());
				new VoucherProgressFragment().show(getFragmentManager(),
						VoucherProgressFragment.TAG);
				PMManager.init(getActivity(), ServiceMode.TEST,
						Constants.TEST_PUBLIC_KEY, bkglistener, null);
			}
		});
		
		// radio group 
		RadioGroup liveTest=(RadioGroup)v.findViewById(R.id.liveTestGroup);
		liveTest.check(serviceMode==ServiceMode.LIVE?R.id.liveButton:R.id.testButton);
		liveTest.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.testButton:
					serviceMode=ServiceMode.TEST;
					break;
				case R.id.liveButton:
					serviceMode=ServiceMode.LIVE;
					break;
				default:
					throw new IllegalArgumentException("Checked an unkown option:"+checkedId);
				}
			}
		});
		
		// login button
		Button loginButton = (Button) v.findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveToPreferences();
				SettingsHelper.getInstance(getActivity()).setServiceMode(
						serviceMode);
				// please note we use the application context, and not the
				// activity, otherwise the activity's resource cannot be garbage
				// collected
				PMBackgroundListener bkglistener = new BackgroundListener(
						getActivity().getApplicationContext());
				new VoucherProgressFragment().show(getFragmentManager(),
						VoucherProgressFragment.TAG);
				PMManager.init(getActivity(), serviceMode, publicKey,
						bkglistener, null);
			}
		});
		
		// generate token
		Button generateTokenWithParamsBtn = (Button) v
				.findViewById(R.id.tokenWithParamsButton);
		generateTokenWithParamsBtn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						saveToPreferences();
						Intent i = new Intent(getActivity(),
								GenerateTokenWoInitActivity.class);
						i.putExtra(PUBLICKEY_KEY, publicKey);
						i.putExtra(SERVICE_MODE_KEY, serviceMode.ordinal());
						startActivity(i);

					}
				});
		return v;
	}

	private void saveToPreferences() {
		this.publicKey = publicKeyText.getText().toString();
		if (!TextUtils.isEmpty(publicKey)) {
			SettingsHelper.getInstance(getActivity()).setPublicKey(publicKey);
		}
		SettingsHelper.getInstance(getActivity()).setServiceMode(serviceMode);
	}

	@Override
	public void onDestroy() {
		PMManager.removeListener(initListener);
		super.onDestroy();
	}
}
