package com.muustwatch;

import java.util.Calendar;

import com.muustwatch.PrefMgr.WorkingTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ScheduleServ {
	static final String  cl_nm = "ScheduleServ";
	static final int SERV_START_REQ_CODE = 23; // To be used to restart
	static final int SERV_KILLER_REQ_CODE = 192837; // -"-

	public static void Launch (Context  in_context) {
		WorkingTime  wrk_time = null;

		PrefMgr.Load(in_context);

		if (PrefMgr.isDefined())
			wrk_time = PrefMgr.WorkingTimeGet();

		if (wrk_time != null) {
			Calendar to_fire = wrk_time.start_date;
			Calendar to_kill = wrk_time.stop_date;
			
			
			MUUDebug.Log(cl_nm, "Start time " + to_fire.getTime().toString());
			MUUDebug.Log(cl_nm, "Stop time " + to_kill.getTime().toString());
			

			long adate = to_fire.getTimeInMillis ();

			Intent serv_intent = new Intent(in_context, ChkPrice.class);

			PendingIntent pendingIntent = PendingIntent.getService(in_context.getApplicationContext(), 
					SERV_START_REQ_CODE, serv_intent, PendingIntent.FLAG_CANCEL_CURRENT); // Flag might be adjusted

			AlarmManager alarmManager = (AlarmManager) in_context.getSystemService(Context.ALARM_SERVICE);

			alarmManager.set(AlarmManager.RTC_WAKEUP, adate, pendingIntent);


			Intent killer = new Intent(in_context, SQServiceKiller.class);
			PendingIntent sender = PendingIntent.getBroadcast(in_context.getApplicationContext(), 
					SERV_KILLER_REQ_CODE, killer, PendingIntent.FLAG_CANCEL_CURRENT);


			alarmManager.set(AlarmManager.RTC_WAKEUP, to_kill.getTimeInMillis(), sender);
		}
	   
   }
}
