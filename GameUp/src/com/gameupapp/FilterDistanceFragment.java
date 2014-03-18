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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FilterDistanceFragment extends DialogFragment {
	public interface FilterDistanceDialogListener {
		public void onDialogPositiveClick(FilterDistanceFragment dialog);
	}
	
	private FilterDistanceDialogListener mListener;
	private int distance;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (FilterDistanceDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e("FilterDistanceDialogFragment", activity.toString() + " must implement FilterDistanceDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.filter_distance_fragment, null);
		initDistanceEditText(v);
		builder.setView(v);
		
		 builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	final EditText editText = (EditText) v.findViewById(R.id.edittext_distance);
		        	String s = editText.getText().toString();
		        	if (!s.equals("")) {
		        		distance = Integer.parseInt(s);
		        	} else {
		        		distance = -1;
		        	}
		        	mListener.onDialogPositiveClick(FilterDistanceFragment.this);
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
	
	private void initDistanceEditText(View v) {
		final EditText editText = (EditText) v.findViewById(R.id.edittext_distance);
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
	
	public int getDistance() {
		return distance;
	}
}
