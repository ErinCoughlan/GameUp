package com.gameupapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DisplayGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_game);
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.GAME_ID);
	}
}
