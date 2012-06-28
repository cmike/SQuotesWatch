package com.muustwatch;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.muustwatch.LimitCrossingNotifier.LimitType;
import com.muustwatch.StockDetailData.StockDtlFired;
import com.muustwatch.db.WatchQuotaDBAdapter;

public class ChkPrice extends Service {
	private Timer timer = new Timer();
	private long UPDATE_INTERVAL = 5000;
	private final String class_nm = getClass().getSimpleName();
	private WatchQuotaDBAdapter dbHelper = null;
	private StockDtlList dtlList;
	private boolean pass_is_active = false;
	Hashtable<String, StockDtlFired> sv_fired_stat = new Hashtable<String, StockDtlFired>();
	int idx_in_list_being_proc = -1;
	
	private static boolean _m_isRunning = false;

	// Keeps track of all current registered clients.
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    
    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_DB_REFRESHED = 3;
    static final int MSG_UNBIND_ALL_REQUEST = 4;
    static final int MSG_FORCE_UNBIND = 5;
    static final int MSG_UNUSED = 6;
    
    static final String ReqStringName = "OptCommand";
    
    // Target we publish for clients to send messages to IncomingHandler.
    final Messenger mMessenger = new Messenger(new IncomingHandler()); 


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	int msgID = MSG_UNUSED;
    	
    	if (intent != null)
    	    msgID = intent.getIntExtra(ReqStringName, MSG_UNUSED);
    	
    	if (msgID == MSG_UNBIND_ALL_REQUEST) {
    		doForceUnbindAll ();
    		MUUDebug.Log(class_nm, "Stop Requested");
    	}
    	return START_STICKY;
    }
    
  	// Handler of incoming messages from clients.
    class IncomingHandler extends Handler { 
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_REGISTER_CLIENT:
                mClients.add(msg.replyTo);
                break;
            case MSG_UNREGISTER_CLIENT:
                mClients.remove(msg.replyTo);
                break;
            default:
                super.handleMessage(msg);
            }
        }
    }
	private class LoadStick extends Object {
		boolean completion = false;
		
		void notify (boolean is_OK) {
			completion = is_OK;
			super.notify();
		}
	}

	private LoadStick	stick = new LoadStick();
	LimitCrossingNotifier notifier = null;
	

	public static boolean isRunning () {
		// Absolutely correct implementation
		// Make sense in general case.
//		Context ctx;
//		boolean ret = false;
//		final String full_cl_name = ChkPrice.class.getName();
//	    ActivityManager manager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
//	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//	        if (full_cl_name.equals(service.service.getClassName())) {
//	            ret = true;
//	            break;
//	        }
//	    }
//		return (ret);
		return (_m_isRunning);
	}
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
				
				_m_isRunning = true;
			}
		}
		
	}
	private void sendMessageToClients (int msgID) {
        for (int i=mClients.size()-1; i>=0; i--) {
            try {
                // Send data as an Integer
                mClients.get(i).send(Message.obtain(null, msgID));


            } catch (RemoteException e) {
                // The client is dead. Remove it from the list; 
                mClients.remove(i);
            }
        }
    }
	private void doForceUnbindAll () {
		sendMessageToClients (MSG_FORCE_UNBIND);
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
							// There might be a need to refresh (re-get) 'this_item'
							// since call to GetStockDtl() (after successful stick.wait())
							// updates value of corresponding dtlList item
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
					
					sendMessageToClients (MSG_DB_REFRESHED);
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
		_m_isRunning = false;
		if (timer != null) {
			timer.cancel();
			MUUDebug.Log(class_nm, "Check Price's Timer stopped.");
		}
		super.onDestroy();
		MUUDebug.Log(class_nm, "Check Price's onDestroy passed.");
	}
}
