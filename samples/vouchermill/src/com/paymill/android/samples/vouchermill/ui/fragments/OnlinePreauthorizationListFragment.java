package com.paymill.android.samples.vouchermill.ui.fragments;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.paymill.android.api.Preauthorization;
import com.paymill.android.listener.PMListPreauthsListener;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class OnlinePreauthorizationListFragment extends OnlineListFragment {

	PMListPreauthsListener getPreauthorizationsListener = new PMListPreauthsListener() {

		@Override
		public void onListPreauths(
				Collection<Preauthorization> preauthorizations) {
			ArrayList<DetailsObject> list = new ArrayList<DetailsObject>();
			for (Preauthorization t : preauthorizations) {
				list.add(DetailsObject.fromPreauthorization(t));
			}
			updateVouchers(list);
		}

		@Override
		public void onListPreauthsFailed(PMError error) {
		}

	};

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getPreauthorizations();

	};

	@Override
	public void onDestroy() {
		PMManager.removeListener(getPreauthorizationsListener);
		super.onDestroy();
	}

	public void getPreauthorizations() {
		((SherlockFragmentActivity) getActivity())
				.setSupportProgressBarIndeterminateVisibility(true);
		PMManager.addListener(getPreauthorizationsListener);
		PMManager.listPreauthorizations(getActivity());
	}
}
