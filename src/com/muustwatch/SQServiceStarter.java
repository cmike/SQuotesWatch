package com.muustwatch;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SQServiceStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context in_context, Intent intent) {

		MUUDebug.Log("SQServiceStarter", "SQServiceStarter got control");
		
		Calendar to_fire = Calendar.getInstance();
		Calendar to_kill = Calendar.getInstance();
		int fire_hours = 19;
		int fire_minutes = 55;
		
		int tokill_hours   = fire_hours;
		int tokill_minutes = fire_minutes + 2;
		
		to_fire.set(Calendar.HOUR_OF_DAY, fire_hours);
		to_fire.set(Calendar.MINUTE, fire_minutes);
		   
		to_kill.set(Calendar.HOUR_OF_DAY, tokill_hours);
		to_kill.set(Calendar.MINUTE, tokill_minutes);
		   
		long adate = to_fire.getTimeInMillis ();
		
		Intent serv_intent = new Intent(in_context, ChkPrice.class);
		
		int requestCode1 = 23; // ???
		PendingIntent pendingIntent = PendingIntent.getService(in_context.getApplicationContext(), 
				requestCode1, serv_intent, PendingIntent.FLAG_CANCEL_CURRENT); // Flag might be adjusted

		AlarmManager alarmManager = (AlarmManager) in_context.getSystemService(Context.ALARM_SERVICE);
		
		alarmManager.set(AlarmManager.RTC_WAKEUP, adate/* System.currentTimeMillis()
				+ (i * 1000) */, pendingIntent);
		
		
		Intent killer = new Intent(in_context, SQServiceKiller.class);
		killer.putExtra("alarm_message", "O'Doyle Rules!");
		 // In reality, you would want to have a static variable for the request code instead of 192837
		int requestCode2 = 192837; // ???
	    PendingIntent sender = PendingIntent.getBroadcast(in_context.getApplicationContext(), 
	    		requestCode2, killer, PendingIntent.FLAG_CANCEL_CURRENT);
		 

		 alarmManager.set(AlarmManager.RTC_WAKEUP, to_kill.getTimeInMillis(), sender);
	}

}
