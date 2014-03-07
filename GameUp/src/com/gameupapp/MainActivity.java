package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.gameupapp.GameFragment.OnGameClicked;

import com.parse.Parse;
import com.parse.ParseObject;

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

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends Activity implements OnGameClicked {
	// General info about user and app
	private String USER_ID = null;
	private boolean loggedIn = false;
	private GameUpInterface gameup;

	private List<GameParse> gameList = new ArrayList<GameParse>();
	
	// Facebook stuff
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        Log.d("facebook", "state is opened: " + Boolean.toString(state.isOpened()));
	    }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Parse information
		// Register GameParse subclass
		ParseObject.registerSubclass(GameParse.class);
		// erin@gameupapp.com Parse
		Parse.initialize(this, "yYt3t3sH7XMU81BXgvYaXnWEsoahXCJb5dhupvP5",
				"dZCnn1DrZJMXyZOkZ7pbM7Z0ePwTyIJsZzgY77FU");
		// Phil's Parse
		//Parse.initialize(this, "a0k4KhDMvl3Mz2CUDcDMLAgnt5uaCLuIBxK41NGa",
		//		"3EJKdG7SuoK89gkFkN1rcDNbFvIgN71iH0mJyfDC");

		ParseFacebookUtils.initialize(getString(R.string.fb_app_id));
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("settings", 0);
		loggedIn = settings.getBoolean(AppConstant.LOGIN, false);
		USER_ID = settings.getString(AppConstant.USER, null);
		
		// Logging into Facebook if active previously logged in
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		if (loggedIn) {
			Log.d("facebook", "logged in - restoring session");
			Session session = Session.getActiveSession();
			if (session == null) {
				Log.d("facebook", "session is null");
				if (savedInstanceState != null) {
					session = Session.restoreSession(this, null, callback, savedInstanceState);
				}
				if (session == null) {
					session = new Session(this);
				}
				Session.setActiveSession(session);
				if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
					session.openForRead(new Session.OpenRequest(this).setCallback(callback));
				}
			}
		}

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
		
		startGameUp();
		updateView();
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
	public void onGameClicked(GameParse gameClicked, int position) {
		GameParse game = gameList.get(position);
		
		// TODO I've been using capital ID and just noticed everything else is Id. I need to fix mine.
		String gameId = game.getGameID();

		// Go to a new activity for the specific game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, DisplayGameActivity.class);
		intent.putExtra(AppConstant.GAME, gameId);
		intent.putExtra(AppConstant.USER, USER_ID);
		intent.putExtra(AppConstant.LOGIN, loggedIn);
		startActivityForResult(intent, AppConstant.DETAIL_ID);
	}

	public void onCreateClicked() {
		// Go to a new activity for creating game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, CreateGameActivity.class);
		intent.putExtra(AppConstant.USER, USER_ID);
		intent.putExtra(AppConstant.LOGIN, loggedIn);
		startActivityForResult(intent, AppConstant.CREATE_ID);
	}

	public void onLoginClicked() {
		// Go to a new activity for logging in and out
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		intent.putExtra(AppConstant.USER, USER_ID);
		intent.putExtra(AppConstant.LOGIN, loggedIn);
		startActivityForResult(intent, AppConstant.LOGIN_ID);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case AppConstant.DETAIL_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				updateView();
				break;
			case AppConstant.CREATE_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				updateView();
				break;
			case AppConstant.LOGIN_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				updateView();
				
				ParseUser user = ParseUser.getCurrentUser();
				// Get the user's name using ParseUser
				
				if (loggedIn) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle("Welcome!")
						.setMessage("Join or create a game " + USER_ID + ".")
						.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
					});
					builder.show();
				} else if (!loggedIn) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle("Goodbye!")
						.setMessage("Thanks for playing " + USER_ID + ".")
						.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
					});
					builder.show();
				}
				break;
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
		SharedPreferences settings = getSharedPreferences(AppConstant.SHARED_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(AppConstant.LOGIN, loggedIn);
		editor.putString(AppConstant.USER, USER_ID);
		editor.apply();
	}
	
	private void updateView() {
		// Simplify interactions if we don't have a registered user
		final Button loginButton = (Button) findViewById(R.id.login);
		if (loggedIn) {
			loginButton.setText(R.string.logout);
		} else {
			loginButton.setText(R.string.sign_up);
		}
	}

}
