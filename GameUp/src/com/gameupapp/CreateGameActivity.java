package com.gameupapp;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class CreateGameActivity extends Activity {
	
	private GameUpInterface gameup;
	private String USER_ID;
	private String GAME_ID;
	
	// Spinner stuff
	private Spinner sportSpinner;
	private Spinner locationSpinner;
	
	// TODO: I18n?
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "MMM dd, yyyy");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(
            "h:mm a");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Initialize the spinners
        initSportSpinner();
        initLocationSpinner();
        
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

		// Get the game id so we can retrieve info
		Intent intent = getIntent();
		USER_ID = intent.getStringExtra(MainActivity.USER);
		
		// GameUp instance
		gameup = GameUpInterface.getInstance(USER_ID);
		gameup.registerObserver(this);
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

		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("user_id", USER_ID);
		editor.apply();
	}
	
	private void initSportSpinner() {
		sportSpinner = (Spinner) findViewById(R.id.sport_spinner);

		// Custom choices
		List<CharSequence> choices = new ArrayList<CharSequence>();
		
		// TODO: Get choices from API and order alphabetically
		choices.add("Baseball");
		choices.add("Basketball");
		choices.add("Football");
		choices.add("Soccer");
		choices.add("Tennis");
		choices.add("Volleyball");
		choices.add("Add New");

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		sportSpinner.setAdapter(adapter);
		sportSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Log.d("spinner", sportSpinner.getSelectedItem().toString());
				updateSportsIcon();
		        
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private void initLocationSpinner() {
		locationSpinner = (Spinner) findViewById(R.id.location_spinner);

		// Custom choices
		List<CharSequence> choices = new ArrayList<CharSequence>();
		
		// TODO: Get choices from API and order alphabetically
		choices.add("Add New");

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		locationSpinner.setAdapter(adapter);
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
	
	public void showSuccessDialog(View v) {
		// TODO: Determine which string to show
    	AlertDialog dialog = createGameAlert(R.string.alert_success_create);
    	dialog.show();
	}
	
	private void updateSportsIcon() {
		ImageView sportIcon = (ImageView) findViewById(R.id.game_sport_icon);
        String s = sportSpinner.getSelectedItem().toString().toLowerCase(Locale.US);
        if (sportIcon != null) {
			int resId = getResId(s, getBaseContext(), R.drawable.class);
			if (resId != -1) {
				sportIcon.setBackgroundResource(resId);
			} else {
				sportIcon.setBackgroundResource(R.drawable.unknown_icon);
			}
        }
	}
	
	/**
	 * Finds the id of a resource given its string name, class, and context.
	 * Returns -1 if no resource is found.
	 * 
	 * Usage: getResId("icon", context, Drawable.class);
	 */
	public static int getResId(String variableName, Context context, Class<?> c) {
	    try {
	        Field idField = c.getDeclaredField(variableName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } 
	}
}
