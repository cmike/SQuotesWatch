package com.muustwatch;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

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

public class StockDetailData implements Parcelable {
	final private String date_format_str = "MM/dd/yy";
	final private String time_format_pattern = "hh:mm";
	private String makeTimeOutputFormat () { return (time_format_pattern + " a"); }
	@SuppressWarnings("unused")
	private String makeFullOutputFormat () {
		return (date_format_str + " " + makeTimeOutputFormat ());
	}
	private String makeInputFormatByExample (String time_exmple) {
		String time_format = time_format_pattern;
		if (time_exmple.split (" ").length > 1) {
			time_format = time_format.concat(" ");
		}
		time_format = time_format.concat("a");
		return (date_format_str + " " + time_format);
	}
	private class MyBound {
		private Boolean is_defined;
		private double  value;
		
		MyBound () { value = 0.0; is_defined = false;}
		MyBound (MyBound in) { value = in.value; is_defined = in.is_defined; }
		
		boolean fromString (String in) {
			boolean isOK = true;
			double  inVal = 0.0;
			
			try {
				inVal = Double.parseDouble(in);
			} catch (Exception e) {
				isOK = false;
			}
			
			if (isOK) {
			    value = inVal;
			    is_defined = true;
			}
			return (isOK);
		}
		@SuppressWarnings("unused")
		void setValue (double in) { value = in; is_defined = true; }
		void setUnDef () {value = 0.0; is_defined = false;}
		
		int getTriggerInt () { return (is_defined ? 1 : 0); }
		@SuppressWarnings("unused")
		Boolean TriggerIsOn () { return (is_defined); }
		
		double getValue () { return value; }
		String asString () {
			String res = (is_defined) ? String.format ("%.2f", value) : "";
			return (res);
		}
	}
	private class MyDouble {
		private double dbl_val;
		private String as_is;
		private Boolean is_dbl_ok;
		
		MyDouble () { dbl_val = 0.0; as_is = ""; is_dbl_ok = false;}
		void copy (MyDouble copy_from) {
			this.dbl_val = copy_from.dbl_val;
			this.as_is = copy_from.as_is;
			this.is_dbl_ok = copy_from.is_dbl_ok;
		}
		
		void fromString (String in) {
			is_dbl_ok = true;
			try {
				dbl_val = Double.parseDouble(in);
			} catch (Exception e) {
				as_is = in;
				is_dbl_ok = false;
			}
		}
		String asString () {
			String result = (is_dbl_ok) ? String.format ("%.2f", dbl_val) : as_is;
			return (result);
		}
		
		Boolean is_valid_double () { return (is_dbl_ok); }
	}
	private String symbol;
	private MyDouble price;
	private String tr_date;
	private String tr_time;
	private Date   trDateTime;    
	private MyDouble change;
	private MyDouble open;
	private MyDouble high;
	private MyDouble low;
	private MyDouble prev_close;
	private Long volume;
	private MyBound ubound;
	private MyBound lbound;
	
	public String getSymbol () { return (symbol); }
	public void setSymbol (String in) {	symbol = in; }
	
	public String getPrevClose () {	return (prev_close.asString()); }
	public void setPrevClose (String in) { prev_close.fromString(in); }
	
	public String getPrice () {	return (price.asString()); }
	public void setPrice (String in) { price.fromString(in); }
	
	public String getTrDate() {
		if (trDateTime != null) {
			SimpleDateFormat format = new SimpleDateFormat(date_format_str);
			tr_date = format.format(trDateTime);
		}
		return (tr_date);
	}

