/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paymill.android.samples.vouchermill.db;

import com.paymill.android.samples.vouchermill.entities.Voucher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple voucher database access helper class. Defines the basic CRUD
 * operations for VoucherMill, and gives the ability to list all vouchers as
 * well as retrieve or modify a specific voucher.
 */

public class VouchersDbAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TRANSACTION_TYPE = "transaction_type";
	public static final String KEY_VOUCHER_TYPE = "voucher_type";
	public static final String KEY_TRANSACTION_ID = "transaction_id";
	public static final String KEY_CREATED_AT = "created_at";

	private static final String TAG = "VoucherDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table voucher (_id integer primary key autoincrement, "
			+ "transaction_type integer not null, voucher_type integer not null, transaction_id text not null, created_at integer not null);";

	private static final String DATABASE_NAME = "voucherDatabase";
	private static final String DATABASE_TABLE = "voucher";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS vouchers");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public VouchersDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the voucher database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public VouchersDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new voucher using the Voucher object provided. If the voucher is
	 * successfully created return the new rowId for that voucher, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param voucher
	 *            the voucher to be inserted into database
	 * @return rowId or -1 if failed
	 */
	public long createVoucher(Voucher voucher) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TRANSACTION_TYPE, voucher.getTransactionType()
				.getId());
		initialValues.put(KEY_VOUCHER_TYPE, voucher.getVoucherDescription());
		initialValues.put(KEY_TRANSACTION_ID, voucher.getTransactionId());
		initialValues.put(KEY_CREATED_AT, voucher.getCreatedAt().getTime());

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the voucher with the given rowId
	 * 
	 * @param rowId
	 *            id of voucher to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteVoucher(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all vouchers in the database
	 * 
	 * @return Cursor over all vouchers
	 */
	public Cursor fetchAllVouchers() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TRANSACTION_TYPE, KEY_VOUCHER_TYPE, KEY_TRANSACTION_ID,
				KEY_CREATED_AT }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the voucher that matches the given rowId
	 * 
	 * @param rowId
	 *            id of voucher to retrieve
	 * @return Cursor positioned to matching voucher, if found
	 * @throws SQLException
	 *             if voucher could not be found/retrieved
	 */
	public Cursor fetchVoucher(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TRANSACTION_TYPE, KEY_VOUCHER_TYPE, KEY_TRANSACTION_ID,
				KEY_CREATED_AT }, KEY_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the voucher that matches the given transactionID
	 * 
	 * @param transactionID
	 *            transactionID of voucher to retrieve
	 * @return Cursor positioned to matching voucher, if found
	 * @throws SQLException
	 *             if voucher could not be found/retrieved
	 */
	public Cursor fetchVoucher(String transactionID) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TRANSACTION_TYPE, KEY_VOUCHER_TYPE, KEY_TRANSACTION_ID,
				KEY_CREATED_AT }, KEY_TRANSACTION_ID + "=" + transactionID, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}
	/**
	 * Update the voucher using the details provided. The voucher to be updated
	 * is specified using the rowId, and it is altered to use the voucher values
	 * passed in
	 * 
	 * @param rowId
	 *            id of voucher to update
	 * @param voucher
	 *            values to set voucher to
	 * @return true if the voucher was successfully updated, false otherwise
	 */
	public boolean updateVoucher(long rowId, Voucher voucher) {
		ContentValues args = new ContentValues();
		args.put(KEY_TRANSACTION_TYPE, voucher.getTransactionTypeId());
		args.put(KEY_VOUCHER_TYPE, voucher.getVoucherDescription());
		args.put(KEY_TRANSACTION_ID, voucher.getTransactionId());
		args.put(KEY_CREATED_AT, voucher.getCreatedAt().getTime());

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
