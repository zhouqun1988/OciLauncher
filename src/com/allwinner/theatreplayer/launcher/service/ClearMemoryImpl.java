package com.allwinner.theatreplayer.launcher.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Process;

import com.allwinner.theatreplayer.launcher.LoadLauncherConfig;
import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.util.Utils;

public class ClearMemoryImpl {
	private final int MAX_TASKS = 30;
	private int MAX_PROTECT_TASKS = 2;
	private Context mContext = null;
	private ActivityManager mActivityManager = null;
	private PackageManager mPackageManager = null;
	private CharSequence[] mProtectPackage;
	private ArrayList<String> mProtectPackageName = new ArrayList<String>();
	private ArrayList<String> mRecentPackageName = new ArrayList<String>();
	private String mPackageName;

	public ClearMemoryImpl(Context context) {
		mContext = context;
		mPackageName = context.getPackageName();
		mActivityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		mPackageManager = mContext.getPackageManager();

		getProtectList();

		MAX_PROTECT_TASKS = context.getResources().getInteger(
				R.integer.maxs_protext_task);
		Utils.log("Total memory in system is " + getTotalMemory() + "M");
	}

	private void getProtectList() {
		mProtectPackageName = LoadLauncherConfig.getProtectList(mContext);

		if (mProtectPackageName == null) {
			mProtectPackageName = new ArrayList<String>();
			mProtectPackage = mContext.getResources().getTextArray(
					R.array.protect_pacakge);
			for (CharSequence p : mProtectPackage) {
				mProtectPackageName.add(p.toString());
			}
		}
	}

	public void start() {
		Utils.log("Start clear memory, detected avail memory in system is "
				+ getAvailMemory(mContext) + "M");
		updateRecentPackage();
		removeTask();
		Utils.log("After clear memory, avail memory in system is "
				+ getAvailMemory(mContext) + "M");
	}

	@SuppressWarnings("unchecked")
	private void updateRecentPackage() {
		final int origPri = Process.getThreadPriority(Process.myTid());
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager
				.getRecentTasks(MAX_TASKS, ActivityManager.RECENT_WITH_EXCLUDED);

		if (recentTasks == null) {
			return;
		}
		if (mRecentPackageName != null) {
			mRecentPackageName.clear();
		} else {
			mRecentPackageName = new ArrayList<String>();
		}
		if (mProtectPackageName != null) {
			mRecentPackageName = (ArrayList<String>) mProtectPackageName
					.clone();
		}

		int i = 0;
		for (ActivityManager.RecentTaskInfo recent : recentTasks) {
			ResolveInfo resolveInfo = mPackageManager.resolveActivity(
					recent.baseIntent, 0);
			if (resolveInfo == null) {
				continue;
			}

			if (mRecentPackageName
					.contains(resolveInfo.activityInfo.packageName)) {
				continue;
			}
			mRecentPackageName.add(resolveInfo.activityInfo.packageName);
			Utils.log("RecentTaskInfo = "
					+ resolveInfo.activityInfo.packageName);
			i++;
			if (i == MAX_PROTECT_TASKS) {
				break;
			}
		}

		Process.setThreadPriority(origPri);
	}

	private void removeTask() {
		List<ActivityManager.RunningAppProcessInfo> list = mActivityManager
				.getRunningAppProcesses();
		long startTime = System.currentTimeMillis();
		if (list == null) {
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

			Utils.log("pid=" + apinfo.pid + "   processName="
					+ apinfo.processName + "  importance=" + apinfo.importance);
			String[] pkgList = apinfo.pkgList;
			boolean isFound = false;
			for (String recent : mRecentPackageName) {
				if (apinfo.processName.equals(recent)) {
					isFound = true;
					break;
				}
			}

			if (isFound) {
				continue;
			}

			if (mPackageName.equals(apinfo.processName)) {
				continue;
			}

			if (apinfo.importance >= (ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)) {
				for (int j = 0; j < pkgList.length; j++) {
					mActivityManager.killBackgroundProcesses(pkgList[j]);
					android.util.Log.d("TPLauncher", "kill process "
							+ pkgList[j]);
				}
			}
		}

		Utils.log("removeTask cost time = "
				+ (System.currentTimeMillis() - startTime));
	}

	private long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / (1024 * 1024);
	}

	private long getTotalMemory() {
		String str1 = "/proc/meminfo";
		String str2;
		String[] stringArray;
		long initialMemory = 0;

		try {
			FileReader fileReader = new FileReader(str1);
			BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
			str2 = bufferedReader.readLine();

			if (str2 != null) {
				stringArray = str2.split("\\s+");
				for (String num : stringArray) {
					Utils.log(num + "\t");
				}

				initialMemory = Integer.valueOf(stringArray[1]).intValue() * 1024;
				bufferedReader.close();
			}

		} catch (IOException e) {
		}
		return initialMemory / (1024 * 1024);
	}

	public void muteNetRadio(String newStartPkg) {
		if (newStartPkg.equals(Constants.PACKAGE_LIVE)
				|| newStartPkg.equals(Constants.PACKAGE_VOD)
				|| newStartPkg.equals(Constants.PACKAGE_OLDER_ZONE)) {
			Intent intent = new Intent("com.allwinner.action.TP_MUTE_SOUND");
			mContext.sendBroadcast(intent);
		}
	}

}
