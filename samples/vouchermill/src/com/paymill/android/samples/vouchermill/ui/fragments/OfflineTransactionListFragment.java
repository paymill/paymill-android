package com.paymill.android.samples.vouchermill.ui.fragments;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.paymill.android.samples.vouchermill.R;
import com.paymill.android.samples.vouchermill.db.VouchersDbAdapter;
import com.paymill.android.samples.vouchermill.entities.Voucher;
import com.paymill.android.samples.vouchermill.entities.Voucher.TransactionType;
import com.paymill.android.samples.vouchermill.entities.Voucher.Type;
import com.paymill.android.samples.vouchermill.ui.adapters.VouchersListAdapter;

public class OfflineTransactionListFragment extends SherlockFragment {

	VouchersDbAdapter dbHelper;
	ArrayList<Voucher> list = new ArrayList<Voucher>();
	VouchersListAdapter adapter;
	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.all_lists_fragment, container, false);

		dbHelper = new VouchersDbAdapter(getActivity());
		dbHelper.open();

		listView = (ListView) v.findViewById(android.R.id.list);
		adapter = new VouchersListAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		fillData();

		getActivity().setTitle(R.string.PurchasedVouchersOfflineFragmentTitle);
		setRetainInstance(true);
		return v;
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
		// Get all of the vouchers from the database and create the item list
		Cursor c = dbHelper.fetchAllVouchers();
		getActivity().startManagingCursor(c);

		TransactionType transactionType;
		Type voucherType;
		String transactionId;
		java.util.Date createdAt;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			transactionType = TransactionType.fromId(c.getInt(c
					.getColumnIndex(VouchersDbAdapter.KEY_TRANSACTION_TYPE)));
			voucherType = Type.fromDescription(Integer.toString(c.getInt(c
					.getColumnIndex(VouchersDbAdapter.KEY_VOUCHER_TYPE))));
			transactionId = c.getString(c
					.getColumnIndex(VouchersDbAdapter.KEY_TRANSACTION_ID));
			createdAt = new java.util.Date(c.getInt(c
					.getColumnIndex(VouchersDbAdapter.KEY_CREATED_AT)));
			list.add(Voucher.fromData(transactionType, voucherType,
					transactionId, createdAt));
		}
		adapter.notifyDataSetChanged();
	}
}
