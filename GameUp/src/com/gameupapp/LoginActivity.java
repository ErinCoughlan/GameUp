package com.gameupapp;

import java.util.Arrays;

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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	private GameUpInterface gameup;
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
		SharedPreferences settings = getSharedPreferences(AppConstant.SHARED_PREF, 0);
		loggedIn = settings.getBoolean(AppConstant.LOGIN, false);
		USER_ID = settings.getString(AppConstant.USER, null);

		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		// GameUp instance
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);

		Session.getActiveSession().addCallback(callback);
		
		Log.d("login", "at least I'm starting");
	}

	@Override
	public void onStop() {
		super.onStop();
		uiHelper.onStop();
		
		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}

		Session.getActiveSession().removeCallback(callback);
		
		// Save the user_id and similar shared variables
		SharedPreferences settings = getSharedPreferences(AppConstant.SHARED_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(AppConstant.LOGIN, loggedIn);
		editor.putString(AppConstant.USER, USER_ID);
		editor.apply();
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
		
		// Finish auth with Parse if we executed a login (and the user isn't linked yet)
		if (requestCode == AppConstant.FB_REQUEST) {
	        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}	

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isClosed()) {
			Log.d("login", "state is closed");
			logOut();
		} else if (state.isOpened()) {
			Log.d("login", "state is opened");
			logIn(session);
		}
	}
	
	private void logIn(Session session) {
		ParseFacebookUtils.logIn(this, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException e) {
					if (user == null) {
						Log.d("facebook", "User cancelled the Facebook login");
						Intent result = new Intent();
						result.putExtra(AppConstant.USER, USER_ID);
						result.putExtra(AppConstant.LOGIN, loggedIn);
						setResult(Activity.RESULT_CANCELED, result);
						finish();
					} else if (user.isNew()) {
						Log.d("facebook", "User signed up and logged in through Facebook!");
					} else {
						Log.d("facebook", "User logged in through Facebook!");
					}
					
				}
			});
		
		// Request user data and show the results
		// TODO: Determine if this login information can be stored with Parse
		//       instead of being passed around using SharedPrefs
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				Log.d("facebook", user.getName());
				USER_ID = user.getFirstName();
				Log.d("facebook", user.getId());
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
