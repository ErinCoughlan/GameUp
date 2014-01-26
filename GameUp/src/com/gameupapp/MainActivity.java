package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.gameupapp.GameFragment.OnGameClicked;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements OnGameClicked {
	// General info about user and app
	private String USER_ID = null;
	private boolean loggedIn = false;
	private GameUpInterface gameup;

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

		startGameUp();
		updateScreen();
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
				updateScreen();
				break;
			case AppConstant.CREATE_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				updateScreen();
				break;
			case AppConstant.LOGIN_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				updateScreen();
				
				
				if (loggedIn) {
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
				} else if (!loggedIn) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle("Goodbye!")
						.setMessage("Thanks for playing " + USER_ID)
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
	}
	
	private void updateScreen() {
		// Simplify interactions if we don't have a registered user
		final Button loginButton = (Button) findViewById(R.id.login);
		if (loggedIn) {
			loginButton.setText(R.string.logout);
		} else {
			loginButton.setText(R.string.sign_up);
		}
	}

}
