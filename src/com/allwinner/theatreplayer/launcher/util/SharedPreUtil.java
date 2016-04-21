package com.allwinner.theatreplayer.launcher.util;

import com.allwinner.theatreplayer.launcher.LauncherApp;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class SharedPreUtil {

	public static boolean isFirstRunQQ(String packageName) {
		int currentVersion = getQQVersionNum(packageName);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LauncherApp.getInstance().getApplicationContext());
		int lastVersion = prefs.getInt(packageName, 0);
		if (currentVersion > lastVersion) {
			return true;
		}
		return false;
	}

	public static void saveQQVersionNum(String packageName) {
		int currentVersion = getQQVersionNum(packageName);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LauncherApp.getInstance().getApplicationContext());
		prefs.edit().putInt(packageName, currentVersion).commit();
	}
	
	private static int getQQVersionNum(String packageName) {
		PackageInfo info;
		try {
			info = LauncherApp.getInstance().getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
