package com.muustwatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StockListAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	YahooSymbolList symbols=new YahooSymbolList();
		
	public StockListAdapter(Context context, YahooSymbolList data){
		mInflater = LayoutInflater.from(context);
		symbols = data;
	}
	
	public void setData(YahooSymbolList data)
	{
		symbols = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return symbols.size();
	}

	@Override
	public YahooSymbol getItem(int position) {
		return symbols.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
        
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.items, null);

            holder = new ViewHolder();
           
            holder.stockname = (TextView) convertView.findViewById(R.id.symbol);
            holder.exch = (TextView) convertView.findViewById(R.id.exch);
                
            
            convertView.setTag(holder);
        } else {
            
            holder = (ViewHolder) convertView.getTag();
        }         
        YahooSymbol item = symbols.get(position);
        holder.stockname.setText(item.getName()+"\n("+item.getSymbol()+")");
        holder.exch.setText(item.getExch()+"-"+item.getType());
         
        return convertView;
	}
	
	class ViewHolder {
    	
        TextView stockname, exch;   
    }
}
