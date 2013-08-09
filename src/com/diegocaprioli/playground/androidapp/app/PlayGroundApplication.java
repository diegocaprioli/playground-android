package com.diegocaprioli.playground.androidapp.app;

import com.diegocaprioli.playground.androidapp.controller.PlayGroundAPIController;

import android.app.Application;

public class PlayGroundApplication extends Application {

	public PlayGroundAPIController apiController;
	
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		//TODO: to be used to initialize anything that the app might need in the future.
		
		apiController = new PlayGroundAPIController(getAssets());
		
		
	}

	
	
}
