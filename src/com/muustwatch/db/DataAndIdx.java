package com.muustwatch.db;

import com.muustwatch.StockDetailData;

public class DataAndIdx {
	StockDetailData m_data = null;
	Long            m_row_idx = (long) -1;
	
	public DataAndIdx (StockDetailData in_data, Long row_idx) {
		m_data = in_data;
		m_row_idx = row_idx;
	}
	
	public Long getRowIdx () { return (m_row_idx); }
	public StockDetailData getData () { return (m_data); }
}
