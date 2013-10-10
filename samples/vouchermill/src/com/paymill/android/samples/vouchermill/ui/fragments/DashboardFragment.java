package com.paymill.android.samples.vouchermill.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.ui.NotConsumedListActivity;
import com.paymill.android.samples.vouchermill.ui.OfflineListActivity;
import com.paymill.android.samples.vouchermill.ui.OnlineListActivity;
import com.paymill.android.samples.vouchermill.ui.VoucherScreenActivity;
import com.paymill.android.samples.vouchermill.ui.adapters.DashboardAdapter;

public class DashboardFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.dashboard_home_fragment, container,
				false);

		GridView menuGrid = (GridView) v
				.findViewById(R.id.menu_dashboard_gridview);
		menuGrid.setAdapter(new DashboardAdapter(getActivity()
				.getApplicationContext()));

		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				switch (position) {
				case 0:
					Intent voucherIntent = new Intent(getActivity(),
							VoucherScreenActivity.class);
					startActivity(voucherIntent);
					break;
				case 1:
					Intent onlineListIntent = new Intent(getActivity(),
							OnlineListActivity.class);
					startActivity(onlineListIntent);
					break;
				case 2:
					Intent offlineListIntent = new Intent(getActivity(),
							OfflineListActivity.class);
					startActivity(offlineListIntent);
					break;
				case 3:
					Intent notConsumedListIntent = new Intent(getActivity(),
							NotConsumedListActivity.class);
					startActivity(notConsumedListIntent);
					break;
				}
			}
		});

		return v;
	}

	void startFragment(Fragment fragment) {

		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.fragmentHolder, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}
