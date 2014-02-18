package com.gameupapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class LoginActivity extends Activity {
	private String USER_ID;
	private boolean loggedIn;

	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
		// Restore preferences
		SharedPreferences settings = getSharedPreferences("settings", 0);
		loggedIn = settings.getBoolean(AppConstant.LOGIN, false);
		USER_ID = settings.getString(AppConstant.USER, null);
		Log.d("login", "(login create) user_id: " + USER_ID + " is loggedIn " + loggedIn);

		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		Session.getActiveSession().addCallback(callback);
	}

	@Override
	public void onStop() {
		super.onStop();
		uiHelper.onStop();

		Session.getActiveSession().removeCallback(callback);
		
		// Save the user_id and similar shared variables
		SharedPreferences settings = getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(AppConstant.LOGIN, loggedIn);
		editor.putString(AppConstant.USER, USER_ID);
		editor.apply();
		
		Log.d("login", "(login stop) user_id: " + USER_ID + " is loggedIn " + loggedIn);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Intent result = new Intent();
		setResult(Activity.RESULT_CANCELED, result);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}	

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.d("facebook", "session state change");
		if (state.isClosed()) {
			Log.d("facebook", "state is now closed");
			logOut();
		} else if (state.isOpened()) {
			Log.d("facebook", "state is now open");
			logIn(session);
		}
	}
	
	private void logIn(Session session) {
		// Request user data and show the results
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				Log.d("facebook", user.getFirstName());
				USER_ID = user.getFirstName();
				loggedIn = true;
				Intent result = new Intent();
				result.putExtra(AppConstant.USER, USER_ID);
				result.putExtra(AppConstant.LOGIN, loggedIn);
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		}).executeAsync();
	}
	
	private void logOut() {
		loggedIn = false;
		Intent result = new Intent();
		result.putExtra(AppConstant.USER, USER_ID);
		result.putExtra(AppConstant.LOGIN, loggedIn);
		setResult(Activity.RESULT_OK, result);
		finish();
	}
}
