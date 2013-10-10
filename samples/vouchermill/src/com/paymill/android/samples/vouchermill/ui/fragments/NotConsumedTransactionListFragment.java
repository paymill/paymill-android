package com.paymill.android.samples.vouchermill.ui.fragments;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.api.Transaction;
import com.paymill.android.listener.PMListNotConsTransListener;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.ui.FromNCListDetailActivity;
import com.paymill.android.samples.vouchermill.ui.adapters.DetailsListAdapter;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

public class NotConsumedTransactionListFragment extends SherlockFragment implements OnItemClickListener {

	protected static final String TAG = "NotConsumedTransactionList";
	ArrayList<DetailsObject> list = new ArrayList<DetailsObject>();

	DetailsListAdapter adapter;
	ListView listView;

	PMListNotConsTransListener getNotConsTransListener = new PMListNotConsTransListener() {

		@Override
		public void onListNotConsTransactions(
				Collection<Transaction> transactions) {
			list.clear();
			for (Transaction t : transactions) {
				list.add(DetailsObject.fromTransaction(t));
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onListNotConsTransactionsFailed(PMError error) {
		}

	};;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.all_lists_fragment, container, false);
		listView = (ListView) v.findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		adapter = new DetailsListAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		setRetainInstance(true);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
		Log.d(TAG, Integer.toString(position));
		Bundle data = new Bundle();
		DetailsObject detail = list.get(position);
		data.putParcelable(DetailsObject.DETAILS_KEY, detail);
		Intent intent = new Intent(getActivity(), FromNCListDetailActivity.class);
		intent.putExtras(data);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		getTransactions();

	}

	@Override
	public void onDestroy() {
		PMManager.removeListener(getNotConsTransListener);
		super.onDestroy();
	}

	public void getTransactions() {
		PMManager.addListener(getNotConsTransListener);
		PMManager.getNotConsumedTransactions(getActivity());
	}

}
