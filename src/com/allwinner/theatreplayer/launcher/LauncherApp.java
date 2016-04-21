package com.allwinner.theatreplayer.launcher;

import java.util.ArrayList;

import android.app.Application;
import android.view.WindowManager;

import com.allwinner.theatreplayer.launcher.data.CellInfo;

public class LauncherApp extends Application {
	private static LauncherApp mInstance;
	public LauncherModel mModel;
	private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();

	public static ArrayList<CellInfo> gCellInfoList =null;
	public static boolean gIsFirstRunQQ = false;
	@Override
	public void onCreate() {
		super.onCreate();
		mModel = new LauncherModel(this);
		gCellInfoList = new ArrayList<CellInfo>();
		setInstance(this);
	}

	private void setInstance(LauncherApp instatnce){
		mInstance = this;
	}
	
	public static LauncherApp getInstance() {
		return mInstance;
	}

	public LauncherModel getModel() {
		return mModel;
	}
	
	public WindowManager.LayoutParams getWindowParams() {
		return windowParams;
	}
}