    private void DateTimeStr2Date () {
    	String inString = tr_date + " " + tr_time;
		boolean ok = true;
		String fmt = makeInputFormatByExample(tr_time);
		SimpleDateFormat format = new SimpleDateFormat (fmt);
		Date date = null;
		try {
			date = format.parse(inString);
//			System.out.println("Original string: " + inString);
//			System.out.println("Parsed date    : " + date.toString());
		} catch (Exception pe) {
			System.out.println("ERROR: could not parse date in string \""
					+ inString + "\"");
			ok = false;
		}
		
		if (ok)
			trDateTime = date;
		else
			trDateTime = null;
    }
	public void setTrDate(String in) {
		tr_date = in;
		if (tr_time != null && tr_time.length() > 0) {
			DateTimeStr2Date ();
		}
	}
	
	public String getTrTime () { 
		if (trDateTime != null) {
			SimpleDateFormat format = new SimpleDateFormat(makeTimeOutputFormat ());
			tr_time = format.format(trDateTime);
		}
		return (tr_time); 
	}
	public void setTrTime (String in) { 
		tr_time = in; 
		if (tr_date != null && tr_date.length() > 0) {
			DateTimeStr2Date ();
		}
	}
	
	public String getChange () { return (change.asString()); }
	public void setChange (String in) { change.fromString(in); }
	
	public String getOpen () { return (open.asString()); }
	public void setOpen (String in) { open.fromString(in); }
	
	public String getHigh () { return (high.asString()); }
	public void setHigh (String in) { high.fromString(in); }
	
	public String getLow () { return (low.asString()); }
	public void setLow (String in) { low.fromString(in); }
	
	public String getVolume () { return Long.toString(volume); }
	public void setVolume (String in) { 
		try {
		  volume = Long.parseLong(in); 
		} catch (Exception e) {
		  volume = (long) 0;
		}
	}
	public boolean UBoundIsSet () { return (ubound != null && ubound.is_defined); }
	public int getIntUBoundTrig () { return ((ubound != null) ? ubound.getTriggerInt() : -1); }
	public double getDblUBoundVal () {return ((ubound != null) ? ubound.getValue() : 0.0); }
	public String getUBoundVal () {
		String res = (ubound != null && ubound.is_defined) ? ubound.asString() : "";
		
		return (res);
	}
	public boolean setUBound_fromString (String in) { 
		ubound = new MyBound(); return (ubound.fromString(in)); 
	}
	public void setUBoundUndef () { ubound = new MyBound(); ubound.setUnDef(); }
	public boolean LBoundIsSet () { return (lbound != null && lbound.is_defined); }
	public int getIntLBoundTrig () { return ((lbound != null) ? lbound.getTriggerInt() : -1); }
	public double getDblLBoundVal () {return ((lbound != null) ? lbound.getValue() : 0.0); }
	public String getLBoundVal () {
		String res = (lbound != null && lbound.is_defined) ? lbound.asString() : "";
		
		return (res);
	}
	public boolean setLBound_fromString (String in) { 
		lbound = new MyBound(); return (lbound.fromString(in)); 
	}
	public void setLBoundUndef () { lbound = new MyBound(); lbound.setUnDef(); }
	
