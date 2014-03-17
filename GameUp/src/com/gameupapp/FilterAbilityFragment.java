package com.gameupapp;

import java.util.ArrayList;
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
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FilterAbilityFragment extends DialogFragment {
	public interface FilterAbilityDialogListener {
		public void onDialogPositiveClick(FilterAbilityFragment dialog);
	}
	
	private FilterAbilityDialogListener mListener;
	private int abilityLevel;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (FilterAbilityDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e("FilterAbilityDialogFragment", activity.toString() + " must implement FilterAbilityDialogListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.filter_ability_fragment, null);
		initAbilitySpinner(v);
		builder.setView(v);
		
		 builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	mListener.onDialogPositiveClick(FilterAbilityFragment.this);
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
	
	private void initAbilitySpinner(View v) {
		final Spinner abilitySpinner = (Spinner) v.findViewById(R.id.ability_spinner);

		// Custom choices
		List<String> choices = new ArrayList<String>();
		choices.addAll(AppConstant.ABILITY_LEVELS);

		// Create an ArrayAdapter with custom choices
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.drawable.spinner_item, choices);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.drawable.spinner_dropdown_item);

		// Set the adapter to the spinner
		abilitySpinner.setAdapter(adapter);
		abilitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				abilityLevel = AppConstant.ABILITY_LEVELS.indexOf(abilitySpinner.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				abilityLevel = -1;
			}
		});
	}
	
	public int getAbilityLevel() {
		return abilityLevel;
	}
}
