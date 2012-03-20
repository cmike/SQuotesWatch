package com.muustwatch;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SymbolList extends ListActivity{
	
	private Intent ret_data = null;
	YahooSymbolList results;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			results = extras.getParcelable("results");
		}
		
		StockListAdapter adapter = new StockListAdapter(this, results);
		//adapter.setData(results);
		setListAdapter(adapter);
	}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		YahooSymbol item = (YahooSymbol) getListAdapter().getItem(position);
		String p1 = item.getSymbol();
		
//		LoadDeatails loader = new LoadDeatails(SymbolList.this, p1);
//		Thread t = new Thread(loader);
//		t.start();
		
		ret_data = new Intent ();
		ret_data.putExtra("return_symbol", p1);
		setResult(RESULT_OK, ret_data);
		finish();
	}
	
	@Override
	public void finish() {
		if (ret_data == null) {
		  ret_data = new Intent();
		  setResult(RESULT_CANCELED, ret_data);
		}
		super.finish();
	}
	
}