	public Boolean is_data_loaded() {
		return (price.is_valid_double() && change.is_valid_double() && symbol.length() > 0);
	}

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getSymbol());
        out.writeString(getPrice());
        out.writeString(getTrDate());
        out.writeString(getTrTime());
        out.writeString(getChange());
        out.writeString(getOpen());
        out.writeString(getHigh());
        out.writeString(getLow());
        out.writeString(getPrevClose());
        out.writeString(getVolume());
        
        if (ubound != null) {
        	out.writeInt(ubound.getTriggerInt ());
        	out.writeString(ubound.asString());

        	if (lbound != null) {
        		out.writeInt(lbound.getTriggerInt ());
        		out.writeString(lbound.asString());
        	}
        }
    }

    public static final Parcelable.Creator<StockDetailData> CREATOR
            = new Parcelable.Creator<StockDetailData>() {
        public StockDetailData createFromParcel(Parcel in) {
            return new StockDetailData(in);
        }

        public StockDetailData[] newArray(int size) {
            return new StockDetailData[size];
        }
    };
    
    private StockDetailData(Parcel in) {
    	int  triggerON;
    	String trg_val;

		InitProperties();
    	setSymbol (in.readString());
    	setPrice (in.readString());
    	setTrDate (in.readString());
    	setTrTime (in.readString());
    	setChange (in.readString());
    	setOpen (in.readString());
    	setHigh (in.readString());
    	setLow (in.readString());
    	setPrevClose (in.readString());
    	setVolume (in.readString());
    	
    	if (in.dataAvail() > 0) {
    		triggerON = in.readInt();
    		trg_val = in.readString();

    		ubound = new MyBound ();
    		if (triggerON == 1) {
    			ubound.fromString(trg_val);
    		} else {
    			ubound.setUnDef();
    		}

    		if (in.dataAvail() > 0) {
    			lbound = new MyBound ();

    			triggerON = in.readInt();
    			trg_val = in.readString();

    			if (triggerON == 1) {
    				lbound.fromString(trg_val);
    			} else {
    				lbound.setUnDef();
    			}
    		}
    	}
    }

    private void InitProperties () {
		symbol = null;
		price = new MyDouble();
		tr_date = null;
		tr_time = null;
		trDateTime = null;    
		change = new MyDouble();;
		open = new MyDouble();
		high = new MyDouble();
		low = new MyDouble();
		prev_close = new MyDouble();
		volume = (long)0;
		ubound = null;
		lbound = null;
	}
	StockDetailData() {
		InitProperties();
	}
	
	public void Update (StockDetailData in) {
		
		if (!in.getSymbol().equals(symbol)) {
		  throw new Error ("Wrong Input Object");
		} else {
			price.copy(in.price);
			tr_date = in.tr_date;
			tr_time = in.tr_time;
			trDateTime = in.trDateTime;
			change.copy(in.change);
			open.copy(in.open);
			high.copy (in.high);
			low.copy(in.low);
			prev_close.copy(in.prev_close);
			volume = in.volume;
			
			if (in.ubound != null)
				ubound = new MyBound(in.ubound);
			
			if (in.lbound != null)
				lbound = new MyBound(in.lbound);
		}
	}
	
	public static StockDetailData getDetailsByCursor(Cursor quotas_db_crs) {
		StockDetailData data_item = null;
		if (quotas_db_crs != null) {
			try {
				data_item = new StockDetailData();

				data_item.setSymbol(quotas_db_crs.getString(1)); // "symbol"
				data_item.setPrice(quotas_db_crs.getString(2)); // "price"));
				data_item.setTrDate(quotas_db_crs.getString(3)); // "date"));
				data_item.setTrTime(quotas_db_crs.getString(4)); // "time"));
				data_item.setChange(quotas_db_crs.getString(5)); // "change"));
				data_item.setOpen(quotas_db_crs.getString(6)); // "open"));
				data_item.setHigh(quotas_db_crs.getString(7)); // "high"));
				data_item.setLow(quotas_db_crs.getString(8)); // "low"));
				data_item.setPrevClose(quotas_db_crs.getString(9)); // "prev_close"));
				data_item.setVolume(quotas_db_crs.getString(10)); // "volume"));

				int  triggerON = quotas_db_crs.getInt(11); // u_bound_trig
				String trg_val = quotas_db_crs.getString(12); // u_bound_val

				if (triggerON == 1) {
					data_item.setUBound_fromString(trg_val);
				} else {
					data_item.setUBoundUndef ();
				}

				triggerON = quotas_db_crs.getInt(13); // l_bound_trig
				trg_val = quotas_db_crs.getString(14); // l_bound_val

				if (triggerON == 1) {
					data_item.setLBound_fromString(trg_val);
				} else {
					data_item.setLBoundUndef ();
				}

			} catch (Exception db_ex) {
				data_item = null;
			}
		}

		return data_item;
	}
}