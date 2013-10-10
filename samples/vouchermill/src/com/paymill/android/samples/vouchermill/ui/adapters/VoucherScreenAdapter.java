package com.paymill.android.samples.vouchermill.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.paymill.android.samples.vouchermill.entities.Voucher;
import com.paymill.android.samples.vouchermill.entities.Voucher.Type;
import com.paymill.android.samples.vouchermill.ui.fragments.CustomVoucherFragment;
import com.paymill.android.samples.vouchermill.ui.fragments.StandardVoucherFragment;

public class VoucherScreenAdapter extends FragmentPagerAdapter {

	public VoucherScreenAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		if (Voucher.Type.values()[index] == Type.CustomVoucher) {
			return new CustomVoucherFragment();
		} else {
			return StandardVoucherFragment
					.instance(Voucher.Type.values()[index]);
		}

	}

	@Override
	public int getCount() {
		return Voucher.Type.values().length;
	}

}
