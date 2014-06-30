package com.paymill.android.samples.vouchermill.db;

import java.util.Collection;

import android.content.Context;
import android.database.Cursor;

import com.paymill.android.api.Payment;
import com.paymill.android.api.Preauthorization;
import com.paymill.android.api.Transaction;
import com.paymill.android.listener.PMBackgroundListener;
import com.paymill.android.samples.vouchermill.entities.Voucher;
import com.paymill.android.samples.vouchermill.entities.Voucher.Type;
import com.paymill.android.samples.vouchermill.ui.helpers.SettingsHelper;
import com.paymill.android.service.PMError;
import com.paymill.android.service.PMManager;

/**
 * This is a sample background listener.
 * <p>
 * <b>Note:</b> We don't actually implement all methods. In fact, we implement
 * only one.
 * </p>
 */
public class BackgroundListener implements PMBackgroundListener {

	Context context;

	public BackgroundListener(Context context) {
		this.context = context;
	}

	@Override
	public void onInit(String deviceId) {

	}

	@Override
	public void onInitFailed(PMError error) {

	}

	@Override
	public void onGenerateToken(String token) {

	}

	@Override
	public void onGenerateTokenFailed(PMError error) {

	}

	@Override
	public void onPreauth(Transaction preauthorizationTransaction) {

	}

	@Override
	public void onPreauthFailed(PMError error) {

	}

	@Override
	public void onGetPreauth(Preauthorization preauthorization) {

	}

	@Override
	public void onGetPreauthFailed(PMError error) {

	}

	@Override
	public void onListPreauths(Collection<Preauthorization> preauthorizations) {

	}

	@Override
	public void onListPreauthsFailed(PMError error) {

	}

	@Override
	public void onTransaction(Transaction transaction) {
		createOrRecoverTransaction(transaction);
	}

	@Override
	public void onTransactionFailed(PMError error) {

	}

	@Override
	public void onListTransactions(Collection<Transaction> transactions) {

	}

	@Override
	public void onListTransactionsFailed(PMError error) {

	}

	@Override
	public void onGetTransaction(Transaction transaction) {

	}

	@Override
	public void onGetTransactionFailed(PMError error) {

	}

	@Override
	public void onListNotConsTransactions(Collection<Transaction> transactions) {
		for (Transaction transaction : transactions) {
			createOrRecoverTransaction(transaction);
		}
	}

	@Override
	public void onListNotConsTransactionsFailed(PMError error) {

	}

	@Override
	public void onListNotConsPreauths(
			Collection<Preauthorization> preauthorizations) {

	}

	@Override
	public void onListNotConsPreauthsFailed(PMError error) {

	}

	@Override
	public void onConsumePreauth(Preauthorization preauthorization) {

	}

	@Override
	public void onConsumePreauthFailed(PMError error) {

	}

	@Override
	public void onConsumeTransaction(Transaction transaction) {

	}

	@Override
	public void onConsumeTransactionFailed(PMError error) {

	}

	@Override
	public void onNewDeviceId(String deviceId) {

	}

	@Override
	public void onNewDeviceIdFailed(PMError error) {

	}

	private void createOrRecoverTransaction(Transaction transaction) {
		VouchersDbAdapter dbHelper = new VouchersDbAdapter(context);
		dbHelper.open();
		Cursor cursor = null;
		try {
			cursor = dbHelper.fetchVoucher(transaction.getId());
		} catch (Exception e) {
			cursor = null;
		}
		// we have not yet saved this transaction
		if (cursor == null) {
			// Lets see if the description is ok

			Voucher create = Voucher.fromTransaction(transaction);
			// we don't save custom vouchers
			if (create != null && create.getVoucherType() != Type.CustomVoucher) {
				// this is not the transaction we were looking for
				dbHelper.createVoucher(create);
			}
		}
		// we should not save it again if we already have...

		// either way we now have to consume the transaction...
		if (SettingsHelper.getInstance(context).isAutoConsume()) {
			PMManager.consumeTransaction(context, transaction);
		}
	}

	@Override
	public void onPaymentSaved(Payment payment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaymentSavedFailed(PMError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListPayments(Collection<Payment> payments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListPaymentsFailed(PMError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResetPayments(boolean result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResetPaymentsFailed(PMError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaymentsAvaialable(boolean available) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaymentsAvaialableFailed(PMError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaymentDeleted(Payment payment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaymentDeletedFailed(PMError error) {
		// TODO Auto-generated method stub
		
	}
}
