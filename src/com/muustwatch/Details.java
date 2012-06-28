package com.muustwatch;

//import com.muustwatch.R;
//import com.muustwatch.StockDetailData;
import com.muustwatch.db.DataAndIdx;
import com.muustwatch.db.WatchQuotaDBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Details extends Activity {
	DataAndIdx	ItemInDB = null;
	StockDetailData  stock_detail = null;
	Integer in_item_idx = -1;
	WatchQuotaDBAdapter dbHelper = null;
	boolean	allow_add_to_button = true;
	private Button CloseButton;
	private Button ToChartButton;
	private Button ToPortfolioButton;
	Intent ret_data = null;
	private EditText UboundVal;
	private CheckBox chb_txt1;
	private EditText LboundVal;
	private CheckBox chb_txt2;
	private EditText WatchingVal;



	private void UpdateBounds () {
		if (chb_txt2.isChecked())
			stock_detail.setLBound_fromString(LboundVal.getText().toString());
		else
			stock_detail.setLBoundUndef();
		
		if (chb_txt1.isChecked())
			stock_detail.setUBound_fromString(UboundVal.getText().toString());
		else
			stock_detail.setUBoundUndef();
	}
	@Override
	public void finish() {
		if (ret_data == null) {
		  ret_data = new Intent();
		  setResult(RESULT_CANCELED, ret_data);
		}
		super.finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		Bundle extras = getIntent().getExtras();
		Intent  in = getIntent();
		Bundle extras = in.getBundleExtra("InData");
		if (extras != null)
			stock_detail = extras.getParcelable("results");

		in_item_idx = in.getIntExtra("ItemIdxInTable", -1);

		if (stock_detail == null || !stock_detail.is_data_loaded())
			throw new Error ("Empty Details Data");
		
		setContentView(R.layout.details);

		dbHelper = new WatchQuotaDBAdapter(this);
		dbHelper.open();
		ItemInDB = dbHelper.getWQuotaBySymbol(stock_detail.getSymbol());
		if (ItemInDB != null)
			allow_add_to_button = false;
		
		
		CloseButton = (Button) findViewById(R.id.CloseBtn);
		CloseButton.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
		
		
		ToChartButton = (Button) findViewById(R.id.GoToChart);
		ToChartButton.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("Symbol", stock_detail.getSymbol ());
				Intent intent = new Intent(Details.this, ChartView.class);
				intent.putExtras(b);
				startActivity(intent);
			}

		});
		
		UboundVal = new EditText(this);
		chb_txt1 = new CheckBox (this);
		LboundVal = new EditText(this);
		chb_txt2 = new CheckBox (this);
		WatchingVal = new EditText(this);
		
		if (allow_add_to_button) {
			ToPortfolioButton = new Button(this);
			ToPortfolioButton.setText("Add to Portfolio");
			LinearLayout btn_lay = (LinearLayout) findViewById(R.id.ButtonLay);
			btn_lay.addView(ToPortfolioButton);
			ToPortfolioButton.setOnClickListener(new OnClickListener () {

				@Override
				public void onClick(View v) {
					UpdateBounds ();
					dbHelper.createWQuota(stock_detail);
					
					ret_data = new Intent ();
					ret_data.putExtra("AddPushed", true);
					setResult(RESULT_OK, ret_data);
					finish();
				}

			});
		} else if (ItemInDB != null) {
			ToPortfolioButton = new Button(this);
			ToPortfolioButton.setText("Update Portfolio");
			LinearLayout btn_lay = (LinearLayout) findViewById(R.id.ButtonLay);
			btn_lay.addView(ToPortfolioButton);
			ToPortfolioButton.setOnClickListener(new OnClickListener () {

				@Override
				public void onClick(View v) {
					StockDetailData orig = ItemInDB.getData();
					UpdateBounds ();
					orig.Update(stock_detail);
					dbHelper.updateWQuota(ItemInDB.getRowIdx(), orig);
					finish();
				}

			});			
		} else
			throw new Error ("Update Request w/o Data");

		final String undefString = getApplicationContext().getResources().getString (R.string.BOUND_UNDEF);
		
		ScrollView sv = (ScrollView)findViewById(R.id.baseScroll);
		sv.setBackgroundColor(Color.rgb(0,0, 0));
		
		// Let us set up the UI with these variables
		TableLayout table = (TableLayout)findViewById(R.id.table1);
		table.setColumnStretchable(0, true);
		table.setColumnStretchable(1, true);
		table.setBackgroundColor(Color.rgb(0,0, 0));

		TableRow tr = new TableRow(this);
		TextView tv1 = new TextView(this);
		tv1.setText("Stock Details");
		tv1.setTextSize(16);
		tr.addView(tv1);
		tr.setBackgroundColor(Color.argb(200,102, 0, 0));

		table.addView(tr);

		String key="",value="";


		for(int loop=1;loop<=10;loop++)
		{
			switch(loop)
			{

			case 1: key = "Symbol"; value = stock_detail.getSymbol (); break;
			case 2: key = "Last Price"; value = stock_detail.getPrice ();break;
			case 3: key = "Date"; value = stock_detail.getTrDate ();break;
			case 4: key = "Time"; value=stock_detail.getTrTime ();break;
			case 5: key = "Change"; value=	stock_detail.getChange ();break;
			case 6: key = "Open Price"; value=	stock_detail.getOpen ();break;
			case 7: key = "Highest Price"; value=	stock_detail.getHigh ();break;
			case 8: key = "Lowest Price"; value=stock_detail.getLow ();break;
			case 9: key = "Previous Close"; value=stock_detail.getPrevClose();break;
			case 10: key = "Volume"; value=stock_detail.getVolume ();break;
			}

			tr = new TableRow(this);
			tv1 = new TextView(this);
			tr.setBackgroundColor(Color.argb((60+(loop*10)),102, 0, 0));
			TextView tv2 = new TextView(this);
			tv1.setText(key);
			tv2.setText(value);
			tr.addView(tv1);
			tr.addView(tv2);
			table.addView(tr);

			tv1.setTextSize(13);
			tv2.setTextSize(14);
		}

		// Watching
		tr = new TableRow(this);
		
		tv1 = new TextView(this);
		tv1.setText("Watching Price");
		tv1.setTextSize(13);
		
		WatchingVal.setText(stock_detail.getWatching());
		WatchingVal.setTextSize(14);
		WatchingVal.setSingleLine(true);
		
		tr.addView(tv1);
		tr.addView(WatchingVal);
		
		table.addView(tr);
		// End Watching
		tr = new TableRow(this);

		
		String toShow = stock_detail.getUBoundVal();
		if (toShow.length() == 0)
			toShow = undefString;

		UboundVal.setText (toShow);
		UboundVal.setEnabled(stock_detail.UBoundIsSet());
		UboundVal.setTextSize(14);
		UboundVal.setSingleLine(true);
		UboundVal.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
