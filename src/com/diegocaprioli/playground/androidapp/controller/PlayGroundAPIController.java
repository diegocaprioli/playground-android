package com.diegocaprioli.playground.androidapp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.diegocaprioli.playground.androidapp.exception.PlayGroundException;


/**
 * Controller that manages the communication between the android app and the 
 * PlayGround API.
 * The same instance should be used every time.
 * 
 * @author diego
 * 
 */
public class PlayGroundAPIController {
	
	private static final String TAG = "PlayGroundAPIController"; 
	
		
	private AssetManager mAssetsManager;
	private List<JSONObject> mUsers;
	
	
	public PlayGroundAPIController(AssetManager assetsManager) {
		mUsers = new ArrayList<JSONObject>();
		mAssetsManager = assetsManager;
	}
	
	
	/**
	 * Returns the SSLContext to be used to connect to the SSL API of 
	 * PlayGround, using its self signed certificate as trusted
	 */
	private SSLContext getSSLSocketFactory() {
	
		
		// Load CAs from an InputStream
		// (could be from a resource or ByteArrayInputStream or ...)
		CertificateFactory cf;
		InputStream caInput = null;
		Certificate ca;
		try {
			cf = CertificateFactory.getInstance("X.509");
			caInput = mAssetsManager.open("dmc-net.no-ip.org.crt");
			//caInput = mAssetsManager.open("10.0.2.2.crt");
			ca = cf.generateCertificate(caInput);
		    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN() + " altNAmes=" + ((X509Certificate) ca).getSubjectAlternativeNames());
		    
		    
		} catch (Exception e) {
			PlayGroundException.LogException(e, TAG, "There was an error while trying to load the CAs: ");
			return null;
		} finally {
		    try {
		    	if (caInput != null) {
		    		caInput.close();
		    	}
			} catch (IOException e) {
				PlayGroundException.LogException(e, TAG, "There was an error in caInput.close() method call: ");
				return null;
			}
		}

		// Create a KeyStore containing our trusted CAs
		KeyStore keyStore;
		try {
			String keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);
		} catch (Exception e) { 
			PlayGroundException.LogException(e, TAG, "There was an error trying to create the keystore: ");
			return null;
		}
		
		// Create a TrustManager that trusts the CAs in our KeyStore
		TrustManagerFactory tmf;
		try {
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);
		} catch (Exception e) {
			PlayGroundException.LogException(e, TAG, "There was an error trying to create the TrustManager: ");
			return null;
		}
		
		// Create an SSLContext that uses our TrustManager
		SSLContext context = null;
		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);
		} catch (Exception e) {
			PlayGroundException.LogException(e, TAG, "There was an error trying to create the SSLContext: ");
			return null;
		}

		return context;
		
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
				HttpsURLConnection urlConn = null;
				
				try {
					
					//form the url to the users service
					
					/* you can access your host machine with the IP address "10.0.2.2". 
					 * This has been designed in this way by the Android team. 
					 * So your webserver can perfectly run at localhost and from 
					 * your Android app you can access it via "http://10.0.2.2:8080". 
					 */
					
					URL url = new URL("https://dmc-net.no-ip.org:4433/app.php/api/v1/users");
					//URL url = new URL("https://10.0.2.2:4430/app.php/api/v1/users");
					
					//create the connection

					urlConn = (HttpsURLConnection) url.openConnection();
					urlConn.setSSLSocketFactory(getSSLSocketFactory().getSocketFactory());
					urlConn.setReadTimeout(1000);
					urlConn.setConnectTimeout(10000);
					urlConn.setRequestMethod("GET");
					
					//HTTP Headers
					urlConn.setRequestProperty("Accept", "application/json");
					urlConn.setRequestProperty("Authorization", "Basic " + Base64.encodeToString("apiuser:apipass".getBytes(), Base64.NO_WRAP));
					
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
						if (isr != null) {
							isr.close();
						}
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
