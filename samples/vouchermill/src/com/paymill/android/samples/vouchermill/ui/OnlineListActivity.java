package com.paymill.android.samples.vouchermill.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.adapters.VoucherScreenAdapter;
import com.paymill.android.samples.vouchermill.ui.fragments.OnlinePreauthorizationListFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.OnlineTransactionListFragment;
import com.paymill.android.samples.vouchermill.ui.helpers.SherlockListNavigationHelper;
import com.paymill.android.samples.vouchermill.ui.helpers.SherlockListNavigationHelper.SupportActionBarActivity;
import com.viewpagerindicator.UnderlinePageIndicator;

public class OnlineListActivity extends BaseActivity
		implements
		SherlockListNavigationHelper.Listener<OnlineListActivity.OnlineVouchersListItem>,
		SupportActionBarActivity {

	ViewPager viewPager;
	VoucherScreenAdapter adapter;
	UnderlinePageIndicator indicator;
	SherlockListNavigationHelper<OnlineVouchersListItem> navigationHelper;

	public static enum OnlineVouchersListItem implements
			SherlockListNavigationHelper.Item {
		TRANSACTIONS(R.string.online_transactions), PREAUTHORIZATIONS(
				R.string.online_preauthorizations);
		private int stringResId;

		private OnlineVouchersListItem(int stringResId) {
			this.stringResId = stringResId;
		}

		private static final OnlineVouchersListItem[] all = { TRANSACTIONS,
				PREAUTHORIZATIONS };

		@Override
		public int getStringResource() {
			return this.stringResId;
		}

		public static OnlineVouchersListItem fromOrdinal(int ordinal) {
			for (OnlineVouchersListItem item : OnlineVouchersListItem.values()) {
				if (item.ordinal() == ordinal) {
					return item;
				}

			}
			return null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInsanceState) {
		super.onCreate(savedInsanceState);
		this.setContentView(R.layout.singlefragment);
		navigationHelper = new SherlockListNavigationHelper<OnlineListActivity.OnlineVouchersListItem>(
				this, OnlineVouchersListItem.all, this,
				R.layout.custom_spiner_item,R.layout.custom_spiner_dropdown_item);
		navigationHelper.enableListNavigation();
		if (savedInsanceState != null && savedInsanceState.getInt("item") > -1) {
			navigationHelper.setSelectedNavigationItem(OnlineVouchersListItem
					.fromOrdinal(savedInsanceState.getInt("item")));
		}
	}

	@Override
	public boolean onNavigationItemSelected(OnlineVouchersListItem listItem) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(listItem
				.toString());
		if (fragment == null) {
			switch (listItem) {
			case TRANSACTIONS:
				fragment = new OnlineTransactionListFragment();
				break;
			case PREAUTHORIZATIONS:
				fragment = new OnlinePreauthorizationListFragment();
				break;
			default:
				return false;
			}
		}
		fragmentManager.beginTransaction()
				.replace(R.id.fragmentHolder, fragment).commit();
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("item", navigationHelper.getSelectedItem().ordinal());
	}
}