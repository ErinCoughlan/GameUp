package com.gameupapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddVenueFragment extends DialogFragment {
	
	public interface AddVenueDialogListener {
		public void onDialogPositiveClick(AddVenueFragment dialog);
	}
	
	private AddVenueDialogListener mListener;
	private String name;
	private String location;

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.add_venue, null);
		final EditText locationET = (EditText) v.findViewById(R.id.venue_location);
		final EditText nameET = (EditText) v.findViewById(R.id.venue_name);
		
		builder.setView(v);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	location = locationET.getText().toString();
		            name = nameET.getText().toString();
		        	mListener.onDialogPositiveClick(AddVenueFragment.this);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (AddVenueDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e("AddVenueDialogFragment", activity.toString() + " must implement AddVenueDialogListener");
		}
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getName() {
		// Name is optional, so if there is not name, return the location instead
		if (!name.equals("")) {
			return name;
		} else {
			return location;
		}
	}
}
