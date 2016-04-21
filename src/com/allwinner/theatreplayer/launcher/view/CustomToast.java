package com.allwinner.theatreplayer.launcher.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allwinner.theatreplayer.launcher.R;

public class CustomToast {
	private static int mOldMsgId;
	private static String mOldMsg;
	protected static Toast mToast = null;
	private static long mOneTime = 0;
	private static long mTwoTime = 0;
	private static TextView mCotent = null;

	public static synchronized void showToast(Context context, int resid) {
		if (context != null && mToast == null) {
			LayoutInflater inflater = LayoutInflater.from(context
					.getApplicationContext());
			View view = inflater.inflate(R.layout.custom_toast, null);
			mCotent = (TextView) view.findViewById(R.id.toast_content);
			mCotent.setText(resid);
			mToast = new Toast(context.getApplicationContext());
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.setDuration(Toast.LENGTH_LONG);
			mToast.setView(view);
			mToast.show();
			mOneTime = System.currentTimeMillis();
		} else {
			mTwoTime = System.currentTimeMillis();
			if (resid == mOldMsgId) {
				if (mTwoTime - mOneTime > Toast.LENGTH_LONG) {
					mToast.show();
				}
			} else {
				mOldMsgId = resid;
				if (mCotent != null) {
					mCotent.setText(resid);
				}
				if (mToast != null) {
					mToast.show();
				}
			}
		}
		mOneTime = mTwoTime;
	}

	public static synchronized void showToast(Context context, String info) {
		if (context != null && mToast == null) {
			LayoutInflater inflater = LayoutInflater.from(context
					.getApplicationContext());
			View view = inflater.inflate(R.layout.custom_toast, null);
			mCotent = (TextView) view.findViewById(R.id.toast_content);
			mCotent.setText(info);
			mToast = new Toast(context.getApplicationContext());
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.setDuration(Toast.LENGTH_SHORT);
			mToast.setView(view);
			mToast.show();
			mOneTime = System.currentTimeMillis();
		} else {
			mTwoTime = System.currentTimeMillis();
			if (info.equals(mOldMsg)) {
				if (mTwoTime - mOneTime > Toast.LENGTH_LONG) {
					mToast.show();
				}
			} else {
				mOldMsg = info;
				if (mCotent != null) {
					mCotent.setText(info);
				}
				if (mToast != null) {
					mToast.show();
				}
			}
		}
		mOneTime = mTwoTime;
	}

	public static synchronized void toast(Context context, String str) {
		if (mToast == null && context != null) {
			mToast = Toast.makeText(context.getApplicationContext(), "",
					Toast.LENGTH_SHORT);
		}
		if (mToast != null) {
			mToast.setText(str);
			mToast.show();
		}

	}

}
