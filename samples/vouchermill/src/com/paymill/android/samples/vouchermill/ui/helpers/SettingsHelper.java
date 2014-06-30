package com.paymill.android.samples.vouchermill.ui.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.util.Base64;

import com.paymill.android.payment.PaymentActivity.Settings;
import com.paymill.android.samples.vouchermill.util.Util;
import com.paymill.android.service.PMService.ServiceMode;

public class SettingsHelper {
	private static final String SHAREDPREFS_KEY = "VOUCHERMILL";
	private static final String SETTINGS_PREF_KEY = "pmsettings";
	private static final String AUTO_CONSUME_PREF_KEY = "autoconsume";
	private static final String PUBLIC_KEY_PREF_KEY = "PUBLICKEY";
	private static final String SERVICE_MODE_PREF_KEY = "SERVICEMODE";
	private static SettingsHelper singleton;

	public static SettingsHelper getInstance(Context context) {
		if (singleton == null) {
			singleton = new SettingsHelper(context);
		}
		return singleton;
	}

	private Settings settings;
	private boolean autoConsume;
	private boolean useSafeStore;
	private String publicKey;
	private ServiceMode serviceMode;
	private SharedPreferences preferences;

	public SettingsHelper(Context context) {
		preferences = context.getApplicationContext().getSharedPreferences(
				SHAREDPREFS_KEY, 0);
		initFromPreferences();
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		save();
	}

	public boolean isAutoConsume() {
		return autoConsume;
	}

	public void setAutoConsume(boolean autoConsume) {
		this.autoConsume = autoConsume;
		save();
	}

	public boolean isUseSafeStore() {
		return useSafeStore;
	}

	public void setUseSafeStore(boolean useSafeStore) {
		this.useSafeStore = useSafeStore;
		save();
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
		save();
	}

	public ServiceMode getServiceMode() {
		return serviceMode;
	}

	public void setServiceMode(ServiceMode serviceMode) {
		this.serviceMode = serviceMode;
		save();
	}

	private void initFromPreferences() {
		retrieveSettings();
		this.autoConsume = preferences.getBoolean(AUTO_CONSUME_PREF_KEY, true);
		this.publicKey = preferences.getString(PUBLIC_KEY_PREF_KEY, "");
		this.serviceMode = Util.serviceModeFromOrdinal(preferences.getInt(
				SERVICE_MODE_PREF_KEY, ServiceMode.TEST.ordinal()));
	}

	private void save() {
		saveSettings();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PUBLIC_KEY_PREF_KEY, publicKey);
		editor.putInt(SERVICE_MODE_PREF_KEY, serviceMode.ordinal());
		editor.putBoolean(AUTO_CONSUME_PREF_KEY, autoConsume);
		editor.commit();
	}

	private void saveSettings() {
		Parcel settingsParcel = Parcel.obtain();
		settings.writeToParcel(settingsParcel, 0);
		String serialized = null;
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(settingsParcel.marshall());
			serialized = Base64.encodeToString(bos.toByteArray(), 0);
		} catch (IOException e) {
			// nothing we can do
		} finally {
			settingsParcel.recycle();
		}
		if (serialized != null) {
			Editor editor = preferences.edit();
			editor.putString(SETTINGS_PREF_KEY, serialized);
			editor.commit();
		}
	}

	private void retrieveSettings() {
		String serialized = preferences.getString(SETTINGS_PREF_KEY, null);
		if (serialized != null) {
			Parcel parcel = Parcel.obtain();
			try {
				byte[] data = Base64.decode(serialized, 0);
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);
				this.settings = new Settings(parcel);
			} finally {
				parcel.recycle();
			}
		}
		if (this.settings == null) {
			this.settings = new Settings();
			saveSettings();
		}
	}
}
