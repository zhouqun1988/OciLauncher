package com.allwinner.theatreplayer.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Xml;

import com.allwinner.theatreplayer.launcher.data.AppInfo;
import com.allwinner.theatreplayer.launcher.data.Category;
import com.allwinner.theatreplayer.launcher.data.CellInfo;
import com.allwinner.theatreplayer.launcher.data.Constants;
import com.allwinner.theatreplayer.launcher.util.Utils;

public class LoadApplicationInfo {

	private static final String CATEGORY = "category";
	private static final String APPLICATION = "application";

	@SuppressWarnings("resource")
	private static InputStream getCategoryXML(Context context) {

		InputStream stream = null;

//		File file = new File(Constants.PATH_SYSTEM_ETC
//				+ Constants.DEFAULT_CATEGORY);
//		if (file.exists()) {
//			try {
//				stream = new FileInputStream(file);
//			} catch (Exception e) {
//				Utils.log("open file err!");
//			}
//		} else {
			try {
				stream = context.getResources().getAssets()
						.open("default_category.xml");
			} catch (Exception e) {
				Utils.log("open file err!");
			}
//		}

		return stream;
	}

	private static ArrayList<Category> parse(Context context, InputStream is) {
		ArrayList<Category> categoryList = new ArrayList<Category>();
		Category category = null;
		ArrayList<CellInfo> cellInfoList = null;
		// int appCount = 0;
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "UTF-8");
			int eventType = parser.getEventType();
			CellInfo cellInfo = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals(CATEGORY)) {
						cellInfoList = new ArrayList<CellInfo>();
						category = new Category();
						category.headline = parser.getAttributeValue(null,
								"headline");
						String strPosition = parser.getAttributeValue(null,
								"position");
						if (strPosition != null && !strPosition.equals("")) {
							category.position = Integer.valueOf(strPosition);
						}
						String strType = parser.getAttributeValue(null, "type");
						if (strType != null && !strType.equals("")) {
							category.type = Integer.valueOf(strType);
						}

					}
					if (parser.getName().equals(APPLICATION)) {
						cellInfo = new CellInfo();
						cellInfo.title = parser
								.getAttributeValue(null, "title");
						if (category != null) {
							cellInfo.type = category.type;
						}

						cellInfo.packageName = parser.getAttributeValue(null,
								"packageName");
						cellInfo.className = parser.getAttributeValue(null,
								"className");
						cellInfo.icon = parser.getAttributeValue(null, "icon");
						if (cellInfo.icon.equals("@null")) {
							cellInfo.icon = null;
						}
						cellInfo.backgroundPic = parser.getAttributeValue(null,
								"backgroundPic");
						cellInfo.backgroundColour = parser.getAttributeValue(
								null, "backgroundColour");
						cellInfo.portrait = parser.getAttributeValue(null,
								"portrait");
						if (cellInfoList != null) {							
//							Log.i( "cellInfo title = "+cellInfo.title);
//							Log.i( "cellInfo packageName = "+cellInfo.packageName);
							cellInfoList.add(cellInfo);
						}
					}
				case XmlPullParser.END_TAG:
					if (parser.getName().equals(CATEGORY)) {
						// if (cellInfo != null&&appCount<Constants.CELL_COUNT)
						// {
						// //if(cellInfo.type!=3||isInstalled(context,
						// cellInfo.packageName)){
						// cellInfoList.add(cellInfo);
						// appCount++;
						// //}
						// cellInfo = null;
						// }
						if (cellInfoList != null && cellInfoList.size() > 0
								&& category != null && categoryList != null) {
							category.cellInfoList = cellInfoList;
							categoryList.add(category);
						}

					}
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return categoryList;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Category> loadCellInfoFromSysXml(Context context) {
		ArrayList<Category> categoryList = null;
		InputStream inStream = getCategoryXML(context);
		if (inStream != null) {
			categoryList = parse(context, inStream);
		}
		if (categoryList != null) {
			@SuppressWarnings("rawtypes")
			Comparator comp = new Comparator() {
				public int compare(Object o1, Object o2) {
					Category p1 = (Category) o1;
					Category p2 = (Category) o2;
					if (p1.position < p2.position)
						return -1;
					else if (p1.position == p2.position)
						return 0;
					else if (p1.position > p2.position)
						return 1;
					return 0;
				}
			};
			Collections.sort(categoryList, comp);
		}		
		return categoryList;
	}

	public static boolean isInstalled(Context context, String pkg) {
		final PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(pkg);
		if (intent != null) {
			return true;
		}
		return false;
	}
	
	public static void LoadAppInfo(Context context, ArrayList<AppInfo> mAppInfoArray) {
        if(mAppInfoArray != null){
            mAppInfoArray.clear();
        }
        
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        int length = packages.size();
        
        for (int i = 0; i < length; i++) {
            PackageInfo packageInfo = (PackageInfo) packages.get(i);
            AppInfo appInfo = new AppInfo();
            
            appInfo.appName = packageInfo.applicationInfo.loadLabel(
            		context.getPackageManager()).toString();
            appInfo.appBuildNum = packageInfo.versionName;
            appInfo.appIcon = packageInfo.applicationInfo
                    .loadIcon(context.getPackageManager());
            appInfo.packageName = packageInfo.applicationInfo.packageName;
            
            Log.i("app name ="+appInfo.appName);
            Log.i("version name ="+appInfo.appBuildNum);
            Log.i("package name ="+appInfo.packageName);
            
            if(appInfo.packageName.indexOf("android") == -1 && appInfo.packageName.indexOf("softwinner") == -1){
                mAppInfoArray.add(appInfo);
            }
            //mStringArray.add(appInfo.appName);
            
        }
    }
}
