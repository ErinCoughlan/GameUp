package com.gameupapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;

public class CreateGameActivity extends Activity implements 
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener {
	
	private GameUpInterface gameup;
	private int abilityLevel;
	private String sport;
	private Calendar startC;
	private Calendar endC;
	private LocationClient locationClient;
	
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
		startC = Calendar.getInstance();
		endC = Calendar.getInstance();
        endC.add(Calendar.HOUR,  1);
        updateTimes();
        locationClient = new LocationClient(this, this, this);
        
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		// GameUp instance
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);
		
		// Initialize the spinners and other elements
        initSportSpinner();
        initLocationSpinner();
        initAbilitySpinner();
        initPlayersEditText();
        locationClient.connect();
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
				sport = (String) parent.getItemAtPosition(pos);
				releaseFocus();
			}
		});
		
	}
	
	private void initLocationSpinner() {
		final Spinner locationSpinner = (Spinner) findViewById(R.id.location_spinner);
		
		locationSpinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});

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
		
		locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String selectedItem = locationSpinner.getSelectedItem().toString();
				if(selectedItem.equals("Add New")) {
					AlertDialog dialog;
					LayoutInflater inflater = (LayoutInflater) 
							getSystemService(LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.add_venue, 
							(ViewGroup) findViewById(R.layout.activity_create_game));
					
					AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
				    builder.setView(layout);
				    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				            //save info where you want it
				        }
				    });
				    
					dialog = builder.create();
					dialog.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void initAbilitySpinner() {
		final Spinner abilitySpinner = (Spinner) findViewById(R.id.ability_spinner);
		
		abilitySpinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(
					Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
		});

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
				abilityLevel = AppConstant.ABILITY_LEVELS.indexOf(abilitySpinner.getSelectedItem().toString());
				releaseFocus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				abilityLevel = -1;
			}
		});
	}
	
	private void initPlayersEditText() {
		final EditText editText = (EditText) findViewById(R.id.edittext_players);
		editText.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					editText.clearFocus();
					releaseFocus();
				}
				return false;
			}
		});
	}
	
	public void showTimePickerDialog(final View v, int hourOfDay, int minute) {
		TimePickerDialog diag = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				if (v.getId() == R.id.start_time_picker) {
					startC.set(Calendar.HOUR_OF_DAY, hourOfDay);
					startC.set(Calendar.MINUTE, minute);
					
					// Make it a little easier for the user to set the time
					Date startDate = startC.getTime();
					Date endDate = endC.getTime();
					if (endDate.before(startDate)) {
						endC.set(Calendar.HOUR_OF_DAY, hourOfDay);
						endC.set(Calendar.MINUTE, minute);
						endC.add(Calendar.HOUR,  1);
					}
				} else if (v.getId() == R.id.end_time_picker) {
					endC.set(Calendar.HOUR_OF_DAY, hourOfDay);
					endC.set(Calendar.MINUTE, minute);
				}
				updateTimes();
			}
		}, hourOfDay, minute, false);
		
		diag.show();
	}
	
	public void showDatePickerDialog(final View v, int year, int monthOfYear, int dayOfMonth) {
		DatePickerDialog diag = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int month,
					int dayOfMonth) {
				if (v.getId() == R.id.start_date_picker) {
					startC.set(Calendar.YEAR, year);
					startC.set(Calendar.MONTH, month);
					startC.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					
					// Make it a little easier for the user to set the date
					Date startDate = startC.getTime();
					Date endDate = endC.getTime();
					if (endDate.before(startDate)) {
						endC.set(Calendar.YEAR, year);
						endC.set(Calendar.MONTH, month);
						endC.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					}
				} else if (v.getId() == R.id.end_date_picker) {
					endC.set(Calendar.YEAR, year);
					endC.set(Calendar.MONTH, month);
					endC.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				}
				updateTimes();
				
			}
		}, year, monthOfYear, dayOfMonth);
		
		diag.show();
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
	            	boolean succeeded = createGame();
	            	
	            	AlertDialog dialog;
	            	if (succeeded) {
	            		dialog = HelperFunction.createGameAlert(
	            				R.string.alert_success_create, true, CreateGameActivity.this, loggedIn);
	            	} else {
	            		dialog = HelperFunction.createGameAlert(
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
	
	private void updateTimes() {
        Date start = startC.getTime();
        Date end = endC.getTime();
        
        Button startDate = (Button) findViewById(R.id.start_date_picker);
        Button startTime = (Button) findViewById(R.id.start_time_picker);
        Button endDate = (Button) findViewById(R.id.end_date_picker);
        Button endTime = (Button) findViewById(R.id.end_time_picker);
        
        startDate.setText(dateFormatter.format(start));
        startTime.setText(timeFormatter.format(start));
        endDate.setText(dateFormatter.format(end));
        endTime.setText(timeFormatter.format(end));
        
        final int startYear = startC.get(Calendar.YEAR);
        final int startMonth = startC.get(Calendar.MONDAY);
        final int startDay = startC.get(Calendar.DAY_OF_MONTH);
        final int startHour = startC.get(Calendar.HOUR_OF_DAY);
        final int startMinute = startC.get(Calendar.MINUTE);
        
        final int endYear = endC.get(Calendar.YEAR);
        final int endMonth = endC.get(Calendar.MONDAY);
        final int endDay = endC.get(Calendar.DAY_OF_MONTH);
        final int endHour = endC.get(Calendar.HOUR_OF_DAY);
        final int endMinute = endC.get(Calendar.MINUTE);
        
        // Set up the onClicks for each button
        startTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showTimePickerDialog(v, startHour, startMinute);
			}
		});
        
        endTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showTimePickerDialog(v, endHour, endMinute);
			}
		});
        
        startDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePickerDialog(v, startYear, startMonth, startDay);
			}
		});
        
        endDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDatePickerDialog(v, endYear, endMonth, endDay);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case AppConstant.LOGIN_ID:
				updateView();
			}
		}
	}
	
	private boolean createGame() {
		Date startDate = startC.getTime();
		Date endDate = endC.getTime();
		
		// Max number of players
		final EditText editText = (EditText) findViewById(R.id.edittext_players);
		int playerCount = Integer.parseInt(editText.getText().toString());
        
        // TODO get actual location
        long latitude = 0;
        long longitude = 0;
        
        // TODO get actual readable location
        String readableLocation = "aLocation";
        
        Log.d("create", "ability level: " + Integer.toString(abilityLevel));
        Log.d("create", "sport: " + sport);
        Log.d("create", "playerCount: " + Integer.toString(playerCount));
        
    	boolean succeeded = gameup.createGame(startDate, endDate, 
    			abilityLevel, playerCount, readableLocation, latitude, 
    			longitude, sport);
    	
    	return succeeded;
	}
	
	private void releaseFocus() {
		LinearLayout dummy = (LinearLayout) findViewById(R.id.dummy);
		dummy.requestFocus();
		
		InputMethodManager imm = (InputMethodManager) getSystemService(
			Context.INPUT_METHOD_SERVICE);
		View v = findViewById(android.R.id.content);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
