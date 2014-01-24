package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.gameupapp.GameFragment.OnGameClicked;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements OnGameClicked {

	// Intent information ids
	static String GAME_ID = "game_id";
	static String USER = "user";

	// General info about user and app
	private String USER_ID = null;
	private GameUpInterface gameup;

	// Response from activites
	// TODO: Find a better way or move to a different file
	private final int detailId = 0;
	private final int createId = 1;
	private final int loginId = 2;


	private List<Game> gameList = new ArrayList<Game>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		displayGames();

		// Set up on click for creating new games
		final Button button = (Button) findViewById(R.id.new_game);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCreateClicked();
			}
		});

		// Set up on click for logging in
		final Button loginButton = (Button) findViewById(R.id.login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onLoginClicked();
			}
		});
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
			fragment.update(gameList);
		}
	}

	@Override
	public void onGameClicked(Game gameClicked, int position) {
		Game game = gameList.get(position);
		String gameId = game.getGameId();

		// Go to a new activity for the specific game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, DisplayGameActivity.class);
		intent.putExtra(GAME_ID, gameId);
		intent.putExtra(USER, USER_ID);
		startActivityForResult(intent, detailId);
	}

	public void onCreateClicked() {
		// Go to a new activity for the specific game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, CreateGameActivity.class);
		intent.putExtra(USER, USER_ID);
		startActivityForResult(intent, createId);
	}

	public void onLoginClicked() {
		// Go to a new activity for the specific game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		intent.putExtra(USER, USER_ID);
		startActivityForResult(intent, loginId);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case detailId:
				if (gameup != null) {
					startGameUp();
				}
				break;
			case createId:
				break;
			case loginId:
				USER_ID = data.getStringExtra("userId");
				Log.d("facebook", "returned from login: " + USER_ID);
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("Welcome!")
				.setMessage("Join or create a game " + USER_ID)
				.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}

		// Save the user_id and similar shared variables
		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user_id", USER_ID);
		editor.apply();
	}

}
