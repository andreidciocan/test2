package com.example.test2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class ClientThread extends Thread {
	
	private String   address;
	private int      port;
	private String   url;
	WebView webView;
	
	private Socket   socket;
	
	public ClientThread(
			String address,
			int port,
			String clientUrl,
			WebView webView) {
	
		this.address                 = address;
		this.port                    = port;
		this.url                    = clientUrl;
		this.webView         = webView;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			if (socket == null) {
				Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
			}
			
			BufferedReader bufferedReader = Utilities.getReader(socket);
			PrintWriter    printWriter    = Utilities.getWriter(socket);
			if (bufferedReader != null && printWriter != null) {
				printWriter.println(url);
				printWriter.flush();
				String lineContent;
				String webContent = "";
				
				while ((lineContent = bufferedReader.readLine()) != null) {
					webContent += lineContent;
				}
				final String finalWebContent = webContent;
				
				webView.post(new Runnable() {
					@Override
					public void run() {
						webView.loadDataWithBaseURL(url, finalWebContent, "text/html", "UTF-8", null);
					}
				});
			} else {
				Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
			}
			socket.close();
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		}
	}

}