//				boolean isChecked = chb_txt1.isChecked();
//
//				UboundVal.setEnabled(isChecked);
				if (/*isChecked && */undefString.equals(UboundVal.getText().toString())) {
					UboundVal.setText ("");
				}
			}
		});

		chb_txt1.setText(R.string.UPPER_BOUND_LBL);
		chb_txt1.setChecked(stock_detail.UBoundIsSet());
		chb_txt1.setTextSize(13);
		chb_txt1.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				boolean isChecked = chb_txt1.isChecked();

				UboundVal.setEnabled(isChecked);
				
				if (isChecked)
					UboundVal.requestFocus ();
			}
		});
		
		tr.addView(chb_txt1);
		tr.addView(UboundVal);
		
		table.addView(tr);
		
		tr = new TableRow(this);

		
		toShow = stock_detail.getLBoundVal();
		if (toShow.length() == 0)
			toShow = undefString;

		LboundVal.setText (toShow);
		LboundVal.setEnabled(stock_detail.LBoundIsSet());
		LboundVal.setTextSize(14);
		LboundVal.setSingleLine(true);
		LboundVal.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
//				boolean isChecked = chb_txt2.isChecked();
//
//				LboundVal.setEnabled(isChecked);
				if (/*isChecked && */undefString.equals(LboundVal.getText().toString())) {
					LboundVal.setText ("");
				}
			}
		});

		chb_txt2.setText(R.string.LOWER_BOUND_LBL);
		chb_txt2.setChecked(stock_detail.LBoundIsSet());
		chb_txt2.setTextSize(13);
		chb_txt2.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				boolean isChecked = chb_txt2.isChecked();

				LboundVal.setEnabled(isChecked);
				
				if (isChecked)
					LboundVal.requestFocus ();
			}
		});
		
		tr.addView(chb_txt2);
		tr.addView(LboundVal);
		
		table.addView(tr);
		
//		CloseButton.requestFocus();
	}

	@Override
	public void onPause () {
		super.onPause ();
		dbHelper.close();
	}

}
