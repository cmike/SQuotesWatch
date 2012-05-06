package com.muustwatch;

import java.util.Hashtable;
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

import com.muustwatch.LimitCrossingNotifier.LimitType;
import com.muustwatch.StockDetailData.StockDtlFired;
import com.muustwatch.db.WatchQuotaDBAdapter;

public class ChkPrice extends Service {
	private Timer timer = new Timer();
	private long UPDATE_INTERVAL = 5000;
	private final String class_nm = getClass().getSimpleName();
	private final IBinder mBinder = new MyBinder();
	private WatchQuotaDBAdapter dbHelper = null;
	private StockDtlList dtlList;
	private boolean pass_is_active = false;
	Hashtable<String, StockDtlFired> sv_fired_stat = new Hashtable<String, StockDtlFired>();
	int idx_in_list_being_proc = -1;
	
	private class LoadStick extends Object {
		boolean completion = false;
		
		void notify (boolean is_OK) {
			completion = is_OK;
			super.notify();
		}
	}

	private LoadStick	stick = new LoadStick();
	LimitCrossingNotifier notifier = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		if (PrefMgr.isDefined())
		{
			int r_intval = PrefMgr.RequestIntervalGet();
			
			if (r_intval > 0) {
				
				MUUDebug.Log(class_nm, "Request interval: " + r_intval);
				UPDATE_INTERVAL = r_intval * 1000 * 60;
				notifier = new LimitCrossingNotifier (getApplicationContext());
				dbHelper = new WatchQuotaDBAdapter(ChkPrice.this);
				
				pollForUpdates();
			}
		}
		
	}

	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
				if (msg.what == 1) { // TODO to be prepared array of results
					
					StockDetailData loaded_data_item = (StockDetailData) msg.obj;
					StockDetailData src_data_item = dtlList.get(idx_in_list_being_proc);
					
					boolean upper_was_fired = src_data_item.is_UBoundFired();
					boolean lower_was_fired = src_data_item.is_LBoundFired();
					
					StockDtlFired need_fire = src_data_item.Check (loaded_data_item);
					
					StockDtlFired sv_this_stat = new StockDtlFired ();
					
					sv_this_stat.UpperToFire = src_data_item.is_UBoundFired();
					sv_this_stat.LowerToFire = src_data_item.is_LBoundFired();
					sv_fired_stat.put(src_data_item.getSymbol(), sv_this_stat);
					
					// Call below most likely useless: there is no need
					// update memory object, since it will be anyway release
					// upon next run() cycle, and all the data gets put
					// into DB
					src_data_item.Update(loaded_data_item);
					
					if (need_fire.UpperToFire) {
						notifier.LimitCrossingNotify(src_data_item.getSymbol(), loaded_data_item.getPrice(), 
								src_data_item.getUBoundVal(), LimitType.UpperLimit);

					}
					if (need_fire.LowerToFire) {
						notifier.LimitCrossingNotify(src_data_item.getSymbol(), loaded_data_item.getPrice(), 
								src_data_item.getLBoundVal(), LimitType.LowerLimit);

					}
					if (MUUDebug.LOGGING) {
						String fire_status = "";
						boolean upper_is_fired = src_data_item.is_UBoundFired();
						boolean lower_is_fired = src_data_item.is_LBoundFired();
						
						if (upper_was_fired != upper_is_fired)
							fire_status += " Upper Fired " + upper_was_fired + " != " + upper_is_fired;
					
						if (need_fire.UpperToFire)
							fire_status += " Upper limit " +
									         src_data_item.getUBoundVal() + 
									         " passed";
						if (lower_was_fired != lower_is_fired)
							fire_status += " Lower Fired " + lower_was_fired + " != " + lower_is_fired;
						
						if (need_fire.LowerToFire)
							fire_status += " Lower limit " +
										   src_data_item.getLBoundVal() +
										   " passed";
						
					    MUUDebug.Log(class_nm, "Loaded: " + 
					    						loaded_data_item.getSymbol() +
					    						" price: " +
					    						loaded_data_item.getPrice() +
					    						fire_status);
					}
					synchronized (stick) {
						stick.notify(true);
					}
				} else {
					Bundle err_data = msg.getData();
					String err_msg = null;
					
					if (err_data != null)
						err_msg = err_data.getString("err_msg");
					
					if (err_msg == null)
						err_msg = "Error!";
					
					MUUDebug.Log(class_nm, "Error requesting data: " + err_msg);

					synchronized (stick) {
						stick.notify(false);
					}
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
				boolean at_list_one_OK = false;
				if (pass_is_active)
					return;
				
				pass_is_active = true;
				dbHelper.open();
				Cursor crsFull = dbHelper.fetchAllWQuotas();
				idx_in_list_being_proc = -1;
				if (crsFull.getCount() > 0) {
					dtlList = new StockDtlList(crsFull);

					int n_in_list = dtlList.size();
					
					MUUDebug.Log(class_nm, "Staring Loop");
					for (idx_in_list_being_proc = 0; 
						 idx_in_list_being_proc < n_in_list; 
						 idx_in_list_being_proc++) {
						
						StockDetailData this_item = dtlList.get(idx_in_list_being_proc);

						this_item.UpdateFiredState (sv_fired_stat.get(this_item.getSymbol()));
						
						MUUDebug.Log(class_nm, "Processing: " + this_item.getSymbol());
						GetStockDtl (this_item.getSymbol());
						synchronized (stick) {
							try {
								stick.wait();
							} catch (InterruptedException e) {
								// e.printStackTrace();
								MUUDebug.Log(class_nm, "Loop interrupted");
								break;
							}
						}
						if (stick.completion) {
						    Long rowID = dtlList.getDBrowID (idx_in_list_being_proc);
						    dbHelper.updateWQuota (rowID, this_item);
						    at_list_one_OK = true;
						} else {
							MUUDebug.Log(class_nm, "Failure " + this_item.getSymbol());
						}
						MUUDebug.Log(class_nm, "Keep Going");
					}
					MUUDebug.Log(class_nm, "Loop passed");
					idx_in_list_being_proc = -1;
					
				}
				crsFull.deactivate();
				dbHelper.close();				
				pass_is_active = false;
				if (!at_list_one_OK) {
					ChkPrice.this.stopSelf(); // Looks like it does not actually stop the Sevice
					MUUDebug.Log(class_nm, "All Symbols Failed");
				}
			}
		}, 0, UPDATE_INTERVAL);
		MUUDebug.Log(class_nm, "Check Price's Timer started.");
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
			MUUDebug.Log(class_nm, "Check Price's Timer stopped.");
		}
		super.onDestroy();
		MUUDebug.Log(class_nm, "Check Price's onDestroy passed.");
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
