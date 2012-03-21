package com.muustwatch;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.muustwatch.db.WatchQuotaDBAdapter;

public class ChkPrice extends Service {
	private Timer timer = new Timer();
	private static final long UPDATE_INTERVAL = 5000;
	private final IBinder mBinder = new MyBinder();
	private WatchQuotaDBAdapter dbHelper = null;
	private StockDtlList dtlList;
	private Object	stick = new Object();
	private boolean pass_is_active = false;
	int idx_in_list_being_proc = -1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new WatchQuotaDBAdapter(ChkPrice.this);
		
		pollForUpdates();
	}

	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
				if (msg.what == 1) { // TODO to be prepared array of results
					
					StockDetailData loaded_data_item = (StockDetailData) msg.obj;
					StockDetailData src_data_item = dtlList.get(idx_in_list_being_proc);
					
					src_data_item.Update(loaded_data_item);
					
					Log.i(getClass().getSimpleName(), "Loaded: " + loaded_data_item.getSymbol());
					synchronized (stick) {
						stick.notify();
					}
				} else {
					Bundle err_data = msg.getData();
					String err_msg = null;
					
					if (err_data != null)
						err_msg = err_data.getString("err_msg");
					
					if (err_msg == null)
						err_msg = "Error!";
					
					Log.i(getClass().getSimpleName(), "Error requesting data: " + err_msg);
				}
			}
		};

	private void GetStockDtl (String symb) {
		LoadDeatails loader = new LoadDeatails(symb, handler);
		Thread t = new Thread(loader);
		t.start();
	}
	
	private void pollForUpdates() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (pass_is_active)
					return;
				
				pass_is_active = true;
				dbHelper.open();
				Cursor crsFull = dbHelper.fetchAllWQuotas();
				idx_in_list_being_proc = -1;
				if (crsFull.getCount() > 0) {
					dtlList = new StockDtlList(crsFull);

					int n_in_list = dtlList.size();
					
					Log.i(getClass().getSimpleName(), "Staring Loop");
					for (idx_in_list_being_proc = 0; 
						 idx_in_list_being_proc < n_in_list; 
						 idx_in_list_being_proc++) {
						StockDetailData this_item = dtlList.get(idx_in_list_being_proc);

						Log.i(getClass().getSimpleName(), "Processing: " + this_item.getSymbol());
						GetStockDtl (this_item.getSymbol());
						synchronized (stick) {
							try {
								stick.wait();
							} catch (InterruptedException e) {
								// e.printStackTrace();
								Log.i(getClass().getSimpleName(), "Loop interrupted");
								break;
							}
						}
						Long rowID = dtlList.getDBrowID (idx_in_list_being_proc);
						dbHelper.updateWQuota (rowID, this_item);
						Log.i(getClass().getSimpleName(), "Keep Going");
					}
					Log.i(getClass().getSimpleName(), "Loop passed");
					idx_in_list_being_proc = -1;
					
				}
				crsFull.deactivate();
				dbHelper.close();				
				pass_is_active = false;
			}
		}, 0, UPDATE_INTERVAL);
		Log.i(getClass().getSimpleName(), "Check Price's Timer started.");
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroy();
		Log.i(getClass().getSimpleName(), "Check Price's Timer stopped.");

	}
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	public class MyBinder extends Binder {
		ChkPrice getService() {
			return ChkPrice.this;
		}
	}

}
