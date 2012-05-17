package com.muustwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SQServiceStarter extends BroadcastReceiver {

	static final String  cl_nm = "SQServiceStarter";
	@Override
	public void onReceive(Context in_context, Intent intent) {

		MUUDebug.Log(cl_nm, "SQServiceStarter got control");

		ScheduleServ.Launch(in_context);
	}

}
