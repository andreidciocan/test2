package com.example.test2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.*;
import android.view.*;


public class MainActivity extends Activity {

	
	// Server widgets
	private EditText     serverPortEditText       = null;
	private Button       connectButton            = null;
	
	// Client widgets
	private EditText     clientPortEditText       = null;
	private EditText	 clientUrlEditText 		  = null;
	private Button       getPageButton = 		  null;
	private WebView      webView					  = null;
	private EditText     clientAddressEditText	  = null;
	
	private ServerThread serverThread             = null;
	private ClientThread clientThread             = null;
	
	private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
	private class ConnectButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			String serverPort = serverPortEditText.getText().toString();
			if (serverPort == null || serverPort.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Server port should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
			
			serverThread = new ServerThread(Integer.parseInt(serverPort));
			if (serverThread.getServerSocket() != null) {
				serverThread.start();
			} else {
				Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not creat server thread!");
			}
			
		}
	}
	
	private GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
	private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			String clientPort    = clientPortEditText.getText().toString();
			String clientAddress 	 = clientAddressEditText.getText().toString();
			if (clientAddress == null || clientAddress.isEmpty() ||
				clientPort == null || clientPort.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Client connection parameters should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
			
			if (serverThread == null || !serverThread.isAlive()) {
				Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
				return;
			}
			
			String clientUrl = clientUrlEditText.getText().toString();
			if (clientUrl == null || clientUrl.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Parameters from client (city / information type) should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
						
			clientThread = new ClientThread(
					clientAddress,
					Integer.parseInt(clientPort),
					clientUrl,
					webView);
			clientThread.start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		serverPortEditText = (EditText)findViewById(R.id.editText1);
		connectButton = (Button)findViewById(R.id.connect);
		connectButton.setOnClickListener(connectButtonClickListener);
		
		clientAddressEditText = (EditText) findViewById(R.id.editText3);
		clientPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
		getPageButton = (Button)findViewById(R.id.get);
		getPageButton.setOnClickListener(getWeatherForecastButtonClickListener);
		
		clientUrlEditText = (EditText) findViewById(R.id.editText2);
		webView = (WebView) findViewById(R.id.webView1);
/*		
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;

	    try {
	        url = new URL("http://stackoverflow.com/");
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	        String finalWebContent = "";	        
	        
	        
	        while ((line = br.readLine()) != null) {
	            finalWebContent += line;
	        }
	        webView.loadDataWithBaseURL("http://stackoverflow.com/", finalWebContent , "text/html", "UTF-8", null);
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    } */
		
		/*String url = "https://www.wikipedia.org/";
		String finalWebContent = "nimic";
		Document doc = null;
		try {
			doc = Jsoup.connect("https://www.wikipedia.org/").get();
			finalWebContent = doc.body().text();
			//finalWebContent = Jsoup.connect(url).get().html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
		
	}
	
	@Override
	protected void onDestroy() {
		if (serverThread != null) {
			serverThread.stopThread();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

