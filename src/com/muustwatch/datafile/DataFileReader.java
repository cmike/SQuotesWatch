package com.muustwatch.datafile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.muustwatch.MUUDebug;
import com.muustwatch.StockDetailData;

import android.os.Environment;

public class DataFileReader {
	static final String DATA_FOLDER = "DbgData";
	File file_to_read = null;
	int  line_no = -1;
	String _m_ticker_symb = null;
	InputStreamReader _m_isReader = null;
	BufferedReader _m_reader = null;
	
	public DataFileReader () { };
	
	public boolean isEmpty () { return (_m_reader == null); }
	public void Release () {
		if (!isEmpty()) {
		        try {
		    		_m_isReader.close();
		        }
		        catch (IOException e) {
		            // handle exception
		        }
		        finally {
		        	_m_isReader = null;
		        	_m_reader = null;
		        }
		}
	}
	private void InitByFile (File read_from) {
		FileInputStream fIn = null;
		file_to_read = read_from;
		boolean isOK = true;
		try {
			fIn = new FileInputStream (file_to_read);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isOK = false;
		}
		
		if (isOK) {
			_m_isReader = new InputStreamReader(fIn);
		    _m_reader = new BufferedReader(_m_isReader);
		} else {
			_m_isReader = null;
			_m_reader = null;
		}
	}
	private DataFileReader (File read_from) {
		InitByFile (read_from);
	}
	public static DataFileReader ReaderInit(String symb_name) {
		DataFileReader out = null;
		String lowerc_name = symb_name.toLowerCase();
		File try_this = new File (Environment.getExternalStorageDirectory() + 
				 File.separator + 
				 DATA_FOLDER +
				 File.separator + 
				 lowerc_name +
				 "_daily.csv"
				 );
		if (try_this.canRead()) {
			out = new DataFileReader (try_this);
			out._m_ticker_symb = lowerc_name;
		}
		
		return (out);
	}
	public boolean LoadNextLine (StockDetailData item_to_load_to) {
		boolean loaded = false;
		
		try {
	        String line = _m_reader.readLine();
	        
	        if (MUUDebug.LOOP_READ && line == null && line_no >= 0) {
	        	if (MUUDebug.LOGGING)
	        		MUUDebug.Log (getClass().getSimpleName(), 
	        				"Reseting " +
	        						file_to_read.getName() +
	        						" after " +
	        						line_no + " lines read");
	        	InitByFile (file_to_read);
	        	line_no = -1;
	        	line = _m_reader.readLine();
	        }
	        
	        if (line != null) {
	             String[] RowData = line.split(",");
	             
	             if (RowData.length == 10) { 
					item_to_load_to.setSymbol (RowData[0]);
					item_to_load_to.setPrice (RowData[1]);
					item_to_load_to.setTrDate (RowData[2]);
					item_to_load_to.setTrTime (RowData[3]);
					item_to_load_to.setChange (RowData[4]);
					item_to_load_to.setOpen (RowData[5]);
					item_to_load_to.setHigh (RowData[6]);
					item_to_load_to.setLow (RowData[7]);
					item_to_load_to.setPrevClose(RowData[8]);
					item_to_load_to.setVolume (RowData[9]);
	             }
			 
				 loaded =	item_to_load_to.is_data_loaded();
	        }
	        else
	        	loaded = false;
	    }
	    catch (IOException ex) {
	        // handle exception
	    }

		if (loaded)
			line_no++;
		
		return (loaded);
	}
}
