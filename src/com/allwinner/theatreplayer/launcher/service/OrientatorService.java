package com.allwinner.theatreplayer.launcher.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.allwinner.theatreplayer.launcher.R;

public class OrientatorService extends Service {
	private static ImageButton mButton = null;
	private static WindowManager mWM = null;
	private static WindowManager.LayoutParams mLP = null;
	public static boolean mIsClose = true;
	public static boolean mIsForcePortrait = false;

	public OrientatorService() {

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			initialize();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressLint("InflateParams")
	private void initialize() {
		mButton = ((ImageButton) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.screen, null));
		Drawable drawable = getResources().getDrawable(R.drawable.transparent)
				.mutate();
		drawable.setAlpha(0);
		mButton.setImageDrawable(drawable);
		mWM = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
	}

	@SuppressLint("RtlHardcoded")
	public static synchronized void forceLandscapeMode() {
		try {
			if (mLP == null) {
				mLP = new WindowManager.LayoutParams();
				mLP.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
				mLP.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
				mLP.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				mLP.gravity = Gravity.LEFT | Gravity.TOP;
				mLP.width = 1;
				mLP.height = 1;
				mWM.addView(mButton, mLP);
			} else {
				mLP.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				mWM.updateViewLayout(mButton, mLP);
			}
			mIsClose = false;
			mIsForcePortrait = false;
		} catch (Exception e) {
			
		}

	}

	@SuppressLint("RtlHardcoded")
	public static synchronized  void forcePortraitMode() {
		try {
			if (mLP == null) {
				mLP = new WindowManager.LayoutParams();
				mLP.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
				mLP.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
				mLP.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				mLP.gravity = Gravity.LEFT | Gravity.TOP;
				mLP.width = 1;
				mLP.height = 1;
				mWM.addView(mButton, mLP);
			} else {
				mLP.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				mWM.updateViewLayout(mButton, mLP);
			}
			mIsClose = false;
			mIsForcePortrait = true;
		} catch (Exception e) {
			
		}

	}

	public static synchronized void closeOrientatorMode() {
		try {
			if (mWM != null && mLP != null) {
				mWM.removeView(mButton);
				mLP = null;
			}
			mIsClose = true;
			mIsForcePortrait = false;
		} catch (Exception e) {
		}

	}

}