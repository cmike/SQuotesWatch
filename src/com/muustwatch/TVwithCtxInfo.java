package com.muustwatch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.TextView;

// The only aim is to provide getContextMenuInfo()
// See: "Identifying the view selected in a ContextMenu 
//       (in onContextItemSelected() method)"
//  in http://ogrelab.ikratko.com
public class TVwithCtxInfo extends TextView {

	public TVwithCtxInfo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TVwithCtxInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TVwithCtxInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override  
    protected ContextMenuInfo getContextMenuInfo() {  
        return new TVCtxMenuInfo(this);
	}
	public static class TVCtxMenuInfo implements ContextMenu.ContextMenuInfo {
		public TVCtxMenuInfo (View targ_view) {
			this.targetView = (TextView)targ_view;
		}
		
		public TextView targetView;
	}

}
