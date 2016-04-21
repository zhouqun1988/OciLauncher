package com.allwinner.theatreplayer.launcher.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Utils {
	private static String TAG = "TPLauncher.ClearMem";
	private static boolean Debug = false;

	public static String sdcardPathQQ = Environment
			.getExternalStorageDirectory() + "/OCI";

	public static final String DOWNLOAD_PATH = sdcardPathQQ + "/release.apk";

    public static final int CHECK_CYCLE_DAY=1;
    
    public static final String rcId = "oci_launcher";//oci_launcher_test
    
    public static final int CHECK_TIMEOUT=60*1000*5;
    
    public static int getVersionCode(Context context)//获取版本号(内部识别号)  
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionCode;  
        } catch (NameNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return 0;  
        }  
    } 
    public static String getVersion(Context context)//获取版本号  
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (NameNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return "";  
        }  
    }  
    
	public static void saveUpdateJson(String content,final String pathLicense) throws Exception {
		File file = new File(pathLicense);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		// 写入数据
		fos.write(content.getBytes());
		// 关闭输出流
		fos.close();
	}
    
	/**
	 * 
	 * @return String s="total=5;time=60*60*1000";
	 * @throws Throwable
	 */
	public static String readFile(String filepath) throws Throwable {
		File file = new File(filepath);
		FileInputStream fis = new FileInputStream(file);
		byte[] buf = new byte[1024];
		int len = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 读取数据
		while ((len = fis.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		byte[] data = baos.toByteArray();
		// 关闭流
		baos.close();
		fis.close();
		return new String(data);
	}
	
    public static boolean checkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
	
	public static void log(String str) {
		if (Debug) {
			Log.e(TAG, str);
		}
	}
	public static String getDeviceNameForConfigSystem() {
		return android.os.Build.MODEL;
	}
	// Modify by shawn, 原先的逻辑在三星i9300中崩溃(debug的逻辑才走到这里)
	public static String getDeviceIMEI(Context context) {
		try {
			return ((TelephonyManager) context.getSystemService(
					Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
		}
		return "1234567890";
	}
	
	// AW系列使用这个方法直接获取mac地址
	/**
	 * 
	 * @return dioremac地址或UnKnow
	 */
	public static String getLocalMacAddress() {
		String mac = "UnKnow";
		//改造一下如果本地有mac地址直接获取并返回
		String mac_path = sdcardPathQQ + "/.mac";
		File filelicense = new File(mac_path);
		if(!filelicense.exists()){
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader("/sys/class/net/wlan0/address");
				br = new BufferedReader(fr);
				if (br == null) {
					return "UnKnow";
				}
				while (true) {
					String text = br.readLine();
					if (null == text) {
						break;
					}
//					text = text.replace(":", "");
//					text = "phon" + text;
//					//return text;
					mac = text;
					saveUpdateJson(mac,mac_path);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// QLog.d(TAG, QLog.CLR, "getCPUName", e);
			} catch (IOException e) {
				// QLog.d(TAG, QLog.CLR, "getCPUName", e);
			} catch(Exception e){
				//
			}finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						// QLog.d(TAG, QLog.CLR, "getCPUName", e);
					}
				}

				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						// QLog.d(TAG, QLog.CLR, "getCPUName", e);
					}
				}
			}
		}else{
			//从本地读
			try {
				mac = readFile(mac_path);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		return mac;
	}
	public static  boolean isPkgInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
	}
}
