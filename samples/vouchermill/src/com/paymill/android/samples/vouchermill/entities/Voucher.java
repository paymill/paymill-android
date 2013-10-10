package com.paymill.android.samples.vouchermill.entities;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;

import com.paymill.android.api.Preauthorization;
import com.paymill.android.api.Transaction;
import com.paymill.android.factory.PMFactory;
import com.paymill.android.factory.PMPaymentParams;
import com.paymill.android.samples.vouchermill.R;

public class Voucher {

	private TransactionType transactionType;
	private Type voucherType;
	private String transactionId;
	private java.util.Date createdAt;

	public enum TransactionType {
		Transaction(1), Preauthorization(2);

		private int id;

		private TransactionType(int id) {
			this.id = id;
		}

		public static TransactionType fromId(int id) {
			for (TransactionType tt : values()) {
				if (tt.id == id)
					return tt;
			}
			return null;
		}

		public int getId() {
			return id;
		}
	}

	public enum Type {
		SmallVoucher(1000, "EUR", "Burger", R.drawable.voucher0), MiddleVoucher(
				2000, "EUR", "Ticket", R.drawable.voucher1), BigVoucher(5000,
				"EUR", "Tire", R.drawable.voucher2), CustomVoucher(0, "", "",
				R.drawable.voucher_custom);

		private int amount;
		private String currency;
		private String description;
		private int imageId;

		private Type(int amount, String currency, String description,
				int imageId) {
			this.amount = amount;
			this.currency = currency;
			this.description = description;
			this.imageId = imageId;
		}

		public int getId() {
			return ordinal();
		}

		public int getAmount() {
			return amount;
		}

		public String getCurrency() {
			return currency;
		}

		public String getDescription() {
			return description;
		}

		public int getImageId() {
			return imageId;
		}

		public static Type getType(int id) {
			for (Type t : values()) {
				if (t.getId() == id)
					return t;
			}
			return null;
		}

		public PMPaymentParams genPaymentParams() {
			return PMFactory.genPaymentParams(currency, amount,
					Integer.toString(getId()));
		}

		public static Type fromDescription(String description) {

			try {
				for (Type t : Type.values()) {
					if (t.getId() == Integer.parseInt(description))
						return t;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			CustomVoucher.description = description;
			return CustomVoucher;
		}

		public void setCustomType(int amount, String currency,
				String description) {
			setCustomAmount(amount);
			setCustomCurrency(currency);
			setCustomDescription(description);
		}

		public void setCustomAmount(int amount) {
			CustomVoucher.amount = amount;
		}

		public void setCustomCurrency(String currency) {
			CustomVoucher.currency = currency;
		}

		public void setCustomDescription(String description) {
			CustomVoucher.description = description;
		}
	}

	public static Voucher fromTransaction(Transaction transaction) {
		if (transaction == null) {
			return null;
		}

		Type type = Type.fromDescription(transaction.getDescription());

		Voucher voucher = new Voucher();
		voucher.transactionId = transaction.getId();
		voucher.transactionType = TransactionType.Transaction;
		voucher.createdAt = transaction.getCreatedAt();
		voucher.voucherType = type;
		return voucher;
	}

	public static Voucher fromData(TransactionType transactionType,
			Type voucherType, String transactionId, java.util.Date createdAt) {

		Voucher voucher = new Voucher();
		voucher.transactionId = transactionId;
		voucher.transactionType = transactionType;
		voucher.createdAt = createdAt;
		voucher.voucherType = voucherType;
		return voucher;
	}

	public static Voucher fromPreauthorization(Preauthorization preauthorization) {
		if (preauthorization == null) {
			return null;
		}
		Type type = Type.CustomVoucher;
		type.setCustomType(Integer.parseInt(preauthorization.getAmount()),
				""/* preauthorization.getCurrency */, "");

		Voucher voucher = new Voucher();
		voucher.transactionId = preauthorization.getId();
		voucher.transactionType = TransactionType.Preauthorization;
		voucher.createdAt = preauthorization.getCreatedAt();
		voucher.voucherType = type;
		return voucher;
	}

	public static Voucher fromDetails(DetailsObject details) {
		if (details == null) {
			return null;
		}
		Type type = null;
		type = Type.fromDescription(details.getDescription());
		if (type == Type.CustomVoucher) {
			type.setCustomAmount(Integer.parseInt(details.getAmount()));
			type.setCustomCurrency(details.getCurrency());
		}

		Voucher voucher = new Voucher();
		voucher.transactionId = details.getTransId();
		voucher.transactionType = details.getTransactionType();
		voucher.createdAt = details.getCreatedAt();
		voucher.voucherType = type;
		return voucher;
	}

	public String getVoucherCurrency() {
		return voucherType.getCurrency();
	}

	public int getVoucherAmount() {
		return voucherType.getAmount();
	}

	public String getVoucherTextDescription() {
		return voucherType.getDescription();
	}

	public int getVoucherDescription() {
		return voucherType.getId();
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public int getTransactionTypeId() {
		return transactionType.getId();
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public java.util.Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.util.Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getVoucherImageId() {
		return voucherType.getImageId();
	}

	public Type getVoucherType() {
		return voucherType;
	}

	@Override
	public String toString() {
		return voucherType.description + " from " + sf.format(createdAt);
	}

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sf = new SimpleDateFormat("dd.MM HH:mm");
}
