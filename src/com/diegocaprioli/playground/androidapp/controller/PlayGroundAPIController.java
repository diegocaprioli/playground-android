package com.diegocaprioli.playground.androidapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Singleton class that manages the communication between the android app and the 
 * PlayGround API
 * 
 * @author diego
 * 
 */
public class PlayGroundAPIController {
	
	private static final String TAG = "PlayGroundAPIController"; 
	
	private static PlayGroundAPIController instance;
	
	public static PlayGroundAPIController getInstance() {
		if (instance == null) {
			instance = new PlayGroundAPIController();
		}
		return instance;
	}
	
	
	private List<JSONObject> mUsers;
	
	
	private PlayGroundAPIController() {
		mUsers = new ArrayList<JSONObject>();
	}
	
	
	
	/**
	 * Retrieves the list of users from the PlayGround API and saves them in
	 * the users property.
	 */
	public void refreshUsers() {
		
		//TODO: check internet available
		
		
		//AsyncTask that does the work
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... args) {
				
				InputStream is = null;
				InputStreamReader isr = null;
				HttpURLConnection urlConn = null;
				
				try {
					
					//form the url to the users service
					
					/* you can access your host machine with the IP address "10.0.2.2". 
					 * This has been designed in this way by the Android team. 
					 * So your webserver can perfectly run at localhost and from 
					 * your Android app you can access it via "http://10.0.2.2:8080". 
					 */
					
					URL url = new URL("http://10.0.2.2:8080/app_dev.php/api/v1/users");						
					
					//create the connection
					urlConn = (HttpURLConnection) url.openConnection();
					urlConn.setReadTimeout(1000);
					urlConn.setConnectTimeout(10000);
					urlConn.setRequestMethod("GET");					
					
					//HTTP Headers
					urlConn.setRequestProperty("Accept", "application/json");
					
					//connect to server
					urlConn.connect();
					
					int statusCode = urlConn.getResponseCode();
					Log.i(TAG, "StatusCode = " + statusCode);
					
					//obtain response message from server
					is = urlConn.getInputStream();
					isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					StringBuffer message = new StringBuffer();
					while ( (line = br.readLine()) != null ) {
						message.append(line);
					}
					
					Log.i(TAG, "Communication with API done!. Message \n:" + message.toString());
									
					//TODO: save the users!!!!!
					
				} catch (Exception e) {
					
					//FIXME: replace this with each particular catch block and handle properly.
					Log.e(TAG, "There was an error in the communication with PlayGround API: " +
							e.getMessage() );
					e.printStackTrace();
					
				} finally {
					
					try {
						isr.close();
						is.close();
					} catch (IOException e) {
						// FIXME: Auto-generated catch block
						Log.e(TAG, "There was an error in the communication with PlayGround API: " +
								e.getMessage() );
						e.printStackTrace();
					}
					
					urlConn.disconnect();
					
				}
				
				return null;
				
			}					
			
		};
		
		task.execute();
		
	}
	
}
