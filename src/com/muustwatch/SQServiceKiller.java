package com.muustwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SQServiceKiller extends BroadcastReceiver {
    static final String cl_nm = "SQServiceKiller";
	@Override
	public void onReceive(Context in_context, Intent arg1) {

		Intent serv_intent = new Intent(in_context, ChkPrice.class);
		
		if (in_context.getApplicationContext().stopService(serv_intent)) {
			MUUDebug.Log(cl_nm, "Service stopped");
		} else {
			MUUDebug.Log(cl_nm, "Service was not stopped");
		}
		
		MUUDebug.Log(cl_nm, "Re-starting Service");		
		ScheduleServ.Launch(in_context);
	}

}
