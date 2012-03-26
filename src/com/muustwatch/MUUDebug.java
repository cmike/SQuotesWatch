package com.muustwatch;

import android.util.Log;

public final class MUUDebug {
	//set to false to allow compiler to identify and eliminate
	//unreachable code
	public static final boolean ON = true;
	public static final boolean REAL_LOAD = false;
	public static final boolean LOOP_READ = true;
	public static final boolean LOGGING = true;
	public static void  Log (String class_nm, String to_log) {
		if (LOGGING)
			Log.i(class_nm, to_log);
	}
}
