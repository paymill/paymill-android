package com.paymill.android.samples.vouchermill.ui.fragments;

import java.util.ArrayList;
import java.util.Collection;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.paymill.android.api.Transaction;
import com.paymill.android.listener.PMListTransListener;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class OnlineTransactionListFragment extends OnlineListFragment {

	ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

	PMListTransListener getTransactionsListener = new PMListTransListener() {

		@Override
		public void onListTransactions(Collection<Transaction> transactions) {
			ArrayList<DetailsObject> list = new ArrayList<DetailsObject>();
			transactionList.clear();
			for (Transaction t : transactions) {
				list.add(DetailsObject.fromTransaction(t));
				transactionList.add(t);
			}
			updateVouchers(list);

		}

		@Override
		public void onListTransactionsFailed(PMError error) {
			((SherlockFragmentActivity) getActivity())
					.setSupportProgressBarIndeterminateVisibility(false);
		}

	};

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		getTransactions();
	};

	@Override
	public void onDestroy() {
		PMManager.removeListener(getTransactionsListener);
		super.onDestroy();
	}

	public void getTransactions() {
		((SherlockFragmentActivity) getActivity())
				.setSupportProgressBarIndeterminate(true);
		PMManager.addListener(getTransactionsListener);
		PMManager.listTransactions(getActivity());
	}

}
