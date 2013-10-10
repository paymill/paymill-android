package com.paymill.android.samples.vouchermill.ui.fragments;

import com.paymill.android.factory.PMPaymentParams;

public interface VoucherInfo {

	public static class VoucherInfoValidationException extends Exception {

		private static final long serialVersionUID = -1403426274403304883L;

	}

	public PMPaymentParams getInfo() throws VoucherInfoValidationException;
}
