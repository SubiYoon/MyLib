package com.shop.frame;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OCRUtil {

	static String apiURL = "https://czq88r0uii.apigw.ntruss.com/custom/v1/20221/5ca28396b97f897c4ba57a69481fe2b43fe9b1e4b1efafadd8ce70602d212cac/infer";
	static String secretKey = "UEVBSXFva2hXZnJueVJIanVpSVp3cExQT2ljUGpHQkk=";
	//Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static","img").toString();    --->   메서드로 경로값 잡아주면 안됨... 왜??
	//static String imgpath = "/Users/dongsubyoon/multi/html/shop/src/main/resources/static/img/";


	
	public static String getText(String imgname, String imgpath) {
		 String imageFile = imgpath+imgname;
		 StringBuffer response = null;
			try {
				URL url = new URL(apiURL);
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				con.setUseCaches(false);
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setReadTimeout(30000);
				con.setRequestMethod("POST");
				String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", "");
				con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
				con.setRequestProperty("X-OCR-SECRET", secretKey);

				JSONObject json = new JSONObject();
				json.put("version", "V2");
				json.put("requestId", UUID.randomUUID().toString());
				json.put("timestamp", System.currentTimeMillis());
				JSONObject image = new JSONObject();
				image.put("format", "jpg");
				image.put("name", "demo");
				JSONArray images = new JSONArray();
				images.add(image);
				json.put("images", images);
				String postParams = json.toString();

				con.connect();
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				long start = System.currentTimeMillis();
				File file = new File(imageFile);
				System.out.println("----------"+file.getName());
				System.out.println("----------"+file.getPath());
				System.out.println("----------"+file.getAbsolutePath());
				writeMultiPart(wr, postParams, file, boundary);
				wr.close();

				int responseCode = con.getResponseCode();
				BufferedReader br;
				if (responseCode == 200) {
					br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				} else {
					br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				}
				String inputLine;
				response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				//System.out.println("REsult:");
				//System.out.println(response);
		
			} catch (Exception e) {
				System.out.println(e);
			}
		 
			return response.toString();
	}
	
	
	private static void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws
	IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("--").append(boundary).append("\r\n");
		sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n");
		sb.append(jsonMessage);
		sb.append("\r\n");
	
		out.write(sb.toString().getBytes("UTF-8"));
		out.flush();
	
		if (file != null && file.isFile()) {
			out.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
			StringBuilder fileString = new StringBuilder();
			fileString
				.append("Content-Disposition:form-data; name=\"file\"; filename=");
			fileString.append("\"" + file.getName() + "\"\r\n");
			fileString.append("Content-Type: application/octet-stream\r\n\r\n");
			out.write(fileString.toString().getBytes("UTF-8"));
			out.flush();
	
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] buffer = new byte[8192];
				int count;
				while ((count = fis.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}
				out.write("\r\n".getBytes());
			}
	
			out.write(("--" + boundary + "--\r\n").getBytes("UTF-8"));
		}
		out.flush();
	}
	
	
}
