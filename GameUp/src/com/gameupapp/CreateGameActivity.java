package com.gameupapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;

public class CreateGameActivity extends Activity {
	private GameUpInterface gameup;
	private int abilityLevel;
	private String sport;
	
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(
            "h:mm a", Locale.getDefault());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);

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
		gameup = GameUpInterface.getInstance();
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
				LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
				dummy.requestFocus();
				
				InputMethodManager imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
				AutoCompleteTextView sportDropdown = (AutoCompleteTextView) findViewById(R.id.sport_dropdown);
				imm.hideSoftInputFromWindow(sportDropdown.getWindowToken(), 0);
				sport = (String) parent.getItemAtPosition(pos);
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
				LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
				dummy.requestFocus();
				abilityLevel = (Integer) parent.getItemAtPosition(pos);
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
	
	private void updateView() {
		// Set up the create button
		final Button button = (Button) findViewById(R.id.createButton);
		ParseUser user = ParseUser.getCurrentUser();
		final boolean loggedIn = (user != null);
		if (loggedIn) {
			button.setText(R.string.create);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            	/** TODO Check all of this, Philip wrote it and has no idea
	            	 * if any of it is right.
	            	 */
	            	
	                long startDateL = ((DatePicker) findViewById(R.id.start_date_picker)).getMaxDate();
	                int startHour = ((TimePicker) findViewById(R.id.start_time_picker)).getCurrentHour();
	                int startMinute = ((TimePicker) findViewById(R.id.start_time_picker)).getCurrentMinute();
	                
	                long endDateL = ((DatePicker) findViewById(R.id.end_date_picker)).getMaxDate();
	                int endHour = ((TimePicker) findViewById(R.id.end_time_picker)).getCurrentHour();
	                int endMinute = ((TimePicker) findViewById(R.id.end_time_picker)).getCurrentMinute();
	                
	                long startHourL = TimeUnit.HOURS.toMillis(startHour);
	                long startMinuteL = TimeUnit.MINUTES.toMillis(startMinute);
	                
	                long endHourL = TimeUnit.HOURS.toMillis(endHour);
	                long endMinuteL = TimeUnit.MINUTES.toMillis(endMinute);
	                
	                Date startDate = new Date(startDateL + startHourL 
	                		+ startMinuteL);
	                Date endDate = new Date(endDateL + endHourL + endMinuteL);
	                
	                // TODO get actual location
	                long latitude = 0;
	                long longitude = 0;
	                
	                // TODO get actual readable location
	                String readableLocation = "aLocation";
	                
	            	boolean succeeded = gameup.createGame(startDate, endDate, 
	            			abilityLevel, readableLocation, latitude, 
	            			longitude, sport);
	            	
	            	AlertDialog dialog;
	            	if (succeeded) {
	            		dialog = HelperFunction.createGameAlert(
	            				R.string.alert_success_create, true, CreateGameActivity.this, loggedIn);
	            	} else {
	            		dialog = HelperFunction.createGameAlert(
	            				//TODO not sure if this should be false or true
	            				R.string.alert_fail_create, false, CreateGameActivity.this, loggedIn);
	            	}
	            	
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
}
