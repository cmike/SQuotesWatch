package com.muustwatch;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class Prefs extends Activity {
	// TODO below are constant definition to be replaced by real code
	int  start_hour = 19;
	int  start_minutes = 0;
	int  stop_hour = start_hour;
	int  stop_minutes = start_minutes + 5;
	// TODO Eof temporary constant definition
	boolean is24;
	TimePicker StartTime, StopTime;
	ToggleButton[] UI_WeekDays = new ToggleButton[7];
	int[] UI_WeekDays_IDS = {R.id.toggleButton1, R.id.toggleButton2, R.id.toggleButton3, 
				R.id.toggleButton4, R.id.toggleButton5, R.id.toggleButton6, R.id.toggleButton7};
	int[] LocalizedWeekDayIDs = new int[7];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);
		
        is24 = DateFormat.is24HourFormat(getApplicationContext()); 
        
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
          UI_WeekDays[_i].setTextOn(dayNames[LocalizedWeekDayIDs[_i]].subSequence(0, 3));
          UI_WeekDays[_i].setTextOff(dayNames[LocalizedWeekDayIDs[_i]].subSequence(0, 3));
          
          UI_WeekDays[_i].setChecked(true);
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
