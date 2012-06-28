package com.muustwatch.db;

/*
"symbol": "GOOG", text not null
"price": "614.67", 
"date": "11/16/2011",
"time": "12:10pm",
"change": "-1.89",
"open": "612.08",
"high": "618.30",
"low": "612.02",
"prev_close": "xxx.xx",
"volume": "1004115"
*/
import com.muustwatch.StockDetailData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class WatchQuotaDBAdapter {
	    // Database fields
		public static final String KEY_ROWID = "_id";
		
		public static final String KEY_SYMBOL = "symbol";
		public static final String KEY_PRICE = "price"; 
		public static final String KEY_DATE = "date";
		public static final String KEY_TIME = "time";
		public static final String KEY_CHANGE = "change";
		public static final String KEY_OPEN = "open";
		public static final String KEY_HIGH = "high";
		public static final String KEY_LOW = "low";
		public static final String KEY_PREVCLOSE = "prev_close";
		public static final String KEY_UBOUND_TRG = "u_bound_trig";
		public static final String KEY_UBOUND_VAL = "u_bound_val";
		public static final String KEY_LBOUND_TRG = "l_bound_trig";
		public static final String KEY_LBOUND_VAL = "l_bound_val";
		public static final String KEY_VOLUME = "volume";
		public static final String KEY_WATCHING = "watching";
		
		public static final String[] Columns = 
		  { 
			KEY_ROWID,
			KEY_SYMBOL,
			KEY_PRICE, 
			KEY_DATE,
			KEY_TIME,
			KEY_CHANGE,
			KEY_OPEN,
			KEY_HIGH,
			KEY_LOW,
			KEY_PREVCLOSE,
			KEY_VOLUME,
			KEY_WATCHING,
			KEY_UBOUND_TRG,
			KEY_UBOUND_VAL,
			KEY_LBOUND_TRG,
			KEY_LBOUND_VAL
		  };
		public static final String[] FieldDescriptors =
			{
			"integer primary key autoincrement",
			"text not null", 
			"text not null",
			"text not null", 
			"text not null",
			"text not null",
			"text not null", 
			"text not null", 
			"text not null",
			"text not null", 
			"text not null", 
			"text not null", 
			"integer not null", 
			"real not null", 
			"integer not null", 
			"real not null"
			};
		
		private static final String DB_TABLE = "watch_quotas";
		public static String DB_Create_CMD_Get () {
			String ret = "create table " + DB_TABLE	+ " ("
					       + Columns[0] + " " + FieldDescriptors[0];
			
			for (int i = 1; i < Columns.length; i++) {
				ret = ret + ", " + Columns[i] + " " + FieldDescriptors[i];
			}
			ret = ret + ");";
			
			return (ret);
		}
		private Context context;
		private SQLiteDatabase db;
		private WatchQuotaDBHelper dbHelper;

		public WatchQuotaDBAdapter(Context context) {
			this.context = context;
		}

		public WatchQuotaDBAdapter open() throws SQLException {
			dbHelper = new WatchQuotaDBHelper(context);
			db = dbHelper.getWritableDatabase();
			return this;
		}

		public void close() {
			dbHelper.close();
		}

		
	/**
		 * Create a new WQuotas If the WQuotas is successfully created return the new
		 * rowId for that note, otherwise return a -1 to indicate failure.
		 */

		public long createWQuota(StockDetailData data_item) {
			ContentValues values = createContentValues(data_item);

			long res = -1;
			
			try {
				res = db.insert(DB_TABLE, null, values);
			} catch (SQLException e) {
				res = -1;
				// e.getMessage(); 
			}
			return res;
		}

		
	/**
		 * Update the WQuotas
		 */

		public boolean updateWQuota(long rowId, StockDetailData data_item) {
			ContentValues values = createContentValues(data_item);

			return db.update(DB_TABLE, values, KEY_ROWID + "=" + rowId, null) > 0;
		}

		
	/**
		 * Deletes WQuotas
		 */

		public boolean deleteWQuota(long rowId) {
			return db.delete(DB_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
		}

		
	/**
		 * Return a Cursor over the list of all WQuotas in the database
		 * 
		 * @return Cursor over all notes
		 */

		public Cursor fetchAllWQuotas() {
			String[] ClmnsTbl = Columns.clone();
			return db.query(DB_TABLE, 
					ClmnsTbl, null, null, null, null, null);
		}

		
	/**
		 * Return a Cursor positioned at the defined WQuotas
		 */

		public Cursor fetchWQuota (long rowId) throws SQLException {
//			new String[] { KEY_ROWID,
//			KEY_SYMBOL,
//			KEY_PRICE, 
//			KEY_DATE,
//			KEY_TIME,
//			KEY_CHANGE,
//			KEY_OPEN,
//			KEY_HIGH,
//			KEY_LOW,
//			KEY_PREVCLOSE,
//			KEY_VOLUME }, 
			String[] ClmnsTbl = Columns.clone();
			Cursor mCursor = db.query(true, DB_TABLE, 
					ClmnsTbl,
					KEY_ROWID + "="
					+ rowId, null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		public DataAndIdx getWQuotaBySymbol (String symbol) {
			String[] ClmnsTbl = Columns.clone();
			Cursor mCursor = db.query(true, DB_TABLE, 
					ClmnsTbl,
					KEY_SYMBOL + "= '"
					+ symbol + "'", null, null, null, null, null);
			
			DataAndIdx  ret = null;
			if (mCursor != null) {
				if (mCursor.moveToFirst()) {
					StockDetailData ret_data = null;
					Long			rowIdx   = (long) -1;

					ret_data = StockDetailData.getDetailsByCursor (mCursor);
					rowIdx = mCursor.getLong(0);

					ret = new DataAndIdx(ret_data, rowIdx);
				}

				mCursor.close();
			}
			
			return (ret);
		}

		
	private ContentValues createContentValues(StockDetailData data_item) {
		ContentValues values = new ContentValues();

		values.put(KEY_SYMBOL, data_item.getSymbol());
		values.put(KEY_PRICE, data_item.getPrice());
		values.put(KEY_DATE, data_item.getTrDate());
		values.put(KEY_TIME, data_item.getTrTime());
		values.put(KEY_CHANGE, data_item.getChange());
		values.put(KEY_OPEN, data_item.getOpen());
		values.put(KEY_HIGH, data_item.getHigh());
		values.put(KEY_LOW, data_item.getLow());
		values.put(KEY_PREVCLOSE, data_item.getPrevClose());
		values.put(KEY_VOLUME, data_item.getVolume());
		values.put(KEY_WATCHING, data_item.getWatching());

		values.put (KEY_UBOUND_TRG, data_item.getIntUBoundTrig());
		values.put (KEY_UBOUND_VAL, data_item.getDblUBoundVal());
		values.put (KEY_LBOUND_TRG, data_item.getIntLBoundTrig());
		values.put (KEY_LBOUND_VAL, data_item.getDblLBoundVal());

		return (values);
	}
}
