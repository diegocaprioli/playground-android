package com.diegocaprioli.playground.androidapp.exception;

import android.util.Log;

public class PlayGroundException extends Exception {

	
	public static void LogException(Exception e, String tag, String message) {		
		Log.e(tag, message + e.getMessage());
		e.printStackTrace();		
	}
	
	
}
