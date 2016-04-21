package com.allwinner.theatreplayer.launcher.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.allwinner.theatreplayer.launcher.R;
import com.allwinner.theatreplayer.launcher.util.Utils;

@SuppressLint("HandlerLeak")
public class ClearMemoryService extends Service {
	private static ClearMemoryService mInstance;
	private final int START_CLEAR = 1;
	private int mCheckInterval = 60000;
	private ClearMemoryImpl mClearMemoryImpl = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_CLEAR:
				startClear();
				break;
			}
		}
	};

	public static ClearMemoryService getInstance() {
		return mInstance;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.log("ClearMemoryService onCreate");
		mInstance = this;
		mClearMemoryImpl = new ClearMemoryImpl(this);
		mCheckInterval = this.getResources().getInteger(
				R.integer.check_interval);
		mHandler.sendEmptyMessageDelayed(START_CLEAR, mCheckInterval);
	}

	private void startClear() {
		Utils.log("ClearMemoryService startClear");
		mClearMemoryImpl.start();
		mHandler.sendEmptyMessageDelayed(START_CLEAR, mCheckInterval);
	}

}
