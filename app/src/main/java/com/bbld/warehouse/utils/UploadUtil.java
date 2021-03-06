package com.bbld.warehouse.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UploadUtil {
	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 *
	 * @param url Service net address
	 * @param params text content
	 * @param files pictures
	 * @return String result of Service response
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String post(String url, Map<String, String> params, Map<String, File> files) throws JSONException, IOException{
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(90 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name="+file.getValue().getName()+"; filename=\""
						+ file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		int res = conn.getResponseCode();
		Log.i("res", "resCode="+res);

		//	    InputStream in = conn.getInputStream();
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sbb = new StringBuilder();
		String line = "";
		while((line = reader.readLine()) != null){
			sbb.append(line);
		}
		String jsonText = sbb.toString();
		//7. 解析
		//jsonText :  {result:ok}  {result:error, msg:xxx}
		JSONObject obj = new JSONObject(jsonText);
		Log.i("obj", "obj="+obj);
		String ress=obj.getString("mes");
		Log.i("ress", "ress="+ress);

		return ress+""/*sb2.toString()*/;

	}
}
