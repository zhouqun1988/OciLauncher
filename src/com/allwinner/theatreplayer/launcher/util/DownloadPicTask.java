package com.allwinner.theatreplayer.launcher.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadPicTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = "DownloadPicTask";
	private static final int MAX_RETYR_TIMES = 3;
	private static final int BUFSIZE = 64 * 1024;
	private Context mContext;
	private String mStrTag;
	private String mPath;
	private Handler mCallback;
	private boolean finished = false;
	private boolean paused = false;

	public DownloadPicTask(Context context, String strTag, String path,
			Handler handler) {
		mContext = context;
		mStrTag = strTag;
		mPath = path;
		mCallback = handler;
	}

	public String getTag(){
		return mStrTag;
	}
	
	public String getPath(){
		return mPath;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		Boolean bSuccess = false;
		URL url = null;
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		int curTrytime = 0;
		int totalLen = 0;
		int read = 0;
		int curSize = 0;
		int fileoffset = 0;
		byte[] buf = new byte[BUFSIZE];
		try {
			outputStream = mContext.openFileOutput(mStrTag,
					Context.MODE_PRIVATE);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (curTrytime < MAX_RETYR_TIMES && !bSuccess) {
			curTrytime++;
			try {
				while (paused) {
					Thread.sleep(500);
				}
				url = new URL(mPath);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setConnectTimeout(20 * 1000);
				httpURLConnection.setRequestProperty("RANGE", "bytes="
						+ fileoffset + "-");
				if (fileoffset == 0) {
					totalLen = httpURLConnection.getContentLength();
				}
				inputStream = httpURLConnection.getInputStream();

				while (!finished) {

					if (BUFSIZE > curSize) {
						read = inputStream
								.read(buf, curSize, BUFSIZE - curSize);
						if (read > 0) {
							curSize += read;
							fileoffset += read;
						} else if (read == -1 && totalLen == fileoffset) {
							bSuccess = true;
							finished = true;
							outputStream.write(buf, 0, curSize);
						}

					} else {
						outputStream.write(buf, 0, curSize);
						curSize = 0;
					}

				}
				inputStream.close();
				httpURLConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				finished = false;
				if (inputStream != null) {
					try {
						inputStream.close();

						if (httpURLConnection != null) {
							httpURLConnection.disconnect();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bSuccess;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (mCallback != null) {
			Message msg = new Message();
			msg.what = 1;
			msg.arg1 = Integer.valueOf(mStrTag).intValue();
			msg.obj = result;
			mCallback.sendMessage(msg);
		}
		if (!result) {
			Log.e(TAG, "downloadAsyncTask fail! mStrTag=" + mStrTag);
		}
	}

	public boolean isPaused() {
		return paused;
	}

	/**
	 * pause download
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * continue download
	 */
	public void continued() {
		paused = false;
	}

	/**
	 * stop download
	 */
	@Override
	public void onCancelled() {
		finished = false;
		super.onCancelled();
	}

}