package com.allwinner.theatreplayer.launcher.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpConnectionUtil {

	public static enum HttpMethod {
		GET, POST
	}

	public String syncConnect(final String url,
			final LinkedHashMap<String, String> params, final HttpMethod method) {
		String json = null;
		HttpClient client = null;
		try {
			BasicHttpParams httpParameters;
			int timeoutConnection = 10 * 1000;
			int timeoutSocket = 15 * 1000;
			httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			client = new DefaultHttpClient(httpParameters);
			HttpUriRequest request = getRequest(url, params, method);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				json = EntityUtils.toString(entity, HTTP.UTF_8);

			}

		} catch (ClientProtocolException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("HttpConnectionUtil", e.getMessage(), e);
			json = "timeout";
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return json;
	}

	private HttpUriRequest getRequest(String url,
			LinkedHashMap<String, String> params, HttpMethod method) {
		if (method.equals(HttpMethod.POST)) {
			List<NameValuePair> listParams = new ArrayList<NameValuePair>();
			if (params != null) {
				for (String name : params.keySet()) {
					listParams.add(new BasicNameValuePair(name, params
							.get(name)));
				}
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						listParams, "utf-8");
				HttpPost request = new HttpPost(url);
				request.setEntity(entity);
				return request;
			} catch (UnsupportedEncodingException e) {
				throw new java.lang.RuntimeException(e.getMessage(), e);
			}
		} else {
			if (url.indexOf("?") < 0) {
				url += "?";
			}
			if (params != null) {
				for (String name : params.keySet()) {
					url += "&" + name + "=" + params.get(name);
				}
			}

			Pattern p = Pattern.compile("\n");
			Matcher m = p.matcher(url);
			url = m.replaceAll("");
			HttpGet request = new HttpGet(url);
			return request;
		}
	}

}
