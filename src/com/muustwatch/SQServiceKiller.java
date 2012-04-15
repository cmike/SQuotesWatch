package com.muustwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SQServiceKiller extends BroadcastReceiver {

	@Override
	public void onReceive(Context in_context, Intent arg1) {

		Intent serv_intent = new Intent(in_context, ChkPrice.class);
		
		if (in_context.getApplicationContext().stopService(serv_intent)) {
			MUUDebug.Log("SQServiceKiller", "Service stopped");
		} else {
			MUUDebug.Log("SQServiceKiller", "Service was not stopped");
		}
	}

}
