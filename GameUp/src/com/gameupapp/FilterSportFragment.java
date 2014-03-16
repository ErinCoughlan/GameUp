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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;

public class FilterSportFragment extends DialogFragment {
	public interface FilterSportDialogListener {
		public void onDialogPositiveClick(FilterSportFragment dialog);
	}
	
	private FilterSportDialogListener mListener;
	private GameUpInterface gameup;
	private String sport;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (FilterSportDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e("FilterSportDialogFragment", activity.toString() + " must implement FilterSportDialogListener");
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
		View v = inflater.inflate(R.layout.filter_sport, null);
		initSportSpinner(v);
		builder.setView(v);
		
		 builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	mListener.onDialogPositiveClick(FilterSportFragment.this);
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
		AutoCompleteTextView sportDropdown = (AutoCompleteTextView) v.findViewById(R.id.sport_dropdown);

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

		// Set the adapter to the spinner
		sportDropdown.setAdapter(adapter);
		sportDropdown.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				sport = (String) parent.getItemAtPosition(pos);
			}
		});
		
	}
	
	public String getSport() {
		return sport;
	}
}
