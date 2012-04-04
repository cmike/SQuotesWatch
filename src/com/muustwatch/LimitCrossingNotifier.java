package com.muustwatch;

import java.util.Hashtable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class LimitCrossingNotifier {
	public static enum LimitType {
		UpperLimit, LowerLimit;
	}
	public static class SymbNotifyInfo {
		public String mSymbol = null;
		public int    mLowerID = -1;
		public int    mLowerCount = 0;
		public int	  mUpperID = -1;
		public int    mUpperCount = 0;
	}
	
	public LimitCrossingNotifier (Context caller) {
		mContext = caller;
	}
	public void Release () {
		idCountInfo.clear();
	}
	Context mContext;
	Hashtable<String, SymbNotifyInfo> idCountInfo = new Hashtable<String, SymbNotifyInfo>();
	int UniqNotifyID = -1;
	private int UniqNotifyID_get () {
		UniqNotifyID++;
		return (UniqNotifyID);
	}
	
	void LimitCrossingNotify (String symbol, String cur_price, String limit, LimitType l_type) {
		int iconID = -1;
		String limit_t_name = null;
		int notificationID = -1;
		int notificationCounter = -1;
		SymbNotifyInfo this_info = idCountInfo.get(symbol);
		if (this_info == null) {
			this_info = new SymbNotifyInfo();
			idCountInfo.put(symbol, this_info);
		}
		
		switch (l_type) {
			case LowerLimit:
				this_info.mLowerCount += 1;
				if (this_info.mLowerID == -1)
					this_info.mLowerID = UniqNotifyID_get();
				
				iconID = R.drawable.icon_red_notif_dollars;
				limit_t_name = "lower";
				notificationID = this_info.mLowerID;
				notificationCounter = this_info.mLowerCount;
				break;
			case UpperLimit:
				this_info.mUpperCount += 1;
				if (this_info.mUpperID == -1)
					this_info.mUpperID = UniqNotifyID_get();
				iconID = R.drawable.icon_green_notif_dollars;
				limit_t_name = "upper";
				notificationID = this_info.mUpperID;
				notificationCounter = this_info.mUpperCount;
				break;
		}
		
		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(iconID, symbol, System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

//		Intent intent = new Intent(this, PrtfSymbols.class);
		Intent intent = new Intent();
		PendingIntent activity = PendingIntent.getActivity(mContext, 0, intent, 0);
		
		String ttl = "Symbol " + symbol;
		String txt =  "Price " + cur_price + " crossed " + limit_t_name + " limit " + limit;
		notification.setLatestEventInfo(mContext, ttl, txt, activity);
		notification.number = notificationCounter;
		notificationManager.notify(notificationID, notification);
	}
}
