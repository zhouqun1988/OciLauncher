
package com.allwinner.theatreplayer.launcher.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

import org.json.JSONObject;

import com.allwinner.theatreplayer.launcher.service.UpdateService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class DownloadTask extends ThreadTask {
    private static final String TAG = "jim";

    public static final int ERROR_DOWNLOAD_FAILED = 1;
    
    public static final String FAIL_ACTION="com.ococci.update.ACTION_DOWNLOAD_FAIL";
    public static final String SUCCEED_ACTION="com.ococci.update.ACTION_DOWNLOAD_SUCCEED";

    private String mUrl;

    private String mTargetFile;

    private long mFileSize;

    private long mPosition = 0;

    private Handler mHandler;

    private Context mContext;

    private Preferences mPrefs;

    public boolean mDestoryDownloadThread = false;

    private long mCountSize = 0;

    private RandomAccessFile mRandomAccessFile;

    public DownloadTask(Context context, Handler handler) {
        mHandler = handler;
        mContext = context;
//        mPrefs = new Preferences(context);
//        mUrl = mPrefs.getDownloadURL();
//        mTargetFile = mPrefs.getDownloadTarget();
//        mPosition = mPrefs.getDownloadPos();
//        mFileSize = mPrefs.getDownloadSize();
        if (mPosition == 0 || mFileSize == 0/* || !checkCompleted()*/) {
            resetBreakpoint();
            mRunningStatus = RUNNING_STATUS_UNSTART;
        } else {
            mRunningStatus = RUNNING_STATUS_PAUSE;
            mProgress = mFileSize != 0 ? (int) (mPosition * 100 / mFileSize) : 0;
        }
    }

    public DownloadTask(Context context, Handler handler, String url, String targetFile) {
        this(context, handler);
        mUrl = url;
        mTargetFile = targetFile;
    }

    protected void onRunning() {
//    	Log.i(TAG, " DownloadTask 子类开始");
    	
 /*   	<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
    	<map>
    	    <string name="package_descriptor">aaaaaaaaaaaaaaaaaaaaaaaaa</string>
    	    <long name="download_position" value="0" />
    	    <string name="package_md5">E77C578AD612948D0AF50B19724BEEE1</string>
    	    <string name="download_target">/sdcard/release.apk</string>
    	    <string name="download_URL">http://iotqcloud.com:8989/manager/app/test/R16_800/xiaoqi.apk</string>
    	    <long name="download_size" value="0" />
    	</map>
*/
    	File file = new File(Utils.DOWNLOAD_PATH);
		if (file.exists()) {
//			Log.i(TAG, "本地存在 开始删除");
			file.delete();
		}
		// 去下载
		String jsondatalocal;
		String mUrl = "";
		String md5 = "";
		try {
			jsondatalocal = Utils.readFile(Utils.sdcardPathQQ
					+ "/update.json");

			// 获取URL
			JSONObject jsonobj = new JSONObject(jsondatalocal);
			String net_url = jsonobj.getString("url");
//			Log.i(TAG, "url = " + jsonobj.getString("url"));
			md5 = jsonobj.getString("md5");
			mUrl = net_url;
		} catch (Throwable e) {
			e.printStackTrace();
		}
    	
    	//本地获取url
        //mUrl = mPrefs.getDownloadURL();
        //mTargetFile = mPrefs.getDownloadTarget();
		mTargetFile = Utils.DOWNLOAD_PATH;
        try {
        	//开始下载app
            URL url = new URL(mUrl);
            mRandomAccessFile = new RandomAccessFile(mTargetFile, "rw");
           
            byte[] buf = new byte[1024 * 8];
            HttpURLConnection cn = (HttpURLConnection) url.openConnection();
            mFileSize = cn.getContentLength(); 
            if (mFileSize < 0) {
                Log.e(TAG, "Download file " + mTargetFile + " from " + mUrl + " is failure");
                mErrorCode = ERROR_DOWNLOAD_FAILED;
                resetBreakpoint();
                throw new IOException("Something is wrong with network!");
            }
            
            
            mRandomAccessFile.setLength(mFileSize);
            mRandomAccessFile.seek(mPosition);
            cn = (HttpURLConnection) url.openConnection();
            cn.setRequestProperty("jim", "bytes=" + mPosition + "-" + mFileSize);
            mCountSize = mPosition;
            BufferedInputStream bis = new BufferedInputStream(cn.getInputStream());
            int len;
            while ((len = bis.read(buf)) > 0) {
                while (mRunningStatus == RUNNING_STATUS_PAUSE||mRunningStatus==RUNNING_STATUS_UNSTART) {
                    try {
                        if (mRunningStatus==RUNNING_STATUS_UNSTART) {
                        	resetBreakpoint();
                            return;
                        }
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (mRandomAccessFile) {
                    mRandomAccessFile.write(buf, 0, len);
                    mCountSize += len;
                    mProgress = (int) (mCountSize * 100 / mFileSize);
                    if (mFileSize == mCountSize) {
                        mProgress = 100;
                    }
                    mPosition = mCountSize;
//                    mPrefs.setBreakpoint(mFileSize, mPosition);
                }
            }
            
//            String md5 = mPrefs.getMd5();
            if (!MD5.checkMd5(md5, mTargetFile)) {
                mErrorCode = ERROR_DOWNLOAD_FAILED;
//                noticeUI(R.string.Download_failed,FAIL_ACTION);
            }else{
                mErrorCode = NO_ERROR; 
                noticeUI(1,SUCCEED_ACTION);
            }    
            resetBreakpoint();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Download is  failure\n" + e.toString());
            mErrorCode = ERROR_DOWNLOAD_FAILED;
            resetBreakpoint();
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "URL is not exist\n" + e.toString());
            mErrorCode = ERROR_DOWNLOAD_FAILED;
            resetBreakpoint();
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IO is exception\n" + e.toString());
            mErrorCode = ERROR_DOWNLOAD_FAILED;
            // resetBreakpoint();
            e.printStackTrace();
        }
    }

    protected void onStart() {

    }

    @Override
    protected void onStop() {
    	super.onStop();
    	Log.v(TAG,"下载===当前状态为====DownloadTask="+mRunningStatus);
    }
    
    private boolean checkCompleted() {
        File file = new File(mTargetFile);
        if (file.exists() && file.length() == mFileSize) {
            return true;
        }
        return false;
    }

    public void resetBreakpoint() {
        mPosition = 0;
        mFileSize = 0;
//        mPrefs.setBreakpoint(0, 0);
    }
    private void noticeUI(int RString,String action){
        Log.v(TAG, "Donwload finish");
    	Intent intent = new Intent();
		intent.setAction(UpdateService.APKDOWNSUC);
		mContext.sendBroadcast(intent);
    }
}
