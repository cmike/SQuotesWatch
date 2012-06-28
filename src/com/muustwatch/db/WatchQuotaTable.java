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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WatchQuotaTable {
		
		public static void onCreate(SQLiteDatabase database) {
			String DATABASE_CREATE = WatchQuotaDBAdapter.DB_Create_CMD_Get();
			database.execSQL(DATABASE_CREATE);
		}

		public static void onUpgrade(SQLiteDatabase database, int oldVersion,
				int newVersion) {
			Log.w(WatchQuotaTable.class.getName(), "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			database.execSQL("DROP TABLE IF EXISTS watch_quotas");
			onCreate(database);
		}
}
