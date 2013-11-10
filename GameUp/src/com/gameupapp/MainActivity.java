package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.gameupapp.GameFragment.OnGameClicked;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements OnGameClicked {

	// General info about user and app
	private String USER_ID = "Erin";
	private GameUpInterface gameup;
	
	private List<Game> gameList = new ArrayList<Game>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		displayGames();
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// Restore preferences
		SharedPreferences settings = getSharedPreferences("settings", 0);
		USER_ID = settings.getString("user_id", null);

		// Simplify interactions if we don't have a registered user
		if (USER_ID == null) {
			// TODO Determine what needs to be shown with and without a user
		}
		
		startGameUp();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void startGameUp() {
		gameup = GameUpInterface.getInstance(USER_ID);
		gameup.registerObserver(this);

		gameList = gameup.getGames();
		displayGames();
	}
	
	
	public void displayGames() {
		GameFragment fragment = (GameFragment) getFragmentManager()
				.findFragmentById(R.id.games);
		if (fragment != null) {
			Log.d("fragment", "not null, " + Integer.toString(gameList.size()));
			fragment.update(gameList);
		}
	}

	@Override
	public void onGameClicked(Game gameClicked, int position) {
		// TODO Go to a new activity for the specific game
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}

		// Save the current location of the slider
		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user_id", USER_ID);
		editor.apply();

	}

}
