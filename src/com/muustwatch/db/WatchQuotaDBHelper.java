package com.muustwatch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WatchQuotaDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "watch_quotas.db";

	private static final int DATABASE_VERSION = 1;

	public WatchQuotaDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		WatchQuotaTable.onCreate (db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		WatchQuotaTable.onUpgrade(db, oldVersion, newVersion);

	}

}
