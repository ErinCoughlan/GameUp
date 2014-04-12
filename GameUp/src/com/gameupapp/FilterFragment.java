package com.gameupapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView.OnEditorActionListener;

public class FilterFragment extends DialogFragment {
	public interface FilterDialogListener {
		public void onDialogPositiveClick(FilterFragment dialog);
		public void onDialogNeutralClick(FilterFragment dialog);
	}
	
	private FilterDialogListener mListener;
	private GameUpInterface gameup;
	private String sport;
	private int abilityLevel;
	private int distance;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (FilterDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e("FilterDialogFragment", activity.toString() + " must implement FilterDialogListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameup = GameUpInterface.getInstance();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.filter_fragment, null);
		initSportSpinner(v);
		initAbilitySpinner(v);
		// If we can connect, show the distance fields
		if (gameup.CAN_CONNECT) {
			v.findViewById(R.id.text_distance).setVisibility(View.VISIBLE);
			v.findViewById(R.id.edittext_distance).setVisibility(View.VISIBLE);
			initDistanceEditText(v);
		}
		builder.setView(v);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	mListener.onDialogPositiveClick(FilterFragment.this);
		        //save info where you want it
		        }
		});

		builder.setNeutralButton("Clear All Filters", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	mListener.onDialogNeutralClick(FilterFragment.this);
		        //save info where you want it
		        }
		});
		 
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
		  
		return builder.create();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();

		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(getActivity());
		}
	}
	
	private void initSportSpinner(View v) {
		final AutoCompleteTextView sportDropdown = (AutoCompleteTextView) v.findViewById(R.id.sport_dropdown);

		// Custom choices
		List<String> choices = new ArrayList<String>();

		for (Sport sport : gameup.getAllSports()) {
			choices.add(sport.getName());
		}
		
		Collections.sort(choices);

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);
		
		// Current sport selected
		String currSport = ((MainActivity) this.getActivity()).getSport();
		if (currSport != null) {
			sportDropdown.setText(currSport);
		}

		// Set the adapter to the spinner
		sportDropdown.setAdapter(adapter);
		sportDropdown.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				sport = (String) parent.getItemAtPosition(pos);
			}
		});
		
		// Set up the clear button
		ImageButton clear = (ImageButton) v.findViewById(R.id.sport_dropdown_clear);
		clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sportDropdown.setText("");
				sport = null;
				
			}
		});
		
	}
	
	private void initAbilitySpinner(View v) {
		final Spinner abilitySpinner = (Spinner) v.findViewById(R.id.ability_spinner);

		// Custom choices
		List<String> choices = new ArrayList<String>();
		choices.addAll(AppConstant.ABILITY_LEVELS_ANY);

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);
		
		// Set the adapter to the spinner
		abilitySpinner.setAdapter(adapter);
		
		// Current ability level
		int currAbility = ((MainActivity) this.getActivity()).getAbilityLevel();
		if (currAbility != -1) {
			// Translate from ABILITY_LEVELS to ABILITY_LEVELS_ANY
			String abilityString = AppConstant.ABILITY_LEVELS.get(currAbility);
			abilitySpinner.setSelection(AppConstant.ABILITY_LEVELS_ANY.indexOf(abilityString), false);
		}

		abilitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Use ABILITY_LEVELS here to match the create ability levels
				// Equals -1 if string was not found
				abilityLevel = AppConstant.ABILITY_LEVELS.indexOf(abilitySpinner.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				abilityLevel = -1;
			}
		});
	}
	
	private void initDistanceEditText(View v) {
		final EditText editText = (EditText) v.findViewById(R.id.edittext_distance);
		// Current ability level
		int currDistance = ((MainActivity) this.getActivity()).getDistance();
		if (currDistance != -1 && currDistance != 0) {
			// Translate from ABILITY_LEVELS to ABILITY_LEVELS_ANY
			editText.setText(Integer.toString(currDistance));
		}
		
		editText.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					editText.clearFocus();
					String s = editText.getText().toString();
					distance = Integer.parseInt(s);
				}
				return false;
			}
		});
	}
	
	public String getSport() {
		return sport;
	}
	
	public int getAbilityLevel() {
		return abilityLevel;
	}
	
	public int getDistance() {
		return distance;
	}
}
