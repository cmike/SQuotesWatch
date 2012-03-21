package com.muustwatch.datafile;

import java.util.Hashtable;

public class DataFileStorage {
	static Hashtable<String, DataFileReader> _m_Storage = null;
	
	public static DataFileReader getDataFile (String symbol_nm) {
		DataFileReader ret         = null;
		DataFileReader storage_obj = null;
		String Lc_name = symbol_nm.toLowerCase();
		
		if (_m_Storage == null)
			_m_Storage = new Hashtable<String, DataFileReader>();
		
		storage_obj = _m_Storage.get(Lc_name);
		if (storage_obj == null) {
			storage_obj = DataFileReader.ReaderInit (Lc_name);
			
			if (storage_obj == null)
				storage_obj = new DataFileReader();
			
			_m_Storage.put(Lc_name, storage_obj);
		}
		
		if (storage_obj != null && !storage_obj.isEmpty())
			ret = storage_obj;
		
		return (ret);
	}
	
	public static void Release () {
		if (_m_Storage != null) {
			for (DataFileReader this_reader : _m_Storage.values()) {
			    this_reader.Release();
			}
			_m_Storage.clear();
//			Iterator<Entry<String, DataFileReader>> it = _m_Storage.entrySet().iterator();
//			while (it.hasNext()) {
//				Hashtable.Entry<String, DataFileReader> pairs = 
//						(Hashtable.Entry<String, DataFileReader>)it.next();
//
////				it.remove(); // avoids a ConcurrentModificationException
//				pairs.getValue().Release();
//				_m_Storage.remove(pairs.getKey());
//			}
		}
	}
}
