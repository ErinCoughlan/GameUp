package com.gameupapp;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayGameActivity extends Activity {
	
	private GameUpInterface gameup;
	private String USER_ID;
	private String GAME_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_game);

		// TODO: Change the button text depending on if the person joined the game
		
		// Set up the join/unjoin button
		final Button button = (Button) findViewById(R.id.buttonJoin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// TODO: Determine which string to show
            	AlertDialog dialog = createJoinGameAlert(R.string.alert_success_join);
            	dialog.show();
            }
        });
	}
	
	@Override
	public void onStart() {
		super.onStart();

		// Get the game id so we can retrieve info
		Intent intent = getIntent();
		GAME_ID = intent.getStringExtra(MainActivity.GAME_ID);
		USER_ID = intent.getStringExtra(MainActivity.USER);
		
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
			String date = GameAdapter.convertToDate(g.getTimestamp());
			timestamp.setText(date);
		}
		
		if (location != null){
			location.setText(g.getLocation());
		}
		
		if (players != null){
			String str = g.getPlayersJoined() + " out of " + g.getTotalPlayers();
			players.setText(str);
		}
		
		if (sport != null){
			sport.setText(g.getSport());
		}
		
		if (sportIcon != null){
			String s = g.getSport().toLowerCase(Locale.US);
			int id = GameAdapter.getResId(s, this.getBaseContext(), R.drawable.class);
			if (id != -1) {
				sportIcon.setBackgroundResource(id);
			}
		}
	}
	
	private AlertDialog createJoinGameAlert(int message) {
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
