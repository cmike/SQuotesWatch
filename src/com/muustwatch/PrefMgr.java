package com.muustwatch;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefMgr {
	static final String PREFS_SET = "SQuotesWatchPrefs";
	static final String ACTIVE_DAYS = "AciveDays";
	static final String START_H = "StartHour";
	static final String START_M = "StartMinutes";
	static final String STOP_H = "StopHour";
	static final String STOP_M = "StopMinutes";
	static final String REQ_INTVAL_MINUTES = "ReqIntvalMinutes";

	static AppPrefs m_AppPrefs = null;
	
	static class WorkingTime {
		public Calendar start_date;
		public Calendar stop_date;
		
		WorkingTime (Calendar in_start, Calendar in_stop) {
			start_date = in_start;
			stop_date  = in_stop;
		}
	}
	
	static class AppPrefs {
		static final String class_nm = "AppPrefs";
		int	m_start_hours;
		int m_start_minutes;
		
		int m_stop_hours;
		int m_stop_minutes;
		
		int m_request_minutes;
		
		boolean[] active_days = null;

		AppPrefs () {
			m_start_hours = -1;
			m_start_minutes =-1;
			
			m_stop_hours = -1;
			m_stop_minutes =-1;
			
			m_request_minutes = -1;
			
		    active_days = new boolean[7];
		    for (int _i = 0; _i < 7; _i++)
		    	active_days[_i] = false;
		}
		boolean _ValidTime (int hours, int minutes) {
			return (hours >= 0 && hours <= 23 &&
					minutes >= 0 && minutes <= 59);
		}
		public void RequestIntervalSet (int minutes) { m_request_minutes = minutes; }
		public int  RequestIntervalGet () { return (m_request_minutes); }
		public void StartTimeSet (int hours, int minutes) {
			if (_ValidTime (hours, minutes)) {
				m_start_hours = hours;
				m_start_minutes = minutes;
			} else {
				m_start_hours = -1;
				m_start_minutes =-1;
			}			
		}
		public int StartHourGet () {
			return (_ValidTime (m_start_hours, m_start_minutes) ? 
								m_start_hours : -1);
		}
		public int StartMinutesGet () {
			return (_ValidTime (m_start_hours, m_start_minutes) ? 
								m_start_minutes : -1);
		}

		public void StopTimeSet (int hours, int minutes) {
			if (_ValidTime (hours, minutes)) {
				m_stop_hours = hours;
				m_stop_minutes = minutes;
			} else {
				m_stop_hours = -1;
				m_stop_minutes =-1;
			}			
		}
		
		public int StopHourGet () {
			return (_ValidTime (m_stop_hours, m_stop_minutes) ? 
								m_stop_hours : -1);
		}
		public int StopMinutesGet () {
			return (_ValidTime (m_stop_hours, m_stop_minutes) ? 
								m_stop_minutes : -1);
		}

		public void ActiveDaysFromString (String coded_string)  {
			int n_chars = (coded_string != null) ? coded_string.length() : 0;
			int _i;
			char  one_char;
			
			for (_i = 0; _i < n_chars; _i++) {
				one_char = coded_string.charAt(_i);
				active_days[_i] = (one_char == '1');
			}
			for (_i = n_chars; _i < 7; _i++) 
				active_days[_i] = false;			
		}
		public String ActiveDaysToString () {
			String coded = "";
			
			if (active_days != null) {
				int _i;
				for (_i = 0; _i < active_days.length; _i++)
					coded += (active_days[_i]) ? "1" : "0";
				
				_i = coded.length();
				for ( ; _i < 7; _i++)
					coded += "0";
			}
			return (coded);
		}
		public boolean isDayActive (int day_a_week) {
			boolean ret = false;
			int idx = day_a_week - 1;
			
			if (idx >= 0 && idx < 7)
				ret = active_days[idx];
			
			return (ret);
		}
		
		public void SetDayIsActive (int day_a_week, boolean isActive) {
			int idx = day_a_week - 1;
			
			if (idx >= 0 && idx < 7)
				active_days[idx] = isActive;
		}
		
		public boolean isDefined () {
			boolean ret = _ValidTime (m_start_hours, m_start_minutes);
			
			if (ret)
				ret = _ValidTime (m_stop_hours, m_stop_minutes);
			
			if (ret)
				ret = (m_request_minutes > 0);
			
			if (ret) {
				boolean atleast_one = false;
				for (int _i = 0; _i < 7 && !atleast_one; _i++)
					atleast_one = active_days[_i];
				
				ret = atleast_one;
			}
			return (ret);
		}
		
		private Calendar closest_active_get (Calendar since_this) {
			Calendar ret = null;
			Calendar closest = (Calendar) since_this.clone();
			
			int this_day_code = since_this.get(Calendar.DAY_OF_WEEK);
			int _i_day = 7;
			
			while (!isDayActive(this_day_code) && _i_day > 0) {
				closest.add(Calendar.DAY_OF_WEEK, 1);
				this_day_code = closest.get(Calendar.DAY_OF_WEEK);
				_i_day--;
			}

			if (_i_day > 0)
				ret = closest;
			else if (MUUDebug.ON)
				throw new Error ("No Active Days Found");
			
			return (ret);
		}
		public WorkingTime WorkingTimeGet () {
			WorkingTime ret = null;
			Calendar ret_start = null;
			Calendar ret_stop  = null;

			if (isDefined()) {
				boolean next_day_stop = false;

				Calendar right_now = Calendar.getInstance();
				Calendar tmp_closest_start = (Calendar) right_now.clone();
				Calendar tmp_closest_stop = (Calendar) tmp_closest_start.clone();
				Calendar closest_start = null;
				Calendar closest_stop  = null;

				tmp_closest_start.set(Calendar.HOUR_OF_DAY, m_start_hours);
				tmp_closest_start.set(Calendar.MINUTE, m_start_minutes);

				tmp_closest_stop.set(Calendar.HOUR_OF_DAY, m_stop_hours);
				tmp_closest_stop.set(Calendar.MINUTE, m_stop_minutes);
				
				if (tmp_closest_start.after(tmp_closest_stop)) {
					tmp_closest_stop.add(Calendar.DAY_OF_WEEK, 1);
					next_day_stop = true;
				}
				
				if (!right_now.before(tmp_closest_stop))
					tmp_closest_start.add(Calendar.DAY_OF_WEEK, 1);

				closest_start = closest_active_get(tmp_closest_start);
				if (closest_start != null) {
					int this_day_code = closest_start.get(Calendar.DAY_OF_WEEK);

					MUUDebug.Log(class_nm, "closest_start =  " + closest_start.getTime().toString());
					closest_stop = (Calendar) closest_start.clone();
					closest_stop.set(Calendar.HOUR_OF_DAY, m_stop_hours);
					closest_stop.set(Calendar.MINUTE, m_stop_minutes);
					if (next_day_stop)
						closest_stop.add(Calendar.DAY_OF_WEEK, 1);
					
					if (this_day_code == right_now.get(Calendar.DAY_OF_WEEK)) {
						MUUDebug.Log(class_nm, "closest_start(this_day) =  " + closest_start.getTime().toString());
						if (right_now.before(closest_start))
							ret_start = (Calendar) closest_start.clone();
						else if (right_now.before(closest_stop)) {
							ret_start = (Calendar) right_now.clone();
							ret_start.add (Calendar.MINUTE, 2);
						}
						else if (MUUDebug.ON)
						  throw new Error ("closest_active_get()");
					}
					else {
						ret_start = (Calendar) closest_start.clone();
					MUUDebug.Log(class_nm, "closest_start(not this) =  " + closest_start.getTime().toString());
					}
					
					ret_stop = (Calendar) closest_stop.clone();
					if (ret_start == null)
						MUUDebug.Log(class_nm, "ret_start = null");
					else
					  MUUDebug.Log(class_nm, "ret_start =  " + ret_start.getTime().toString());
					
					MUUDebug.Log(class_nm, "ret_stop =  " + ret_stop.getTime().toString());
				}
				else
					MUUDebug.Log(class_nm, "closest_active_get()==null");
			}
			
			if (ret_start != null && ret_stop != null)
				ret = new WorkingTime(ret_start, ret_stop);
			else if (ret_start == null && ret_stop == null)
				MUUDebug.Log(class_nm, "Both Date null");
			else if (ret_start == null)
				MUUDebug.Log(class_nm, "Start Date null");
			else
				MUUDebug.Log(class_nm, "Stop Date null");
			
			return (ret);
		}

	}

	boolean day_coded_string_isOK (String to_check) {
		int n_chars = (to_check != null) ? to_check.length() : 0;
		boolean isOK = (n_chars == 7);
		char  one_char;
		
		for (int _i = 0; _i < n_chars && isOK; _i++) {
			one_char = to_check.charAt(_i);
			isOK = (one_char == '1' || one_char == '0');
		}
		
		return (isOK);
	}
	public static void Load (Context app_ctx) {
		String active_days_coded;
		int _st_hours, _st_minutes, _end_hours, _end_minutes, _r_intval;
		
        SharedPreferences mySharedPreferences = app_ctx.getSharedPreferences(
        		PREFS_SET, Activity.MODE_PRIVATE);
        active_days_coded = mySharedPreferences.getString(ACTIVE_DAYS, "");
        
        _st_hours = mySharedPreferences.getInt(START_H, -1);
        _st_minutes = mySharedPreferences.getInt(START_M, -1);
        
        _end_hours = mySharedPreferences.getInt(STOP_H, -1);
        _end_minutes = mySharedPreferences.getInt(STOP_M, -1);
        
        _r_intval = mySharedPreferences.getInt(REQ_INTVAL_MINUTES, -1);
        
        if (m_AppPrefs == null)
        	m_AppPrefs = new AppPrefs();
        
        m_AppPrefs.StartTimeSet(_st_hours, _st_minutes);
        m_AppPrefs.StopTimeSet(_end_hours, _end_minutes);
        m_AppPrefs.RequestIntervalSet(_r_intval);
        m_AppPrefs.ActiveDaysFromString (active_days_coded);
	}
	
	public static void Save (Context app_ctx) {
		
		SharedPreferences mySharedPreferences = app_ctx.getSharedPreferences(
				PREFS_SET, Activity.MODE_PRIVATE);
		SharedPreferences.Editor  editor = mySharedPreferences.edit();
		
        if (m_AppPrefs == null) {
        	editor.remove(ACTIVE_DAYS);
        	editor.remove(START_H);
        	editor.remove(START_M);
        	editor.remove(STOP_H);
        	editor.remove(STOP_M);
        	editor.remove(REQ_INTVAL_MINUTES);
        } else {
		    String active_days_coded = m_AppPrefs.ActiveDaysToString();
		    
		    editor.putString(ACTIVE_DAYS, active_days_coded);
		    
			int _st_hours    = m_AppPrefs.StartHourGet();
			int _st_minutes  = m_AppPrefs.StartMinutesGet();
			
			editor.putInt(START_H, _st_hours);
			editor.putInt(START_M, _st_minutes);
			
			int _end_hours   = m_AppPrefs.StopHourGet();
			int _end_minutes = m_AppPrefs.StopMinutesGet();
		    
			editor.putInt(STOP_H, _end_hours);
			editor.putInt(STOP_M, _end_minutes);
			
			int _r_int = m_AppPrefs.RequestIntervalGet();
			
			editor.putInt(REQ_INTVAL_MINUTES, _r_int);
        }
		
		editor.commit();
	}
	public static boolean isDayActive (int day_a_week) {
		boolean ret = (m_AppPrefs == null) ? 
						false: m_AppPrefs.isDayActive (day_a_week);
		return (ret);
	}
	public static boolean isDefined () {
		boolean ret = (m_AppPrefs != null);
		
		if (ret)
			m_AppPrefs.isDefined();
		
		return (ret);
	}
	public static void SetDayIsActive (int day_a_week, boolean isActive) {
		if (m_AppPrefs == null)
			m_AppPrefs = new AppPrefs();
		
		m_AppPrefs.SetDayIsActive(day_a_week, isActive);
	}
	public static void StartTimeSet (int hours, int minutes) {
		if (m_AppPrefs == null)
			m_AppPrefs = new AppPrefs();
		
		m_AppPrefs.StartTimeSet (hours, minutes);
	}
	public static void StopTimeSet (int hours, int minutes) {
		if (m_AppPrefs == null)
			m_AppPrefs = new AppPrefs();
		
		m_AppPrefs.StopTimeSet (hours, minutes);
	}
	public static int StartHourGet () {
		int ret = -1;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.StartHourGet();
		
		return (ret);
	}
	public static int StartMinutesGet () {
		int ret = -1;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.StartMinutesGet();
		
		return (ret);
	}
	public static int StopHourGet () {
		int ret = -1;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.StopHourGet();
		
		return (ret);
	}
	public static int StopMinutesGet () {
		int ret = -1;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.StopMinutesGet();
		
		return (ret);
	}
	public static int RequestIntervalGet () {
		int ret = -1;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.RequestIntervalGet();
		
		return (ret);
	}
	public static void RequestIntervalSet (int minutes) {
		if (m_AppPrefs == null)
			m_AppPrefs = new AppPrefs();
		
		m_AppPrefs.RequestIntervalSet(minutes);
	}
	public static WorkingTime WorkingTimeGet () {
		WorkingTime ret = null;
		
		if (m_AppPrefs != null)
			ret = m_AppPrefs.WorkingTimeGet();
		
		return (ret);
	}
}
