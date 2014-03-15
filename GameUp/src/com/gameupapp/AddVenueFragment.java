package com.gameupapp;

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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AddVenueFragment extends DialogFragment {
	
	public interface AddVenueDialogListener {
		public void onDialogPositiveClick(AddVenueFragment dialog);
	}
	
	private AddVenueDialogListener mListener;
	
	
	/*private void initVenueNameEditText() {
		View view = getView();
		getView().findViewById(R.id.edittext_venuename);
		final EditText nameText = (EditText) getView().findViewById(R.id.edittext_venuename);
		nameText.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					nameText.clearFocus();
				}
				return false;
			}
		});
	}
	
	private void initVenueLocationEditText() {
		final EditText locationText = (EditText) getView().findViewById(R.id.edittext_venuelocation);
		locationText.setOnEditorActionListener(new OnEditorActionListener() {        
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					locationText.clearFocus();
				}
				return false;
			}
		});
	}*/
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.add_venue, null));
		
		 builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	mListener.onDialogPositiveClick(AddVenueFragment.this);
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
}
