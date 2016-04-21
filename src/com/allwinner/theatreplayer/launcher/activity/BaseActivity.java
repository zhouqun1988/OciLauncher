package com.allwinner.theatreplayer.launcher.activity;

import android.app.Activity;
import android.os.Bundle;

import com.allwinner.theatreplayer.launcher.Log;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("BaseActivity: "+getClass().getSimpleName());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
