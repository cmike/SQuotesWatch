package com.muustwatch;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class Prefs extends Activity {
	// TODO below are constant definition to be replaced by real code
	int  start_hour = 19;
	int  start_minutes = 0;
	int  stop_hour = start_hour;
	int  stop_minutes = start_minutes + 5;
	// TODO Eof temporary constant definition
	boolean is24;
	TimePicker StartTime, StopTime;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);
		
        is24 = DateFormat.is24HourFormat(getApplicationContext()); 
        
        Calendar this_calend = Calendar.getInstance();
        int first_day_a_week = this_calend.getFirstDayOfWeek();
        
        DateFormatSymbols symbols = new DateFormatSymbols(); 
        String[] dayNames = symbols.getShortWeekdays();
        String class_nm = "Days of the Week";
        
        for (String s : dayNames) { 
        	   System.out.print(s + " ");
        	}
        System.out.print("\n");
        
        MUUDebug.Log(class_nm, "Days = " + dayNames.length);
        MUUDebug.Log(class_nm, "First Day = " + first_day_a_week);
        for (int idx=1; idx < dayNames.length; idx++) {
           MUUDebug.Log(class_nm, dayNames[idx]);
        }
        
        StartTime = (TimePicker)findViewById(R.id.StartTime);
        StopTime = (TimePicker)findViewById(R.id.StopTime);

        StartTime.setCurrentHour(start_hour);
        StartTime.setCurrentMinute(start_minutes);
        StartTime.setIs24HourView(is24);

        StopTime.setCurrentHour(stop_hour);
        StopTime.setCurrentMinute(stop_minutes);
        StopTime.setIs24HourView(is24);
	}
}
