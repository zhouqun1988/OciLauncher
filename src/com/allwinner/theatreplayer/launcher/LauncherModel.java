package com.allwinner.theatreplayer.launcher;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.allwinner.theatreplayer.launcher.data.Constants;

public class LauncherModel {
	private Context mContext;
	private PackageManager mPManager = null;
	private static String strVodtype = "";
	private static String strUUid = "";

	public LauncherModel(Context mContext) {
		this.mContext = mContext;
		if(mContext!=null){
			mPManager = mContext.getApplicationContext().getPackageManager();
		}
	}

	public void startActivityByAction(String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (mContext!=null&&intent != null) {
			try {
				if (action.startsWith(Constants.PACKAGE_VOD)) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					setVodType(Constants.PACKAGE_VOD);
				}
				mContext.getApplicationContext().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext, action + " not found",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void startVstByType(String vodtype, String pkg) {
		Intent intent = new Intent();
		try {
			intent.setAction(pkg);
			intent.putExtra("vodtype", vodtype);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (!strVodtype.equals("") && !strVodtype.equals(vodtype)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			setVodType(vodtype);
			if(mContext!=null){
				mContext.getApplicationContext().startActivity(intent);
			}

		} catch (ActivityNotFoundException e) {
			if(mContext!=null){
				Toast.makeText(mContext.getApplicationContext(), pkg + " not found", Toast.LENGTH_SHORT)
					.show();
			}
		}
	}

	public void startMyVstByUUid(String uuid, int playerIndex, String pkg) {
		Intent intent = new Intent();
		try {
			intent.setAction(pkg);
			intent.putExtra("uuid", uuid);
			intent.putExtra("playerIndex", playerIndex);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (!strUUid.equals("") && !strUUid.equals(uuid)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			
			setUUID(uuid);
			if(mContext!=null){
				mContext.getApplicationContext().startActivity(intent);
			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), pkg + " not found", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void setUUID(String uuid){
		strUUid = uuid;
	}
	
	private void setVodType(String type){
		strVodtype = type;
	}
	
	public void startActivity(String pkg, String className) {

		Intent intent = new Intent();
		intent.setClassName(pkg, className);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (mContext!=null&&intent != null) {
			try {
				mContext.getApplicationContext().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext.getApplicationContext(), pkg + " not found", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void sendBroadcastToVST(String msg) {
		Intent intent = new Intent("com.allwinner.action.TP_BROADCAST_TO_VST");
		intent.putExtra("voice_command", msg);
		if(mContext!=null){
			mContext.getApplicationContext().sendBroadcast(intent);
		}
	}

	public void startThirdApk(String pkg) {
		if(mPManager==null){
			return;
		}
		Intent intent = mPManager.getLaunchIntentForPackage(pkg);
		if (mContext!=null&&intent != null) {
			try {
				mContext.getApplicationContext().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext.getApplicationContext(), pkg + " not found", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public void startThirdApk(String pkg, String type) {
		// ClearMemoryService.getInstance().muteNetRadio(pkg);
		if(mPManager==null){
			return;
		}
		Intent intent = mPManager.getLaunchIntentForPackage(pkg);
		if (mContext!=null&&intent != null) {
			try {
				//intent.putExtra("type", type);
				//mContext.startActivity(intent);
				if (type.equals("gallery")) {
					intent.putExtra("type", type);
					mContext.getApplicationContext().startActivity(intent);
					Intent intent1 = new Intent(
							"com.allwinner.action.GALLERY2_GALLERY");
					mContext.getApplicationContext().sendBroadcast(intent1);
				} else if (type.equals("movie")) {
					intent.putExtra("type", type);
					mContext.getApplicationContext().startActivity(intent);
					Intent intent2 = new Intent(
							"com.allwinner.action.GALLERY2_MOVIE");
					mContext.sendBroadcast(intent2);
				} else {
					ComponentName componentName = new ComponentName(pkg, type);
					Intent intent3 = new Intent();
					intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent3.setComponent(componentName);
					mContext.getApplicationContext().startActivity(intent3);
				}
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext.getApplicationContext(), pkg + " not found", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public int getWeatherTextStringId(int icon) {
		int weatherTextStringId;
		switch (icon) {
		case 1:
			weatherTextStringId = R.string.weather_01_sunny;
			break;
		case 2:
			weatherTextStringId = R.string.weather_02_mostly_sunny;
			break;
		case 3:
			weatherTextStringId = R.string.weather_03_partly_sunny;
			break;
		case 4:
			weatherTextStringId = R.string.weather_04_intermittent_clouds;
			break;
		case 5:
			weatherTextStringId = R.string.weather_05_hazy_sunshine;
			break;
		case 6:
			weatherTextStringId = R.string.weather_06_mostly_cloudy;
			break;
		case 7:
			weatherTextStringId = R.string.weather_07_cloudy;
			break;
		case 8:
			weatherTextStringId = R.string.weather_08_dreary;
			break;
		case 11:
			weatherTextStringId = R.string.weather_11_fog;
			break;
		case 12:
			weatherTextStringId = R.string.weather_12_showers;
			break;
		case 13:
			weatherTextStringId = R.string.weather_13_mostly_cloudy_with_showers;
			break;
		case 14:
			weatherTextStringId = R.string.weather_14_partly_sunny_with_showers;
			break;
		case 15:
			weatherTextStringId = R.string.weather_15_thunder_storms;
			break;
		case 16:
			weatherTextStringId = R.string.weather_16_mostly_cloudy_with_thunder_storms;
			break;
		case 17:
			weatherTextStringId = R.string.weather_17_partly_sunny_with_thunder_storms;
			break;
		case 18:
			weatherTextStringId = R.string.weather_18_rain;
			break;
		case 19:
			weatherTextStringId = R.string.weather_19_flurries;
			break;
		case 20:
			weatherTextStringId = R.string.weather_20_mostly_cloudy_with_flurries;
			break;
		case 21:
			weatherTextStringId = R.string.weather_21_partly_sunny_with_flurries;
			break;
		case 22:
			weatherTextStringId = R.string.weather_22_snow;
			break;
		case 23:
			weatherTextStringId = R.string.weather_23_mostly_cloudy_with_snow;
			break;
		case 24:
			weatherTextStringId = R.string.weather_24_ice;
			break;
		case 25:
			weatherTextStringId = R.string.weather_25_sleet;
			break;
		case 26:
			weatherTextStringId = R.string.weather_26_freezing_rain;
			break;
		case 29:
			weatherTextStringId = R.string.weather_29_rain_and_snow;
			break;
		case 30:
			weatherTextStringId = R.string.weather_30_hot;
			break;
		case 31:
			weatherTextStringId = R.string.weather_31_code;
			break;
		case 32:
			weatherTextStringId = R.string.weather_32_windy;
			break;
		case 33:
			weatherTextStringId = R.string.weather_33_clear;
			break;
		case 34:
			weatherTextStringId = R.string.weather_34_mostly_clear;
			break;
		case 35:
			weatherTextStringId = R.string.weather_35_partly_cloudy;
			break;
		case 36:
			weatherTextStringId = R.string.weather_36_intermittent_clouds;
			break;
		case 37:
			weatherTextStringId = R.string.weather_37_hazy_moonlight;
			break;
		case 38:
			weatherTextStringId = R.string.weather_38_mostly_cloudy;
			break;
		case 39:
			weatherTextStringId = R.string.weather_39_partly_cloudy_with_showers;
			break;
		case 40:
			weatherTextStringId = R.string.weather_40_mostly_cloudy_with_showers;
			break;
		case 41:
			weatherTextStringId = R.string.weather_41_partly_cloudy_with_thunder_storms;
			break;
		case 42:
			weatherTextStringId = R.string.weather_42_mostly_cloudy_with_thunder_storms;
			break;
		case 43:
			weatherTextStringId = R.string.weather_43_mostly_cloudy_with_flurries;
			break;
		case 44:
			weatherTextStringId = R.string.weather_44_mostly_cloudy_with_snow;
			break;
		default:
			weatherTextStringId = R.string.unknow_weather;
			break;
		}
		return weatherTextStringId;
	}

	public int getWeatherImg(int icon) {
		int weatherImgId;
		switch (icon) {
		case 1:
			weatherImgId = R.drawable.icon_white_01;
			break;
		case 2:
			weatherImgId = R.drawable.icon_white_02;
			break;
		case 3:
			weatherImgId = R.drawable.icon_white_03;
			break;
		case 4:
			weatherImgId = R.drawable.icon_white_04;
			break;
		case 5:
			weatherImgId = R.drawable.icon_white_05;
			break;
		case 6:
			weatherImgId = R.drawable.icon_white_06;
			break;
		case 7:
			weatherImgId = R.drawable.icon_white_07;
			break;
		case 8:
			weatherImgId = R.drawable.icon_white_08;
			break;
		case 11:
			weatherImgId = R.drawable.icon_white_11;
			break;
		case 12:
			weatherImgId = R.drawable.icon_white_12;
			break;
		case 13:
			weatherImgId = R.drawable.icon_white_13;
			break;
		case 14:
			weatherImgId = R.drawable.icon_white_14;
			break;
		case 15:
			weatherImgId = R.drawable.icon_white_15;
			break;
		case 16:
			weatherImgId = R.drawable.icon_white_16;
			break;
		case 17:
			weatherImgId = R.drawable.icon_white_17;
			break;
		case 18:
			weatherImgId = R.drawable.icon_white_18;
			break;
		case 19:
			weatherImgId = R.drawable.icon_white_19;
			break;
		case 20:
			weatherImgId = R.drawable.icon_white_20;
			break;
		case 21:
			weatherImgId = R.drawable.icon_white_21;
			break;
		case 22:
			weatherImgId = R.drawable.icon_white_22;
			break;
		case 23:
			weatherImgId = R.drawable.icon_white_23;
			break;
		case 24:
			weatherImgId = R.drawable.icon_white_24;
			break;
		case 25:
			weatherImgId = R.drawable.icon_white_25;
			break;
		case 26:
			weatherImgId = R.drawable.icon_white_26;
			break;
		case 29:
			weatherImgId = R.drawable.icon_white_29;
			break;
		case 30:
			weatherImgId = R.drawable.icon_white_30;
			break;
		case 31:
			weatherImgId = R.drawable.icon_white_31;
			break;
		case 32:
			weatherImgId = R.drawable.icon_white_32;
			break;
		case 33:
			weatherImgId = R.drawable.icon_white_33;
			break;
		case 34:
			weatherImgId = R.drawable.icon_white_34;
			break;
		case 35:
			weatherImgId = R.drawable.icon_white_35;
			break;
		case 36:
			weatherImgId = R.drawable.icon_white_36;
			break;
		case 37:
			weatherImgId = R.drawable.icon_white_37;
			break;
		case 38:
			weatherImgId = R.drawable.icon_white_38;
			break;
		case 39:
			weatherImgId = R.drawable.icon_white_39;
			break;
		case 40:
			weatherImgId = R.drawable.icon_white_40;
			break;
		case 41:
			weatherImgId = R.drawable.icon_white_41;
			break;
		case 42:
			weatherImgId = R.drawable.icon_white_42;
			break;
		case 43:
			weatherImgId = R.drawable.icon_white_43;
			break;
		case 44:
			weatherImgId = R.drawable.icon_white_44;
			break;
		default:
			weatherImgId = R.drawable.icon_white_01;
			break;
		}
		return weatherImgId;
	}

}
