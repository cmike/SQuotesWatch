package com.muustwatch;

import org.json.JSONObject;

import com.muustwatch.datafile.DataFileReader;
import com.muustwatch.datafile.DataFileStorage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class LoadDeatails implements Runnable {
	private final String YAPI_URL_Head = "http://query.yahooapis.com/v1/public/yql?q=";
	private final String YAPI_URL_Tail = "&format=json&callback=muu";
    private final String RequestHead = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=";
    private final String RequestTail = "&f=sl1d1t1c1ohgpv&e=.csv' and columns='symbol,price,date,time,change,open,high,low,prev_close,volume'";
	private String _m_qsymb;
	private StockDetailData data_item = null;
	private Handler _m_handler = null;
	
	public LoadDeatails (String qsymb, Handler proc_result) {
		_m_qsymb = qsymb;
		_m_handler = proc_result;
	}

	
	private String prepare (String in_str) {
		String result = in_str;
		final CharSequence TO_BE_REPLACED = " =:/?,&";
		final CharSequence[] TO_REPLACE_BY = {"%20", "%3D", "%3A", "%2F", "%3F", "%2C", "%26"};
		int total = TO_BE_REPLACED.length();
		for (int i = 0; i < total; i++)
		{
			CharSequence what = TO_BE_REPLACED.subSequence(i, i+1);
			CharSequence repl_by = TO_REPLACE_BY[i];
			result = result.replace(what, repl_by);
		}
		return (result);
	}

	private void NotifyCaller (String err_msg) {
		int n_item = (err_msg == null && data_item != null && data_item.is_data_loaded()) ? 1 : 0;
	  	Message result_msg = _m_handler.obtainMessage (n_item, data_item);
	  	
	  	if (err_msg != null) {
	  		Bundle res = new Bundle();
	  		res.putString("err_msg", err_msg);
	  		
	  		result_msg.setData(res);
	  	}
	  	
	  	_m_handler.sendMessage(result_msg);
	}
	
	private String data_file_load () {
		String err_msg = null;
		
		DataFileReader reader = DataFileStorage.getDataFile(_m_qsymb);
		
		if (reader == null)
			err_msg = "No File Found";
		else {
			data_item = new StockDetailData();
			if (!reader.LoadNextLine(data_item))
				err_msg = "EOF";
		}
		
		return (err_msg);
	}
	
	private String http_load () {
		String err_msg = null;
		String YAPI_Body = RequestHead + _m_qsymb + RequestTail;
		YAPI_Body = prepare (YAPI_Body);
		String YAPI_URL = YAPI_URL_Head + YAPI_Body + YAPI_URL_Tail;
		System.out.print("Loader Activated");
		
		HttpDloader my_downloader = new HttpDloader();
		String res = my_downloader.Download(YAPI_URL);
		// start from first ( and end one char left
		if (!res.startsWith("Error")){
			res = res.substring(res.indexOf('(')+1, res.length()-1);
			// use JSON to parse the result
			try{
				JSONObject mainObject = new JSONObject(res);
				JSONObject queryHead = mainObject.getJSONObject("query");
				JSONObject resultHead = queryHead.getJSONObject("results");
				JSONObject item = resultHead.getJSONObject("row");

				data_item = new StockDetailData();
					/*
				    "symbol": "GOOG",
				    "price": "614.67",
				    "date": "11/16/2011",
				    "time": "12:10pm",
				    "change": "-1.89",
				    "open": "612.08",
				    "high": "618.30",
				    "low": "612.02",
				    "prev_close": "xxx.xx",
				    "volume": "1004115"
				 */						
					data_item.setSymbol (item.getString("symbol"));
					data_item.setPrice (item.getString("price"));
					data_item.setTrDate (item.getString("date"));
					data_item.setTrTime (item.getString("time"));
					data_item.setChange (item.getString("change"));
					data_item.setOpen (item.getString("open"));
					data_item.setHigh (item.getString("high"));
					data_item.setLow (item.getString("low"));
					data_item.setPrevClose(item.getString("prev_close"));
					data_item.setVolume (item.getString("volume"));
			 
					if (!data_item.is_data_loaded())
					  err_msg = "Symbol Not Found";
			}catch(Exception e){
				String msg = e.getMessage();
				
				if (msg.length() == 0) {
					msg = "Data parsing error";
				}
				err_msg = msg;
			}
		}
		else
		{
			err_msg = res;
		}
		
		return (err_msg);
	}
	
	@Override
	public void run() {
		String err_msg = null;

		if (MUUDebug.REAL_LOAD)
		    err_msg = http_load ();
		else
			err_msg = data_file_load ();
		
		NotifyCaller(err_msg);
	}	

}
