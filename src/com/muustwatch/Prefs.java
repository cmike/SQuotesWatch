package com.muustwatch;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class Prefs extends Activity {
	int  start_hour = -1;
	int  start_minutes = -1;
	int  stop_hour = -1;
	int  stop_minutes = -1;
	Context this_ctx = null;
	boolean is24;
	TimePicker StartTime, StopTime;
	EditText  ReqInterval;
	ToggleButton[] UI_WeekDays = new ToggleButton[7];
	int[] UI_WeekDays_IDS = {R.id.toggleButton1, R.id.toggleButton2, R.id.toggleButton3, 
				R.id.toggleButton4, R.id.toggleButton5, R.id.toggleButton6, R.id.toggleButton7};
	int[] LocalizedWeekDayIDs = new int[7];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);
		
		this_ctx = getApplicationContext();
        is24 = DateFormat.is24HourFormat(this_ctx);
        PrefMgr.Load(this_ctx);
        
        Calendar this_calend = Calendar.getInstance();
        int first_day_a_week = this_calend.getFirstDayOfWeek();
        
        int _dayID, _i;
        for (_dayID = first_day_a_week, _i = 0; _dayID <= Calendar.SATURDAY; _dayID++, _i++)
        	LocalizedWeekDayIDs[_i] = _dayID;
        for (_dayID = Calendar.SUNDAY; _dayID < first_day_a_week; _dayID++, _i++)
        	LocalizedWeekDayIDs[_i] = _dayID;
        
        DateFormatSymbols symbols = new DateFormatSymbols(); 
        String[] dayNames = symbols.getShortWeekdays();
        String class_nm = "Days of the Week";
        
        for (String s : dayNames) { 
        	   System.out.print(s + " ");
        	}
        System.out.print("\n");
        
        MUUDebug.Log(class_nm, "Days = " + dayNames.length);
        MUUDebug.Log(class_nm, "First Day = " + first_day_a_week);
        for (int idx=0; idx < 7; idx++) {
           MUUDebug.Log(class_nm, dayNames[LocalizedWeekDayIDs[idx]]);
        }
        
        for (_i = 0; _i < 7; _i++) {
          UI_WeekDays[_i] = (ToggleButton)findViewById(UI_WeekDays_IDS[_i]);
          
          String short_D_name = dayNames[LocalizedWeekDayIDs[_i]];
          int	 _i_last = short_D_name.length();
          
          _i_last = (_i_last <= 3) ? _i_last : 3;
          short_D_name = short_D_name.substring (0, _i_last);
          
          UI_WeekDays[_i].setTextOn(short_D_name);
          UI_WeekDays[_i].setTextOff(short_D_name);
          
          UI_WeekDays[_i].setChecked(PrefMgr.isDayActive(_i));
        }
        StartTime = (TimePicker)findViewById(R.id.StartTime);
        StopTime = (TimePicker)findViewById(R.id.StopTime);

        start_hour = PrefMgr.StartHourGet();
        if (start_hour != -1) {
        	StartTime.setCurrentHour(start_hour);
        	
        	start_minutes = PrefMgr.StartMinutesGet();
        	StartTime.setCurrentMinute(start_minutes);
        }
        StartTime.setIs24HourView(is24);

        stop_hour = PrefMgr.StopHourGet();
        if (stop_hour != -1) {
        	StopTime.setCurrentHour(stop_hour);
        	
        	stop_minutes = PrefMgr.StopMinutesGet();
        	StopTime.setCurrentMinute(stop_minutes);
        }
        StopTime.setIs24HourView(is24);
        
        ReqInterval = (EditText)findViewById(R.id.ReqInt);
        
        Integer r_interval = PrefMgr.RequestIntervalGet();
        
        if (r_interval > 0) {
          String text_minutes = r_interval.toString();
          ReqInterval.setText(text_minutes);
        }
	}
	
	@Override
	public void onPause () {
		
		for (int _i = 0; _i < 7; _i++) {
			PrefMgr.SetDayIsActive(_i, UI_WeekDays[_i].isChecked());
	    }
		
		start_hour    = StartTime.getCurrentHour();
		start_minutes = StartTime.getCurrentMinute();
		PrefMgr.StartTimeSet(start_hour, start_minutes);
		
		stop_hour     = StopTime.getCurrentHour();
		stop_minutes  = StopTime.getCurrentMinute();
		PrefMgr.StopTimeSet(stop_hour, stop_minutes);
		
		boolean is_int_ok = true;
		int r_int = -1;
		try {
			r_int = Integer.parseInt(ReqInterval.getText().toString());
		} catch (Exception e) {
			is_int_ok = false;
		}

		if (is_int_ok && r_int > 0)
			PrefMgr.RequestIntervalSet(r_int);
		
		PrefMgr.Save(this_ctx);
		super.onPause();
	}
}
