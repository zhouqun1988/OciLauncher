package com.allwinner.theatreplayer.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.allwinner.theatreplayer.launcher.data.Constants;

public class LoadLauncherConfig {
	private static final String TAG_ARRANGEMENT = "arrangement";

	public static boolean loadConfigFromSysXml(Context context) {
		boolean bAuthorized = false;
		try {
			InputStream slideInputStream = null;
			try {
				slideInputStream = context.getResources().getAssets()
						.open("launcher_cfg.xml");
			} catch (Exception e) {
				Log.i("open file err!");
			}
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(slideInputStream, "UTF-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:
						if (parser.getName().equals(TAG_ARRANGEMENT)) {
							bAuthorized = Boolean.parseBoolean(parser
									.getAttributeValue(null, "authorized"));
						}
					case XmlPullParser.END_TAG:
						break;
					}
					eventType = parser.next();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bAuthorized;
	}

	public static ArrayList<String> getProtectList(Context context) {
		ArrayList<String> protectList = null;
		try {
				InputStream slideInputStream = null;
				try {
					slideInputStream = context.getResources().getAssets()
							.open("launcher_cfg.xml");
				} catch (Exception e) {
					Log.i("open file err!");
				}
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(slideInputStream, "UTF-8");
				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						if (tag.equalsIgnoreCase("protectarray")) {
							protectList = new ArrayList<String>();
						}
						if (protectList != null) {
							if (tag.equalsIgnoreCase("package")) {
								String packageName = parser.getAttributeValue(
										null, "name");
								protectList.add(packageName);
								//Log.i("packageName: "+packageName);
							}
						}
					case XmlPullParser.END_TAG:
						break;
					}
					eventType = parser.next();
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return protectList;
	}
}
