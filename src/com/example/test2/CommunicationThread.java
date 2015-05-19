package com.example.test2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class CommunicationThread extends Thread {
	
	private ServerThread serverThread;
	private Socket       socket;
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
		this.serverThread = serverThread;
		this.socket       = socket;
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = Utilities.getReader(socket);
				PrintWriter    printWriter    = Utilities.getWriter(socket);
				if (bufferedReader != null && printWriter != null) {
					Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (url)!");

					String url            = bufferedReader.readLine();
					HashMap<String, String> data = serverThread.getData();
					
					String webContent = null;
					if (url != null && !url.isEmpty()) {
						if (data.containsKey(url)) {
							Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
							webContent = data.get(url);
						} else {
							Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the web...");
							
							HttpClient client = new DefaultHttpClient();
							HttpGet get = new HttpGet(url);
							ResponseHandler<String> handler = new BasicResponseHandler();
							
							try {
							    webContent = client.execute(get, handler);
							} catch (Exception e) {
							    // TODO Auto-generated catch block
							    Log.i(Constants.TAG, e.getMessage());
							}
							
							/*
							  URL nowUrl;
						      HttpURLConnection conn;
						      BufferedReader rd;
						      String line;
						      String result = "";
						      try {
						         nowUrl = new URL(url);
						         conn = (HttpURLConnection) nowUrl.openConnection();
						         conn.setRequestMethod("GET");
						         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						         while ((line = rd.readLine()) != null) {
						            result += line;
						         }
						         rd.close();
						      } catch (IOException e) {
						         e.printStackTrace();
						      } catch (Exception e) {
						         e.printStackTrace();
						      }
						      webContent = result;					
							/*
							HttpClient httpClient = new DefaultHttpClient();
							HttpGet httpGet = new HttpGet(url);
							
							ResponseHandler<String> responseHandler = new BasicResponseHandler();
							String pageSourceCode = httpClient.execute(httpGet, responseHandler);
							
							if (pageSourceCode != null) {
								webContent = pageSourceCode;
							}
							*/
						} 
						
						if (webContent != null) {
							serverThread.setData(url, webContent);
							printWriter.println(webContent);
							printWriter.flush();
						} else {
							Log.e(Constants.TAG, "[COMMUNICATION THREAD] Web content is null!");
						}
						
					} else {
						Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
					}
				} else {
					Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
				}
				socket.close();
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
				if (Constants.DEBUG) {
					ioException.printStackTrace();
				}				
			}
		} else {
			Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
		}
	}

}
