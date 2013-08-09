package com.diegocaprioli.playground.androidapp.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.diegocaprioli.playground.androidapp.R;
import com.diegocaprioli.playground.androidapp.app.PlayGroundApplication;
import com.diegocaprioli.playground.androidapp.controller.PlayGroundAPIController;

public class MainActivity extends Activity {

	
	/**
	 * Returns the app's PlayGroundAPIController
	 * @return PlayGroundAPIController
	 */
	private PlayGroundAPIController getApiController() {
		PlayGroundApplication app = (PlayGroundApplication) getApplication();
		return app.apiController;
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //get the users from the API
        getApiController().refreshUsers();     
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
