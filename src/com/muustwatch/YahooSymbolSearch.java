package com.muustwatch;

import org.json.JSONArray;
import org.json.JSONObject;

import com.muustwatch.db.WatchQuotaDBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class YahooSymbolSearch extends Activity implements Runnable{
	protected static final int NO_PROGRESS_DLG = -1;
	protected static final int SYMBOL_SEARCH_LIST_LOAD_PROGRESS = 1;
	protected static final int SYMBOL_DATA_LOAD_PROGRESS = 2;
    protected static final int LST_REQ_CODE = 0;
    protected static final int DTL_REQ_CODE = 1;

    WatchQuotaDBAdapter dbHelper;
    EditText searchString;
	Button searchButton;
	Button getButton;
	int     CurProgressDlgID = NO_PROGRESS_DLG;
	boolean mCalledFromTable = false;
	String yahooURL = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=";
	String yahooURL1 = "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
	YahooSymbolList symbols;
	
	private void showProgressDlg (int id) {
		CurProgressDlgID = id;
		showDialog (id);
	}
	private void dismissProgressDlg () {
		if (CurProgressDlgID != NO_PROGRESS_DLG) {
			try {
			dismissDialog(CurProgressDlgID);
			} catch (Exception e) {
				//ignore error
			}
			CurProgressDlgID = NO_PROGRESS_DLG;
		}
	}

	@Override
	// To Dismiss Progress for Change Orientation (re-starts Appl), 
	// or before launching nested Activity (despite that it gets
	// done at the very beginning of handleMessage() the Dialog
	// might still hang - looks like Android Bug workaround)
	public void onSaveInstanceState (Bundle out_bndl) {
		dismissProgressDlg ();
//		super.onDestroy(); // Most likely it was a typo 		
		super.onSaveInstanceState(out_bndl);		
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			mCalledFromTable = extras.getBoolean("FromTable");
		}
        setContentView(R.layout.main);
        
        searchString = (EditText)findViewById(R.id.searchString);
        searchButton = (Button) findViewById(R.id.button);
        
        searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Start the new thread to download the data from yahoo server
				// and display the same in the list 
				showProgressDlg(SYMBOL_SEARCH_LIST_LOAD_PROGRESS);
				Thread t = new Thread(YahooSymbolSearch.this);
				t.start();		
			}
		});
        
        getButton = (Button) findViewById(R.id.GetBtn);
        getButton.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				String p1 = searchString.getText().toString();
				
				showProgressDlg (SYMBOL_DATA_LOAD_PROGRESS);
				LoadDeatails loader = new LoadDeatails(p1, handler);
				Thread t = new Thread(loader);
				t.start();
							
			}
        	
        });
    }

    private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			dismissProgressDlg();
			
			if (msg.what == -1) {
			Bundle b = new Bundle();
			b.putParcelable("results", symbols);
			Intent intent = new Intent(YahooSymbolSearch.this, SymbolList.class);
			intent.putExtras(b);
			startActivityForResult(intent, LST_REQ_CODE);
			} else {
				if (msg.what == 1) { // TODO to be prepared array of results
					Bundle b = new Bundle();
					
					StockDetailData data_item = (StockDetailData) msg.obj;
					b.putParcelable("results", data_item);
					Intent intent = new Intent(YahooSymbolSearch.this, Details.class);
					intent.putExtra("InData", b);
					startActivityForResult(intent, DTL_REQ_CODE);
				} else {
					Bundle err_data = msg.getData();
					String err_msg = null;
					
					if (err_data != null)
						err_msg = err_data.getString("err_msg");
					
					if (err_msg == null)
						err_msg = "Error!";
					
					createAlertDialog(err_msg);
				}
			}
		}
	};
	    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == LST_REQ_CODE) {
				if (data.hasExtra("return_symbol")) {
					String qsymb = data.getExtras().getString("return_symbol");

					showProgressDlg (SYMBOL_DATA_LOAD_PROGRESS);
					LoadDeatails loader = new LoadDeatails(qsymb, handler);
					Thread t = new Thread(loader);
					t.start();
				}
			} else if (requestCode == DTL_REQ_CODE) {
				if (mCalledFromTable && data.hasExtra("AddPushed")) {
					boolean fromAdd = data.getExtras().getBoolean("AddPushed");
					
					if (fromAdd)
						finish();
				}
			}
		}
	}
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		  case SYMBOL_SEARCH_LIST_LOAD_PROGRESS: {
			ProgressDialog dialog1 = new ProgressDialog(this);
			dialog1.setMessage("Please wait...Dowloading data...");
			dialog1.setIndeterminate(true);
			dialog1.setCancelable(true);
			return dialog1;
		  }
		  case SYMBOL_DATA_LOAD_PROGRESS: {
			ProgressDialog dialog2 = new ProgressDialog(this);
			dialog2.setMessage("Loading. Please wait...");
			dialog2.setIndeterminate(true);
			dialog2.setCancelable(true);
			return dialog2;
		  }
		}
		return super.onCreateDialog(id);
	}
    
    @Override
	public void run() {
		// required...as we cannot create a UI loop inside a thread for alert dialog
		// followed by Looper.loop()
		Looper.prepare();
		String str = searchString.getText().toString();
		str = str.replace(" ", "%20");
		String url = yahooURL+str+yahooURL1;
		HttpDloader my_downloader = new HttpDloader();
		String res = my_downloader.Download(url);
		// start from first ( and end one char left
		if (!res.startsWith("Error")){
			res = res.substring(res.indexOf('(')+1, res.length()-1);
			// use JSON to parse the result
			try{
				JSONObject mainObject = new JSONObject(res);
				JSONObject resultSet = mainObject.getJSONObject("ResultSet");
				JSONArray result = resultSet.getJSONArray("Result");
				if (result.length() > 0){
					symbols = new YahooSymbolList();
					for (int i = 0; i < result.length(); ++i)
					{
						JSONObject item = result.getJSONObject(i);
						YahooSymbol obj = new YahooSymbol();
						obj.setSymbol(item.getString("symbol"));
						obj.setName(item.getString("name"));
						obj.setExch(item.getString("exch"));
						obj.setType(item.getString("type"));
						symbols.add(obj);
					}
					handler.sendEmptyMessage(-1);
				}
				else
				{
				   // alert dialog with no result
				   createAlertDialog("No Results found. Try Again...");
				}
			}catch(Exception e){
				// alert dialog with exception
				createAlertDialog(e.getMessage());
			}
		}
		else
		{
			createAlertDialog(res);
		}
		// required...as we cannot create a UI loop inside a thread for alert dialog
		Looper.loop();
	}
	
	private void createAlertDialog(String message){
		AlertDialog.Builder alertbox = new AlertDialog.Builder(YahooSymbolSearch.this);
        
        // set the message to display
        alertbox.setMessage(message);

        // add a neutral button to the alert box and assign a click listener
        alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

            // click listener on the alert box
            public void onClick(DialogInterface arg0, int arg1) {
    			dismissProgressDlg();
            }
        });

        // show it
        alertbox.show();
	}
}