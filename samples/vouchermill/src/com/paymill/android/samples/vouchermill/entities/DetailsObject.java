package com.paymill.android.samples.vouchermill.entities;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.paymill.android.api.Preauthorization;
import com.paymill.android.api.Transaction;
import com.paymill.android.samples.vouchermill.entities.Voucher.TransactionType;

public class DetailsObject implements Parcelable {

	private static final String TAG = "DeatilsObject";

	public static final String DETAILS_KEY = "details";

	// for transaction consume
	private String transId;
	// common information
	private TransactionType transactionType;
	private String amount;
	private String currency;
	private long createdAt;
	private String description;
	private String statusCode;
	private String paymentType;
	// for Credit Card
	private String creditCardLast4;
	private String cardType;
	// for Direct Debit
	private String accountNumber;
	private String bankCode;

	DetailsObject() {
	}

	DetailsObject(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(transId);
		dest.writeInt(transactionType.getId());
		dest.writeString(amount);
		dest.writeString(currency);
		dest.writeLong(createdAt);
		dest.writeString(description);
		dest.writeString(statusCode);
		dest.writeString(paymentType);
		dest.writeString(creditCardLast4);
		dest.writeString(cardType);
		dest.writeString(accountNumber);
		dest.writeString(bankCode);
	}

	public void readFromParcel(Parcel in) {
		transId = in.readString();
		transactionType = TransactionType.fromId(in.readInt());
		amount = in.readString();
		currency = in.readString();
		createdAt = in.readLong();
		description = in.readString();
		statusCode = in.readString();
		paymentType = in.readString();
		creditCardLast4 = in.readString();
		cardType = in.readString();
		accountNumber = in.readString();
		bankCode = in.readString();
	}

	public static final Parcelable.Creator<DetailsObject> CREATOR = new Creator<DetailsObject>() {

		@Override
		public DetailsObject[] newArray(int size) {
			return new DetailsObject[size];
		}

		@Override
		public DetailsObject createFromParcel(Parcel source) {
			return new DetailsObject(source);
		}
	};

	public static DetailsObject fromTransaction(Transaction transaction) {
		DetailsObject detail = new DetailsObject();
		detail.setTransId(transaction.getId());
		detail.setTransactionType(TransactionType.Transaction);
		detail.setAmount(transaction.getAmount());
		detail.setCurrency(transaction.getCurrency());
		detail.setCreatedAt(transaction.getCreatedAt().getTime());
		detail.setDescription(transaction.getDescription());
		detail.setStatusCode(transaction.getStatus());
		detail.setPaymentType(transaction.getPayment().getType());
		detail.setCreditCardLast4(transaction.getPayment().getLast4());
		detail.setCardType(transaction.getPayment().getCardType());
		detail.setAccountNumber(transaction.getPayment().getAccount());
		detail.setBankCode(transaction.getPayment().getCode());

		return detail;
	}

	public static DetailsObject fromPreauthorization(
			Preauthorization preauthorization) {
		DetailsObject detail = new DetailsObject();
		detail.setTransId(preauthorization.getId());
		detail.setTransactionType(TransactionType.Preauthorization);
		detail.setAmount(preauthorization.getAmount());
		// missing in the current version of SDK
		// detail.setCurrency(preauthorization.getCurrency());
		detail.setCurrency("EUR");
		detail.setCreatedAt(preauthorization.getCreatedAt().getTime());
		detail.setStatusCode(preauthorization.getStatus());
		detail.setPaymentType(preauthorization.getPayment().getType());
		detail.setCreditCardLast4(preauthorization.getPayment().getLast4());
		detail.setCardType(preauthorization.getPayment().getCardType());
		detail.setAccountNumber(preauthorization.getPayment().getAccount());
		detail.setBankCode(preauthorization.getPayment().getCode());

		return detail;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getCreatedAt() {
		return new java.util.Date(createdAt);
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getCreditCardLast4() {
		return creditCardLast4;
	}

	public void setCreditCardLast4(String creditCardLast4) {
		this.creditCardLast4 = creditCardLast4;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

}
