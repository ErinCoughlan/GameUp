package com.gameupapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_game);
		
		// Get the game id so we can retrieve info
		Intent intent = getIntent();
		String gameId = intent.getStringExtra(MainActivity.GAME_ID);
		String userId = intent.getStringExtra(MainActivity.USER);
	
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
		               finish();
		           }
    	});
    	
    	return builder.create();
	}
}
