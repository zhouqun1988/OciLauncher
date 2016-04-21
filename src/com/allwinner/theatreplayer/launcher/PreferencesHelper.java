package com.allwinner.theatreplayer.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import com.allwinner.theatreplayer.launcher.R;

public class PreferencesHelper {

	private static SharedPreferences sPref;
	private static final String PREF_NAME = "TPLauncher";

	private static final String KEY_CURRENT_TEMPERATURE = "current_temperature";
	private static final String KEY_WEATHER = "weather";
	private static final String KEY_WEATHER_ICON = "weather_icon";
	private static final String KEY_CITY = "city";
	private static final String KEY_DATE = "date";

	private static final String KEY_LIVE_AUTHORIZED = "live_authorized";

	private static Context sContext;

	private PreferencesHelper(Context context) {
		sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	private static class SingleInstanceHolder {
		static PreferencesHelper sInstance = new PreferencesHelper(sContext);
	}

	public static PreferencesHelper getInstance(Context context) {
		sContext = context;
		return SingleInstanceHolder.sInstance;
	}

	public String getCurrentTemperature() {
		return sPref.getString(KEY_CURRENT_TEMPERATURE, "");
	}

	public void saveCurrentTemperature(String value) {
		if (value == null) {
			return;
		}
		sPref.edit().putString(KEY_CURRENT_TEMPERATURE, value).commit();
	}

	public String getWeather() {
		return sPref.getString(KEY_WEATHER,
				sContext.getString(R.string.dft_weather));
	}

	public void saveWeather(String value) {
		if (value == null) {
			return;
		}
		sPref.edit().putString(KEY_WEATHER, value).commit();
	}

	public int getWeatherIcon() {
		return sPref.getInt(KEY_WEATHER_ICON, -1);
	}

	public void saveWeatherIcon(int icon) {
		sPref.edit().putInt(KEY_WEATHER_ICON, icon).commit();
	}

	public String getCity() {
		return sPref.getString(KEY_CITY, null);
	}

	public void saveCity(String value) {
		if (value == null) {
			return;
		}
		sPref.edit().putString(KEY_CITY, value).commit();
	}

	public String getUpdateDate() {
		return sPref.getString(KEY_DATE,
				DateFormat.format("MM/dd", System.currentTimeMillis())
						.toString());
	}

	public void saveUpdateDate(String value) {
		if (value == null) {
			return;
		}
		sPref.edit().putString(KEY_DATE, value).commit();
	}

	public boolean getLiveAuthorized() {
		return sPref.getBoolean(KEY_LIVE_AUTHORIZED, false);
	}

	public void saveLiveAuthorized(boolean value) {
		sPref.edit().putBoolean(KEY_LIVE_AUTHORIZED, value).commit();
	}
}
