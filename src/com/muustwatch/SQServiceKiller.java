package com.muustwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SQServiceKiller extends BroadcastReceiver {
    static final String cl_nm = "SQServiceKiller";
	@Override
	public void onReceive(Context in_context, Intent arg1) {

		Kill (in_context);
		
		MUUDebug.Log(cl_nm, "Re-starting Service");		
		ScheduleServ.Launch(in_context);
		MUUDebug.Log(cl_nm, "Service all Set");		
	}

	public static boolean Kill (Context in_context) {
		
	    
		boolean ret = ChkPrice.isRunning();		
		Intent serv_intent = new Intent(in_context, ChkPrice.class);
		if (ret) {
			
			// TODO wait until stopped;

			if (PrtfSymbols.isServBound()) {
			serv_intent.putExtra(ChkPrice.ReqStringName, ChkPrice.MSG_UNBIND_ALL_REQUEST);
			in_context.getApplicationContext().startService(serv_intent);
			}
			in_context.getApplicationContext().stopService(serv_intent);
		}
		
		if (ret) {
			MUUDebug.Log(cl_nm, "Service stop requested");
		} else {
			MUUDebug.Log(cl_nm, "Service inactive");
		}
		
		return (ret);
	}
}
