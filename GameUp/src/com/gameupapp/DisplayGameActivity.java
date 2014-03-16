package com.gameupapp;

import java.text.DecimalFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayGameActivity extends Activity implements 
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GameUpInterface gameup;
	private String GAME_ID;
	private GameParse GAME_PARSE;
	
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_game);
		
		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Get the game id so we can retrieve info
		Intent intent = getIntent();
		GAME_ID = intent.getStringExtra(AppConstant.GAME);
		
		// GameUp instance
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);
		
		// Maps
		if (!gameup.CAN_CONNECT) {
			LinearLayout mapView = (LinearLayout) findViewById(R.id.gameMapView);
        	mapView.setVisibility(View.INVISIBLE);
		} else {
			// Create a client for location in maps
    		mLocationClient = new LocationClient(this, this, this);
    		FragmentManager fm = getFragmentManager();
            MapFragment mf = (MapFragment) fm.findFragmentById(R.id.gameMap);
            map = mf.getMap();
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            
            getCurrentLocation();
		}
		
		// Set info based on game
		String[] params = {GAME_ID};
		new SetGame().execute(params);
	}
	
	private class SetGame extends AsyncTask<String, Integer, Void> {
		GameParse g;
		
		@Override
		protected Void doInBackground(String... gameID) {
			Log.d("DisplayGame", gameID[0]);
			g = gameup.getGame(gameID[0]);
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer...progress) {
			// TODO set progress percent here
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if(g != null) {
				// Allow us to use the game within the class
				GAME_PARSE = g;
				setGameInfo(g);
				updateView();
				Log.d("DisplayGame", "Set game async");
			} else {
				Log.d("DisplayGame", "Failed to get game by id");
			}
		}
	}
	
	private void setGameInfo(GameParse g) {
		TextView timestamp = (TextView) this.findViewById(R.id.gameTimestamp);
		TextView location = (TextView) this.findViewById(R.id.gameLocation);
		TextView players = (TextView) this.findViewById(R.id.gamePlayers);
		TextView sport = (TextView) this.findViewById(R.id.gameSport);
		TextView abilityLevel = (TextView) this.findViewById(R.id.gameAbilityLevel);
		TextView distLocation = (TextView) this.findViewById(R.id.gameDistance);
		
		// check to see if each individual textview is null.
		// if not, assign some text
		if (timestamp != null) {
			String date = HelperFunction.convertToDate(g.getStartDateTime(), g.getEndDateTime());
			timestamp.setText(date);
		}
		
		if (location != null) {
			String locationString = g.getReadableLocation();
			location.setText(locationString);
		}
		
		if (distLocation != null){
			//String locationString = HelperFunction.convertParseGeoToString(i.getLocation());
			//location.setText(locationString);
			
			if (gameup.CAN_CONNECT) {
				double distance = 
						gameup.getDistanceBetweenLocationAndGame(mCurrentLocation.getLatitude(), 
								mCurrentLocation.getLongitude(), g.getGameId());
				
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(1);
				
				distLocation.setText(df.format(distance) + " mi. away");
			} else {
				distLocation.setVisibility(View.INVISIBLE);
			}
		}
		
		if (sport != null) {
			sport.setText(g.getSport());
		}
		
		int joined = g.getCurrentPlayerCount();
		Log.d("gameSpecificView", "joined: " + joined);
		int maxPlayers = g.getMaxPlayerCount();
		if (players != null) {
			String str = joined + " out of " + maxPlayers;
			players.setText(str);
		}
		
		if (abilityLevel != null) {
			int level = g.getAbilityLevel();
			String str = AppConstant.ABILITY_LEVELS.get(level);
			abilityLevel.setText(str);
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent result = new Intent();
		setResult(Activity.RESULT_OK, result);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (gameup.CAN_CONNECT) {
			// Disconnecting maps
	        mLocationClient.disconnect();
		}

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}
	}
	
	private void updateView() {
		// Set up the join/unjoin button
		final Button button = (Button) findViewById(R.id.joinButton);
		ParseUser user = ParseUser.getCurrentUser();
		final boolean loggedIn = (user != null);
		if (loggedIn) {
			boolean alreadyJoined = gameup.checkPlayerJoined(GAME_PARSE);
			if (alreadyJoined) {
				button.setText(R.string.unjoin);
				button.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	boolean success = gameup.postUnjoinGame(GAME_PARSE);
		            	if (success) {
			            	AlertDialog dialog = HelperFunction.createGameAlert(
			            			R.string.alert_success_unjoin, true, DisplayGameActivity.this, loggedIn);
			            	dialog.show();
		            	} else {
		            		AlertDialog dialog = HelperFunction.createGameAlert(
			            			R.string.alert_fail_unjoin, false, DisplayGameActivity.this, loggedIn);
		            		dialog.show();
		            	}
		            }
		        });
			} else {
				button.setText(R.string.join);
		        button.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	boolean success = gameup.postJoinGame(GAME_PARSE);
		            	if (success) {
		            		AlertDialog dialog = HelperFunction.createGameAlert(
			            			R.string.alert_success_join, true, DisplayGameActivity.this, loggedIn);
			            	dialog.show();
		            	} else {
		            		AlertDialog dialog = HelperFunction.createGameAlert(
		            				R.string.alert_fail_join, false, DisplayGameActivity.this, loggedIn);
		            		dialog.show();
		            	}
		            }
		        });
			}
		} else {
			button.setText(R.string.sign_up);
			button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	// Go to a new activity for logging in and out
	        		Intent intent = new Intent();
	        		intent.setClass(DisplayGameActivity.this, LoginActivity.class);
	        		startActivityForResult(intent, AppConstant.LOGIN_ID);
	            }
	        });
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case AppConstant.LOGIN_ID:
				updateView();
			}
		}
	}
    
    public void getCurrentLocation() {
    	mCurrentLocation = mLocationClient.getLastLocation();

        //map.setMyLocationEnabled(true);
        //Location location = map.getMyLocation();

        if (mCurrentLocation != null) {
            LatLng myLocation = new LatLng(mCurrentLocation.getLatitude(),
            		mCurrentLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, AppConstant.MAP_ZOOM));
    		map.addMarker(new MarkerOptions()
                    .title("Current Location")
                    .position(myLocation));
        }
    }
    
    @Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		gameup.CAN_CONNECT = true;
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
