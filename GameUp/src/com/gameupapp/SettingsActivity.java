package com.gameupapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	
	private GameUpInterface gameup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// GameUp instance
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);
	}
	
	@Override
	public void onBackPressed() {
		Intent result = new Intent();
		setResult(Activity.RESULT_OK, result);
		finish();
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}
	}

}
