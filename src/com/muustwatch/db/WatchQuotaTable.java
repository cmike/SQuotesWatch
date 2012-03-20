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
		private static final String DATABASE_CREATE = "create table watch_quotas "
				+ "(_id integer primary key autoincrement, "
				+ "symbol text not null, " 
				+ "price text not null, "
				+ "date text not null, " 
				+ "time text not null, "
				+ "change text not null, "
				+ "open text not null, " 
				+ "high text not null, " 
				+ "low text not null, "
				+ "prev_close text not null, " 
				+ "volume text not null, " 
				+ "u_bound_trig integer not null, " 
				+ "u_bound_val  real not null, " 
				+ "l_bound_trig integer not null, " 
				+ "l_bound_val  real not null);";

		public static void onCreate(SQLiteDatabase database) {
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
