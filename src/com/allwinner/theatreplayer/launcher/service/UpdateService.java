package com.allwinner.theatreplayer.launcher.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.allwinner.theatreplayer.launcher.util.Utils;

public class UpdateService extends Service {

	private static UpdateService instance = null;
	static String TAG = "jim";
	public static final String APKONCHECKED = "com.launcher.service.APKONCHECKED";
	public static final String APKDOWNSUC = "com.launcher.service.APKDOWNSUC";


	public static UpdateService getInstance() {
		return instance;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		instance = this;

		// 创建文件夹
		File fileRoot = new File(Utils.sdcardPathQQ);
		if (!fileRoot.exists()) {
			fileRoot.mkdirs();
		}
		// 检测是否有新的apk
		new Thread(downloadRunlicense).start();
	}

	/**
	 * 检测
	 */
	Runnable downloadRunlicense = new Runnable() {

		@Override
		public void run() {
			sendPost(com.iotqcloud.tools.Utils.SERVER_URL_USE_DOMAIN);
		}
	};

	private void sendPost(String server_url) {
//		Log.v(TAG, "send post to server");
		HttpPost post = new HttpPost(server_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("guid","646212C46B774f95BBBE2F9CCFF32797"));
		params.add(new BasicNameValuePair("service_type", "APK_UPDATE"));
		params.add(new BasicNameValuePair("para_serial", Utils.rcId));

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParameters, Utils.CHECK_TIMEOUT);
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity, "UTF-8");
				Utils.saveUpdateJson(data, Utils.sdcardPathQQ + "/update.json");
				JSONObject jsonobj = new JSONObject(data);
				int version = jsonobj.getInt("version");				
//				Log.i(TAG, "服务器的md5是 = "+jsonobj.getString("md5"));
//				Log.i(TAG, "version = "+version);
//				Log.i(TAG, "local  = "+Utils.getVersionCode(this));			
//				Log.i(TAG, "url  = "+jsonobj.getString("url"));			
				if (version > Utils.getVersionCode(this)) {
					// 去下载
					Intent intent = new Intent();
					intent.setAction(APKONCHECKED);
					sendBroadcast(intent);
				} 
			} else {
				Log.i(TAG, "1: response status:  "
						+ response.getStatusLine().getStatusCode());

			}
		} catch (Exception e) {
			Log.i(TAG, "Exception ERROR_UNKNOWN" + e.getMessage());
		}
	}
}
