package com.gameupapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayGameActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GameUpInterface gameup;
	private String USER_ID;
	private String GAME_ID;
	private boolean loggedIn;
	
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_game);
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("settings", 0);
		loggedIn = settings.getBoolean(AppConstant.LOGIN, false);
		USER_ID = settings.getString(AppConstant.USER, null);
		Log.d("login", "(display create) user: " + USER_ID + " loggedIn = " + loggedIn);
		updateView();
		
		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Create a client for location in maps
		mLocationClient = new LocationClient(this, this, this);
		FragmentManager fm = getFragmentManager();
        MapFragment mf = (MapFragment) fm.findFragmentById(R.id.gameMap);
        map = mf.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// Connect maps and get current location
		mLocationClient.connect();

		// Get the game id so we can retrieve info
		Intent intent = getIntent();
		GAME_ID = intent.getStringExtra(AppConstant.GAME);
		
		// GameUp instance
		gameup = GameUpInterface.getInstance(USER_ID);
		gameup.registerObserver(this);
		
		Game g = gameup.getGame(GAME_ID);
		
		// Set info based on game
		if (g != null) {
			setGameInfo(g);
		}

	}
	
	private void setGameInfo(Game g) {
		TextView timestamp = (TextView) this.findViewById(R.id.gameTimestamp);
		TextView location = (TextView) this.findViewById(R.id.gameLocation);
		TextView players = (TextView) this.findViewById(R.id.gamePlayers);
		TextView sport = (TextView) this.findViewById(R.id.gameSport);
		ImageView sportIcon = (ImageView) this.findViewById(R.id.gameSportIcon);
		
		// check to see if each individual textview is null.
		// if not, assign some text!
		if (timestamp != null){
			String date = HelperFunction.convertToDate(g.getTimestamp());
			timestamp.setText(date);
		}
		
		if (location != null){
			location.setText(g.getLocation());
		}
		
		if (sport != null){
			sport.setText(g.getSport());
		}
		
		if (sportIcon != null){
			String s = g.getSport().toLowerCase(Locale.US);
			int id = HelperFunction.getResId(s, this.getBaseContext(), R.drawable.class);
			if (id != -1) {
				sportIcon.setBackgroundResource(id);
			}
		}
		
		int joined = g.getPlayersJoined();
		int total = g.getTotalPlayers();
		if (players != null){
			String str = joined + " out of " + total;
			players.setText(str);
		}
		
		// TODO: Change to actual player profiles
		List<Integer> playerList = new ArrayList<Integer>();
		for (int i = 0; i < joined; i++) {
			playerList.add(AppConstant.PLAYER);
		}
		for (int i = 0; i < total - joined; i++) {
			playerList.add(AppConstant.PLAYER_ABSENT);
		}
		
		ImageFragment fragment = (ImageFragment) getFragmentManager()
				.findFragmentById(R.id.playerGridView);
		if (fragment != null) {
			fragment.update(playerList);
		}
	}
	
	private AlertDialog createGameAlert(int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    
	    View v = inflater.inflate(R.layout.success_dialog, null);
	    TextView tv = (TextView) v.findViewById(R.id.dialog_message);
	    if (tv != null) {
    		tv.setText(message);
    	}
	    
    	builder.setView(v)
    	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   // TODO: send a request to gameUp
		        	   Intent result = new Intent();
		        	   result.putExtra(AppConstant.USER, USER_ID);
		        	   result.putExtra(AppConstant.LOGIN, loggedIn);
		       		   setResult(Activity.RESULT_OK, result);
		               finish();
		           }
    	});
    	
    	return builder.create();
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
		
		// Disconnecting maps
        mLocationClient.disconnect();

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}
		
		// Save the user_id and similar shared variables
		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(AppConstant.LOGIN, loggedIn);
		editor.putString(AppConstant.USER, USER_ID);
		editor.apply();
	}
	
	private void updateView() {
		// Set up the join/unjoin button
		final Button button = (Button) findViewById(R.id.joinButton);
		if (loggedIn) {
			button.setText(R.string.join);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	// TODO: Determine which string to show
	            	AlertDialog dialog = createGameAlert(R.string.alert_success_join);
	            	dialog.show();
	            }
	        });
		} else {
			button.setText(R.string.sign_up);
			button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	// Go to a new activity for logging in and out
	        		Intent intent = new Intent();
	        		intent.setClass(DisplayGameActivity.this, LoginActivity.class);
	        		intent.putExtra(AppConstant.USER, USER_ID);
	        		intent.putExtra(AppConstant.LOGIN, loggedIn);
	        		startActivityForResult(intent, AppConstant.LOGIN_ID);
	            }
	        });
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case AppConstant.LOGIN_ID:
				USER_ID = data.getStringExtra(AppConstant.USER);
				loggedIn = data.getBooleanExtra(AppConstant.LOGIN, false);
				
				Log.d("login", "(display activity) user: " + USER_ID + " loggedIn = " + loggedIn);
				
				// Finish joining the game if login is successful
				if (loggedIn) {
					AlertDialog dialog = createGameAlert(R.string.alert_success_join);
	            	dialog.show();
					break;
				}
			}
		}
	}

	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d("maps", "Connected");
        
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

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Log.d("maps", "Disconnected. Please re-connect.");
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        AppConstant.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }
}
