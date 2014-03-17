package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.gameupapp.FilterAbilityFragment.FilterAbilityDialogListener;
import com.gameupapp.FilterDistanceFragment.FilterDistanceDialogListener;
import com.gameupapp.FilterSportFragment.FilterSportDialogListener;
import com.gameupapp.GameFragment.OnGameClicked;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.parse.Parse;
import com.parse.ParseObject;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


// TODO Play Services check per https://developer.android.com/training/location/retrieve-current.html
public class MainActivity extends Activity implements OnGameClicked, FilterSportDialogListener,
		FilterAbilityDialogListener, FilterDistanceDialogListener {
	
	// General info about user and app
	private String USERNAME;
	private GameUpInterface gameup;
	private List<GameParse> gameList = new ArrayList<GameParse>();
	private FilterBuilder filterBuilder = new FilterBuilder();
	
	// Maps info
	private LocationClient mLocationClient;
	private Location mCurrentLocation;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Parse information
		// Register GameParse subclass
		ParseObject.registerSubclass(GameParse.class);
		ParseObject.registerSubclass(Sport.class);
		// erin@gameupapp.com Parse
	    Parse.initialize(this, "yYt3t3sH7XMU81BXgvYaXnWEsoahXCJb5dhupvP5",
	    		"dZCnn1DrZJMXyZOkZ7pbM7Z0ePwTyIJsZzgY77FU");
		// Phil's Parse
		// Parse.initialize(this, "a0k4KhDMvl3Mz2CUDcDMLAgnt5uaCLuIBxK41NGa",
		//		"3EJKdG7SuoK89gkFkN1rcDNbFvIgN71iH0mJyfDC");

		ParseFacebookUtils.initialize(getString(R.string.fb_app_id));
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("settings", 0);
		USERNAME = settings.getString(AppConstant.USER, null);

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
		
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        try {
            if (status != ConnectionResult.SUCCESS) {
            	if (!gameup.SEEN_MAPS_ALERT) {
            		/*
	            	GooglePlayServicesUtil.getErrorDialog(status, this,
	            			AppConstant.RQS_GooglePlayServices).show();
	            	gameup.SEEN_MAPS_ALERT = true;
	            	*/
            	}
            	gameup.CAN_CONNECT = false;
            } else {
            	gameup.CAN_CONNECT = true;
            }
  
        } catch (Exception e) {
        	gameup.CAN_CONNECT = false;
            Log.e("Error: GooglePlayServiceUtil: ", "" + e);
        }

		new SetGameList().execute();
		updateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		MenuItem settings = (MenuItem) menu.findItem(R.id.menu_settings);
		MenuItem logout = (MenuItem) menu.findItem(R.id.menu_logout);
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null) {
			settings.setVisible(false);
			logout.setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_logout:
	        	ParseUser.logOut();
	        	updateView();
	            return true;
	        case R.id.menu_settings:
	        	onSettingsClicked();
	        	return true;
	        case R.id.menu_refresh:
	        	refreshGames();
	        	return true;
	        case R.id.menu_filter:
	        	// Nothing here -- all actions are on children
	        	return true;
	        case R.id.menu_filter_sport:
	        	filter(AppConstant.FILTER_SPORT);
	        	return true;
	        case R.id.menu_filter_distance:
	        	filter(AppConstant.FILTER_DISTANCE);
	        	return true;
	        case R.id.menu_filter_time:
	        	filter(AppConstant.FILTER_TIME);
	        	return true;
	        case R.id.menu_filter_ability:
	        	filter(AppConstant.FILTER_ABILITY);
	        	return true;
	        case R.id.menu_filter_clear:
	        	filter(AppConstant.FILTER_CLEAR);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
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
		editor.putString(AppConstant.USER, USERNAME);
		editor.apply();
	}

	private class SetGameList extends AsyncTask<Void, Integer, Void> {
		List<GameParse> gs;
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d("DisplayGame", "Setting gameList async");
			gs = gameup.getFutureGames();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer...progress) {
			// TODO set progress percent here
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(gs != null) {
				gameList = gs;
				Log.d("MainActivity", "Set gameList async");
				displayGames();
			} else {
				Log.d("MainActivity", "Failed to get gameList");
			}
		}
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
		
		String gameId = game.getGameId();
		// Go to a new activity for the specific game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, DisplayGameActivity.class);
		intent.putExtra(AppConstant.GAME, gameId);
		startActivityForResult(intent, AppConstant.DETAIL_ID);
	}

	public void onCreateClicked() {
		// Go to a new activity for creating game
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, CreateGameActivity.class);
		startActivityForResult(intent, AppConstant.CREATE_ID);
	}

	public void onLoginClicked() {
		// Go to a new activity for logging in and out
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, AppConstant.LOGIN_ID);
	}
	
	public void onSettingsClicked() {
		// Go to a new activity for settings
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		startActivityForResult(intent, AppConstant.SETTINGS_ID);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case AppConstant.DETAIL_ID:
				updateView();
				refreshGames();
				break;
			case AppConstant.SETTINGS_ID:
				updateView();
				break;
			case AppConstant.CREATE_ID:
				updateView();
				refreshGames();
				break;
			case AppConstant.LOGIN_ID:
				updateView();
				refreshGames();
				ParseUser user = ParseUser.getCurrentUser();
				boolean loggedIn = !(user == null);
				if (user != null) {
					if (user.get("firstname") != null) {
						USERNAME = user.get("firstname").toString();
					}
					Log.d("facebook", "parse name: " + user.get("firstname"));
					Log.d("facebook", "parse email: " + user.getEmail());
				}
				
				if (loggedIn) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle("Welcome!")
						.setMessage("Join or create a game " + USERNAME + ".")
						.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
					});
					builder.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setTitle("Goodbye!")
						.setMessage("Thanks for playing " + USERNAME + ".")
						.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
					});
					// Clear the USERNAME because the user logged out
					USERNAME = null;
					builder.show();
				}
				break;
			}
		}
	}
	
	private void updateView() {
		invalidateOptionsMenu();
		// Simplify interactions if we don't have a registered user
		final Button loginButton = (Button) findViewById(R.id.login);
		ParseUser user = ParseUser.getCurrentUser();
		boolean loggedIn = !(user == null);
		if (loggedIn) {
			loginButton.setVisibility(View.GONE);
		} else {
			loginButton.setVisibility(View.VISIBLE);
		}
	}
	
	private void refreshGames() {
		gameList = filterBuilder.execute();
		displayGames();
	}
	
	private void filter(int filterType) {
		DialogFragment dialogFrag;
		
		switch (filterType) {
			case AppConstant.FILTER_SPORT:
				dialogFrag = new FilterSportFragment();
				dialogFrag.show(getFragmentManager(), "FilterSportFragment");
				break;
			case AppConstant.FILTER_DISTANCE:
				dialogFrag = new FilterDistanceFragment();
				dialogFrag.show(getFragmentManager(), "FilterDistanceFragment");
				break;
			case AppConstant.FILTER_TIME:
				break;
			case AppConstant.FILTER_ABILITY:
				dialogFrag = new FilterAbilityFragment();
				dialogFrag.show(getFragmentManager(), "FilterAbilityFragment");
				break;
			case AppConstant.FILTER_CLEAR:
				filterBuilder = new FilterBuilder();
				new SetGameList().execute();
				AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("Filter!")
					.setMessage("Cleared all filters")
					.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
				});
				builder.show();
				break;
		}
	}

	@Override
	public void onDialogPositiveClick(FilterSportFragment dialog) {
		String sport = dialog.getSport();
		if (sport != null) {
			gameList = filterBuilder.setSport(sport).execute();
			displayGames();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle("Filter!")
			.setMessage("Filtered by sport: " + sport)
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
		});
		builder.show();
		
	}

	@Override
	public void onDialogPositiveClick(FilterAbilityFragment dialog) {
		int ability = dialog.getAbilityLevel();
		if (ability != -1) {
			gameList = filterBuilder.setAbilityLevel(ability).execute();
			displayGames();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle("Filter!")
			.setMessage("Filtered by ability: " + AppConstant.ABILITY_LEVELS.get(ability))
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
		});
		builder.show();
		
	}

	@Override
	public void onDialogPositiveClick(FilterDistanceFragment dialog) {
		int distance = dialog.getDistance();
		gameList = filterBuilder.setRadius(distance, 0, 0).execute();
		displayGames();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setTitle("Filter!")
			.setMessage("Filtered by distance: " + Integer.toString(distance) + " miles from me")
			.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
		});
		builder.show();
	}
}
