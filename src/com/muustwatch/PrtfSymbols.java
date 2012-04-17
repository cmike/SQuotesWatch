package com.muustwatch;

import com.muustwatch.datafile.DataFileStorage;
import com.muustwatch.db.WatchQuotaDBAdapter;
import java.util.Hashtable;
import java.util.Iterator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class PrtfSymbols extends Activity implements OnClickListener {
	
    protected static final int DTL_REQ_FROM_TBL = 1;
    
    protected static final int ID_ADD = 1000;
	protected static final int ID_DELETE = 1001;
	Hashtable<Integer, Integer> idIdxHash = new Hashtable<Integer, Integer>(); // Res. IDs of Table content to Symbol idx 
	Boolean toggle = false;
	WatchQuotaDBAdapter dbHelper;
	StockDtlList dtlList;
	boolean  in_pause = false;
	ScrollView sv = null;
	TableLayout t = null;
	ChkPrice s = null;
	
	private class IdCount {
		private int	value = 0;
		private boolean is_set = false;
		
		@SuppressWarnings("unused")
		public IdCount () {}
		public IdCount (int in_val) { is_set = true; value = in_val; }
		@SuppressWarnings("unused")
		public void set (int in_val) { is_set = true; value = in_val; }
		public int get () {
			if (!is_set)
				throw new Error ("Non-initialized Integer");
			else
				return (value);
		}
		public int increment () {
			if (!is_set)
				throw new Error ("Non-initialized Integer");
			else
				return (++value);
		}
	}
	public void TableHeadersSetup (TableLayout t) {
		t.setColumnStretchable(0, true);
		t.setColumnStretchable(1, true);
        t.setColumnStretchable(2, true);
        t.setColumnStretchable(3, true);
        // Let us set the headings.
        TableRow tr = new TableRow(this);
        for(int loop=0;loop<5;loop++)
        {
	        String name = new String();
        	switch(loop)
	        {
	        case 0: name="STOCK\nname";break;
	        case 1: name="Date\nand Time";break;
	        case 2: name="Stock\nPrice";break;
	        case 3: name="Peviously\nClosed";break;
	        case 4: name="Day's\ngain";break;
	       
	        }
        	
        	
		  	TextView tv = new TextView(this);
		  	tv.setText(name);
		  	tv.setTextSize(14);
		  	tr.addView(tv);
		  	
        }
        tr.setBackgroundColor(Color.rgb(102, 0, 0));
        t.addView(tr);
		
	}
	
	private void TableOneRowAdd(TableLayout t, IdCount idgen, boolean toggle,
			int item_idx, Hashtable<Integer, Integer> idIdxHash, StockDetailData data_item) {
		TableRow tr1 = new TableRow(this);
		
		TVwithCtxInfo tv1 = new TVwithCtxInfo(this);

		tv1.setText(data_item.getSymbol());
		// Let us assign an ID to this text box and store relation in the hash
		// table
		tv1.setId(idgen.get());
		idIdxHash.put(new Integer(idgen.get()), new Integer (item_idx));
		tv1.setOnClickListener(this);
		registerForContextMenu(tv1);

		TVwithCtxInfo tv2 = new TVwithCtxInfo(this);
		tv2.setText(data_item.getTrDate() + "  " + data_item.getTrTime());
		tv2.setId(idgen.increment());
		idIdxHash.put(new Integer(idgen.get()), new Integer (item_idx));
		tv2.setOnClickListener(this);
		registerForContextMenu(tv2);

		TVwithCtxInfo tv3 = new TVwithCtxInfo(this);
		tv3.setText(data_item.getPrice());
		tv3.setId(idgen.increment());
		idIdxHash.put(new Integer(idgen.get()), new Integer (item_idx));
		tv3.setOnClickListener(this);
		registerForContextMenu(tv3);

		TVwithCtxInfo tv4 = new TVwithCtxInfo(this);
		tv4.setText(data_item.getPrevClose());
		tv4.setId(idgen.increment());
		idIdxHash.put(new Integer(idgen.get()), new Integer (item_idx));
		tv4.setOnClickListener(this);
		registerForContextMenu(tv4);

		TVwithCtxInfo tv5 = new TVwithCtxInfo(this);
		tv5.setText (data_item.getChange());
		tv5.setId(idgen.increment());
		idIdxHash.put(new Integer(idgen.get()), new Integer (item_idx));
		tv5.setOnClickListener(this);
		registerForContextMenu(tv5);

		idgen.increment();
		
		tr1.addView(tv1);
		tr1.addView(tv2);
		tr1.addView(tv3);
		tr1.addView(tv4);
		tr1.addView(tv5);
		t.addView(tr1);
		if (toggle == false) {
			tr1.setBackgroundColor(Color.argb(90, 102, 0, 0));
			toggle = true;
		} else {
			tr1.setBackgroundColor(Color.argb(140, 102, 0, 0));
			toggle = false;
		}

		tv1.setTextSize(14);
		tv2.setTextSize(11);
		tv3.setTextSize(14);
		tv4.setTextSize(14);
		tv5.setTextSize(14);

		
	}
	private void obtain_symb () {
		Intent intent = new Intent(PrtfSymbols.this, YahooSymbolSearch.class);

		intent.putExtra("FromTable", true);
		startActivity(intent);
	}
	private void editPrefs () {
		Intent intent = new Intent(PrtfSymbols.this, Prefs.class);

		intent.putExtra("FromTable", true);
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainapp_mnu, menu);
		return true;
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			s = ((ChkPrice.MyBinder) binder).getService();
			Toast.makeText(PrtfSymbols.this, "Connected",
					Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			s = null;
		}
	};
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addNewID:
			obtain_symb ();
			break;
		case R.id.exitID:
			finish();
			break;
		case R.id.startServID:
			doServiceBind ();
			break;
		case R.id.PrefsID:
			editPrefs ();
			break;
		}
		return (true);
	}
	
	private void doServiceBind() {
		bindService(new Intent(this, ChkPrice.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbols);
		sv = (ScrollView)findViewById(R.id.baseScroll);
		t = (TableLayout)findViewById(R.id.baseTable);
		t.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				obtain_symb();
			}
			
		});

		if (!FillTable ())
			obtain_symb ();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Integer id = new Integer(v.getId());
		// Let us recover the symbol from this
		Integer item_idx = (Integer)idIdxHash.get(id);
		if (item_idx == null)
			throw new Error ("ItemIdx not found");
		else {
		StockDetailData data_item = dtlList.get (item_idx);
//		String symbol = data_item.getSymbol();
//		Context context = getApplicationContext();
//		int duration = Toast.LENGTH_SHORT;
//		Toast.makeText(context, symbol, duration).show();
		
		Bundle b = new Bundle();
		
		b.putParcelable("results", data_item);
		Intent intent = new Intent(PrtfSymbols.this, Details.class);
		intent.putExtra("InData", b);
		intent.putExtra("ItemIdxInTable", item_idx);
//		startActivityForResult(intent, DTL_REQ_FROM_TBL);
		startActivity(intent);
		}
	}

	public void onCreateContextMenu(
			ContextMenu menu, 
			View v, 
			ContextMenu.ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Your Portfolio");
		
		menu.add(Menu.NONE, ID_ADD, Menu.NONE, "Add");
		menu.add(Menu.NONE, ID_DELETE, Menu.NONE, "Delete");
		
	}

	public boolean FillTable () {
		IdCount idcount = new IdCount(1);
		int     item_idx = 0;
		boolean data_loaded = false;

		sv.setBackgroundColor(Color.rgb(0,0, 0));

		t.setBackgroundColor(Color.rgb(0,0, 0));
		t.removeAllViews();
		TableHeadersSetup (t);
		
		idIdxHash.clear();
				
		dbHelper = new WatchQuotaDBAdapter(this);
		dbHelper.open();
		Cursor crsFull = dbHelper.fetchAllWQuotas();
		if (crsFull.getCount() > 0) {
			dtlList = new StockDtlList(crsFull);

			Iterator<StockDetailData>  dtlIterator = dtlList.iterator();

			while (dtlIterator.hasNext()) {
				StockDetailData this_item = dtlIterator.next();

				TableOneRowAdd (t, idcount, toggle, item_idx, idIdxHash, this_item);
				toggle = !toggle;
				item_idx++;
			}
			
			data_loaded = true;
			String string = new String("Please Click on stocks to get \nadditional Information...");
			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, string, duration);
			toast.show();
		}
		crsFull.close();
		dbHelper.close();
		
		return (data_loaded);
	}
	
	@Override
	public void onPause () {
		super.onPause();
		in_pause = true;
	}
	
	@Override
	public void onResume () {
		super.onResume();
		
		if (in_pause) {
		  FillTable ();
		  in_pause = false;
		}
	}
	@Override
	public void onDestroy () {
		if (s != null) {
			unbindService (mConnection);
			s.stopSelf();
		}
		
		if (!MUUDebug.REAL_LOAD)
			DataFileStorage.Release();

		super.onDestroy();
	}
	
	private void DeleteIfConfirmed(String message, final Integer row_idx){
		class MyListener implements DialogInterface.OnClickListener {
			MyListener() {
			}
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					dialog.dismiss();
				{

					Long DBrowID = dtlList.getDBrowID (row_idx);
					dbHelper.open();
					dbHelper.deleteWQuota(DBrowID);
					dbHelper.close();
					if (!FillTable ())
						obtain_symb ();
				}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}
			}

		}
		MyListener listener = new MyListener();

		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		
		alert.setTitle("Confirmation");
		alert.setMessage(message + " will be deleted\nAre you sure?");
		alert.setPositiveButton("Ok", listener);
		alert.setNegativeButton("No", listener);
		alert.show();
		
	}
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case ID_ADD:
			obtain_symb ();
			break;
		case ID_DELETE:
			TVwithCtxInfo.TVCtxMenuInfo info = (TVwithCtxInfo.TVCtxMenuInfo) item.getMenuInfo();

			TextView view = info.targetView;
		    
		    int view_id = view.getId();
		    Integer row_idx = idIdxHash.get(new Integer(view_id));
		    
		    StockDetailData data_item = dtlList.get (row_idx);
			String symbol = data_item.getSymbol();
			DeleteIfConfirmed (symbol, row_idx);
			break;
		}
		return (true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == DTL_REQ_FROM_TBL) {
				
			}
		}
	}
}
