package com.allwinner.theatreplayer.launcher.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;

import com.allwinner.theatreplayer.launcher.LauncherApp;
import com.allwinner.theatreplayer.launcher.LoadApplicationInfo;
import com.allwinner.theatreplayer.launcher.Log;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.AppInfo;
import com.allwinner.theatreplayer.launcher.data.ApplicationInfo;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.view.GridViewAdatper;
import com.allwinner.theatreplayer.launcher.view.GridViewAdatper.GridViewItemClickListener;
import com.allwinner.theatreplayer.launcher.view.GridViewAdatper.ViewHolder;

public class LocalAppActivity extends BaseActivity implements
		GridViewItemClickListener {
	//private ArrayList<ApplicationInfo> mAppInfoList;
	private GridViewAdatper mGridViewAdatper = null;
	private GridView mGridView = null;
	private ArrayList<AppInfo> mAppInfoArray;//存放所有 系统下已安装app
	private Context mContext;//当前上下文
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("name", "trim");
        outState.putString("time", "2016-04-15");
        Log.i("onSaveInstanceState");
    }





    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("onCreate()");
		
		setContentView(R.layout.gridview_layout);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
		mContext = getBaseContext();
		if(savedInstanceState != null) {
		    Log.i("name: "+savedInstanceState.getString("name"));
		    Log.i("time: "+savedInstanceState.getString("time"));
		}
		if(mAppInfoArray == null){
		    mAppInfoArray = new ArrayList<AppInfo>();
		    LoadApplicationInfo.LoadAppInfo(mContext, mAppInfoArray);
		}
		setGridViewStyle();
		
	}

	
	


	@Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i("onResume()");
    }





    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i("onPause()");
    }





    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i("onDestroy()");
    }





    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.i("onStop()");
    }





    @Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	private void setGridViewStyle() {
		mGridView = (GridView) findViewById(R.id.gridview);

		int mColumnWidth = (int) getResources()
				.getDimension(R.dimen.cell_width);
		int appCount = mAppInfoArray.size();
		Log.i("appCount = "+appCount);
		LayoutParams lp = (LayoutParams) mGridView.getLayoutParams();
		switch (appCount) {
    		case 1:
    			lp.width = mColumnWidth;
    			mGridView.setNumColumns(1);
    			break;
    		case 2:
    			lp.width = mColumnWidth * 2;
    			mGridView.setNumColumns(2);
    			break;
    		case 3:
    		case 5:
    		case 6:
    			lp.width = mColumnWidth * 3;
    			mGridView.setNumColumns(3);
    			break;
    		case 4:
    		case 7:
    		case 8:
    			lp.width = mColumnWidth * 4;
    			mGridView.setNumColumns(4);
    			break;
    		default:mGridView.setNumColumns(4);break;
		}

		mGridViewAdatper = new GridViewAdatper(this, mAppInfoArray, this);
		mGridView.setAdapter(mGridViewAdatper);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                // TODO Auto-generated method stub
                Log.i("view: "+view
                        +"pos = "+position
                        +"id = "+id);
                ViewHolder holder = (ViewHolder) view.getTag();
                int pos = holder.getPosition();
                Log.d("OnClickListener pos = "+pos);
                AppInfo appInfo = mAppInfoArray.get(position);
                
                Log.d("OnClickListener view = "+view);
                Log.d("OnClickListener view Id = "+view.getId());
                Log.d("OnClickListener position = "+position);
                Log.d("OnClickListener appInfo.appName: "+appInfo.appName);
                Log.d("OnClickListener appInfo.appBuildNum: "+appInfo.appBuildNum);
                
                Log.i("start appInfo: "+appInfo.appName);
                Log.i("start packageName: "+appInfo.packageName);
                LauncherApp.getInstance().getModel().startThirdApk(appInfo.packageName);
            }
        });
	}

	@Override
	public void dismissWindow() {
		//finish();
	}
	
	
}