package com.allwinner.theatreplayer.launcher.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.allwinner.theatreplayer.launcher.data.PosterInfo;

/**
 * Json结果解析类
 */
public class JsonParser {

	public static String parseIatResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}

	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for (int j = 0; j < items.length(); j++) {
					JSONObject obj = items.getJSONObject(j);
					if (obj.getString("w").contains("nomatch")) {
						ret.append("没有匹配结果.");
						return ret.toString();
					}
					ret.append("【结果】" + obj.getString("w"));
					ret.append("【置信度】" + obj.getInt("sc"));
					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		}
		return ret.toString();
	}

	public static ArrayList<PosterInfo> parsePosterInfo(String json) {
		ArrayList<PosterInfo> posterInfoList = new ArrayList<PosterInfo>();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("home");
			if (words != null && words.length() >= 3) {
				JSONArray items = words.getJSONObject(2).getJSONArray("items");
				for (int j = 0; j < items.length(); j++) {
					PosterInfo posterInfo = new PosterInfo();
					JSONObject obj = items.getJSONObject(j);
					posterInfo.img = obj.getString("img");
					posterInfo.title = obj.getString("txt");
					posterInfo.value = obj.getString("value");
					posterInfoList.add(posterInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return posterInfoList;
	}

}
