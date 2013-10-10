package com.paymill.android.samples.vouchermill.ui.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.entities.DetailsObject;
import com.paymill.android.samples.vouchermill.ui.FromListDetailActivity;
import com.paymill.android.samples.vouchermill.ui.adapters.DetailsListAdapter;

public abstract class OnlineListFragment extends SherlockListFragment {

	protected static final String TAG = "OnlineTransactionList";
	ArrayList<DetailsObject> list = new ArrayList<DetailsObject>();
	DetailsListAdapter adapter;
	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.all_lists_fragment, container,
				false);
		listView = (ListView) v.findViewById(android.R.id.list);
		adapter = new DetailsListAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		setRetainInstance(true);
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Bundle data = new Bundle();
		DetailsObject detail = (DetailsObject) adapter.getItem(position);
		data.putParcelable(DetailsObject.DETAILS_KEY, detail);
		Intent intent = new Intent(getActivity(), FromListDetailActivity.class);
		intent.putExtras(data);
		startActivity(intent);
	}

	void updateVouchers(ArrayList<DetailsObject> details) {
		this.list.clear();
		this.list.addAll(details);
		this.adapter.notifyDataSetChanged();
	}

}
