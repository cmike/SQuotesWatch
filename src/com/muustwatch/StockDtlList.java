package com.muustwatch;

import java.util.ArrayList;
import java.util.Collection;

import android.database.Cursor;

@SuppressWarnings("serial")
public class StockDtlList extends ArrayList<StockDetailData> {
	private class LstOfLongs extends ArrayList<Long> {
		public LstOfLongs () { super (); }
		@SuppressWarnings("unused")
		public LstOfLongs (int capacity) { super (capacity); }
	}
	
	private LstOfLongs row_ids = new LstOfLongs();
	public Long getDBrowID (int  item_idx) {
		Long ret = row_ids.get(item_idx);
		return (ret);
	}
	public StockDtlList() {
		// TODO Auto-generated constructor stub
	}

	public StockDtlList(int capacity) {
		super(capacity);
		// TODO Auto-generated constructor stub
	}

	
	public StockDtlList(Cursor quotas_db_crs) {
		StockDetailData data_item;
		if (quotas_db_crs != null && quotas_db_crs.moveToFirst())
			do {
				data_item = StockDetailData.getDetailsByCursor (quotas_db_crs);
				
				this.add(data_item);
				row_ids.add(quotas_db_crs.getLong(0));
				
			} while (quotas_db_crs.moveToNext());
	
			
	}

	public StockDtlList(Collection<? extends StockDetailData> collection) {
		super(collection);
		// TODO Auto-generated constructor stub
	}

}
