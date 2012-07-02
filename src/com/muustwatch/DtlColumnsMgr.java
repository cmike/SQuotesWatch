package com.muustwatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

public class DtlColumnsMgr {
	
	static Hashtable<String, ColumnDescriptor> _m_Descriptors = null;
	static class ColumnDescriptor {
		String clmn_title;
		float  txt_size;
	}
	
	private static void addOneDescrItem (String method_name, 
										 String title, float txt_size) {
		ColumnDescriptor dscr = new ColumnDescriptor();

		dscr.clmn_title = title;
		dscr.txt_size   = txt_size;
		_m_Descriptors.put(method_name, dscr);
		
	}
	
	// StockDetailData methods to obtain property's value to show 
	public static final String[] AllColumnIDs = {
		"getSymbol", "getTrDateTime", "getPrice", "getPrevClose", "getChange", "getWatching"
	};
	private static void _InitDescrTable () {
		_m_Descriptors = new Hashtable<String, ColumnDescriptor>();
		
		addOneDescrItem(AllColumnIDs[0], "STOCK\nname", 14);
		addOneDescrItem(AllColumnIDs[1], "Date\nand Time", 11);
		addOneDescrItem(AllColumnIDs[2], "Stock\nPrice", 14);
		addOneDescrItem(AllColumnIDs[3], "Peviously\nClosed", 14);
		addOneDescrItem(AllColumnIDs[4], "Day's\ngain", 14);
		addOneDescrItem(AllColumnIDs[5], "Price\nto check", 14);
	};
	
	static public String getTitle (String columnID) {
		String ret = null;
		
		if (_m_Descriptors == null)
			_InitDescrTable ();
		
		ColumnDescriptor dscr = _m_Descriptors.get(columnID);
		
		if (dscr != null)
		  ret = dscr.clmn_title;
		
		return (ret);
	}
	static public float get_tx_size (String columnID) {
		float ret = -1;
		
		if (_m_Descriptors == null)
			_InitDescrTable ();
		
		ColumnDescriptor dscr = _m_Descriptors.get(columnID);
		
		if (dscr != null)
		  ret = dscr.txt_size;
		
		return (ret);
	}
	static public String getText (String columnID, StockDetailData obj) {
		String ret = null;
		
		if (_m_Descriptors == null)
			_InitDescrTable ();
		
		ColumnDescriptor dscr = _m_Descriptors.get(columnID);
		
		if (dscr != null) {
			Method getMethod = null;
			boolean methodOK = true;

			try {
				getMethod = StockDetailData.class.getMethod(columnID, (Class[])null);
			} catch (SecurityException e) {
				methodOK = false;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				methodOK = false;
				e.printStackTrace();
			}
			
			if (methodOK) {
				try {
					ret = (String)getMethod.invoke(obj, (Object[])null);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					methodOK = false;
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					methodOK = false;
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					methodOK = false;
					e.printStackTrace();
				}
			} else
				ret = " ";
		}
		else
			ret = " ";
		
		return (ret);
	}
}
