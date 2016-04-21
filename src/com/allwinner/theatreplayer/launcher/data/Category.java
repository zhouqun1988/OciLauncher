package com.allwinner.theatreplayer.launcher.data;

import android.util.Log;

import java.util.ArrayList;

public class Category {
	public String headline = null;
	public int position = -1;
	public int type = -1;
	public ArrayList<CellInfo> cellInfoList = null;
	
	public void show(Category category){
	    
	    Log.i("Trim", "headline: "+category.headline);
	    Log.i("Trim", "position: "+category.position);
	    Log.i("Trim", "type: "+category.type);
	    Log.i("Trim", "cellInfoList: "+category.cellInfoList);
	}
}
