package com.gameupapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CreateGameActivity extends Activity {
	
	private GameUpInterface gameup;
	private String USER_ID;
	private boolean loggedIn;
	
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(
            "h:mm a", Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(AppConstant.SHARED_PREF, 0);
		loggedIn = settings.getBoolean(AppConstant.LOGIN, false);
		USER_ID = settings.getString(AppConstant.USER, null);
		Log.d("login", "(display create) user: " + USER_ID + " loggedIn = " + loggedIn);
		updateView();
		
		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Initialize times for start and end
        Calendar c = Calendar.getInstance();
        Date start = c.getTime();
        c.add(Calendar.HOUR,  1);
        Date end = c.getTime();
        
        Button startDate = (Button) findViewById(R.id.start_date_picker);
        Button startTime = (Button) findViewById(R.id.start_time_picker);
        Button endDate = (Button) findViewById(R.id.end_date_picker);
        Button endTime = (Button) findViewById(R.id.end_time_picker);
        
        startDate.setText(dateFormatter.format(start));
        startTime.setText(timeFormatter.format(start));
        endDate.setText(dateFormatter.format(end));
        endTime.setText(timeFormatter.format(end));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// GameUp instance
		gameup = GameUpInterface.getInstance(USER_ID);
		gameup.registerObserver(this);
		
		// Initialize the spinners
        initSportSpinner();
        initLocationSpinner();
        initAbilitySpinner();
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
	
	private void initSportSpinner() {
		AutoCompleteTextView sportDropdown = (AutoCompleteTextView) findViewById(R.id.sport_dropdown);

		// Custom choices
		List<String> choices = new ArrayList<String>();

		for (Sport sport : gameup.getAllSports()) {
			choices.add(sport.getName());
		}
		
		Collections.sort(choices);

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		sportDropdown.setAdapter(adapter);
		sportDropdown.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				updateSportsIcon();
				LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
				dummy.requestFocus();
			}
		});
	}
	
	private void initLocationSpinner() {
		Spinner locationSpinner = (Spinner) findViewById(R.id.location_spinner);

		// Custom choices
		List<String> choices = new ArrayList<String>();
		
		// TODO: Get choices from API and set up Add New interactions
		Collections.sort(choices);
		choices.add("Add New");

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		locationSpinner.setAdapter(adapter);
	}
	
	private void initAbilitySpinner() {
		Spinner abilitySpinner = (Spinner) findViewById(R.id.ability_spinner);

		// Custom choices
		List<String> choices = new ArrayList<String>();
		choices.addAll(AppConstant.ABILITY_LEVELS);

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		abilitySpinner.setAdapter(adapter);
		abilitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				updateSportsIcon();
				LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
				dummy.requestFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});
	}
	
	public void showTimePickerDialog(View v) {
	    TimePickerFragment frag = new TimePickerFragment();
		frag.setUpdateButton((Button) v, timeFormatter);
	    DialogFragment newFragment = frag;
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public void showDatePickerDialog(View v) {
		DatePickerFragment frag = new DatePickerFragment();
		frag.setUpdateButton((Button) v, dateFormatter);
	    DialogFragment newFragment = frag;
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	private AlertDialog createGameAlert(int message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
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
	
	public void showSuccessDialog(View v) {
		// TODO: Determine which string to show
    	AlertDialog dialog = createGameAlert(R.string.alert_success_create);
    	dialog.show();
	}
	
	private void updateSportsIcon() {
		AutoCompleteTextView sportDropdown = (AutoCompleteTextView) findViewById(R.id.sport_dropdown);
		
		ImageView sportIcon = (ImageView) findViewById(R.id.game_sport_icon);
        String s = sportDropdown.getText().toString().toLowerCase(Locale.US);
        if (sportIcon != null) {
			int resId = HelperFunction.getResId(s, getBaseContext(), R.drawable.class);
			if (resId != -1) {
				sportIcon.setBackgroundResource(resId);
			} else {
				sportIcon.setBackgroundResource(R.drawable.unknown_icon);
			}
        }
	}
	
	private void updateView() {
		// Set up the create button
		final Button button = (Button) findViewById(R.id.createButton);
		if (loggedIn) {
			button.setText(R.string.create);
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
	        		intent.setClass(CreateGameActivity.this, LoginActivity.class);
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
				
				Log.d("login", "(create activity) user: " + USER_ID + " loggedIn = " + loggedIn);
				
				// Finish joining the game if login is successful
				if (loggedIn) {
					AlertDialog dialog = createGameAlert(R.string.alert_success_join);
	            	dialog.show();
					break;
				}
			}
		}
	}
}
