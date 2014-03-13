package com.gameupapp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	private GameUpInterface gameup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Back button in app
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Set up login and logout buttons
		ImageButton loginButton = (ImageButton) findViewById(R.id.authButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseUser currentUser = ParseUser.getCurrentUser();
				if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
					// Go to the user info activity
					logOut();
				} else {
					logIn();
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		// GameUp instance
		gameup = GameUpInterface.getInstance();
		gameup.registerObserver(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		
		// Clear the observers
		if (gameup != null) {
			gameup.removeObserver(this);
		}
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

		// Finish auth with Parse if we executed a login (and the user isn't linked yet)
		if (requestCode == AppConstant.FB_REQUEST) {
	        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		}
	}
	
	private void logIn() {
		List<String> permissions = Arrays.asList(Permissions.User.EMAIL, Permissions.User.ABOUT_ME);
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException e) {
					if (user == null) {
						Log.d("facebook", "User cancelled the Facebook login");
						Intent result = new Intent();
						setResult(Activity.RESULT_CANCELED, result);
						finish();
					} else if (user.isNew()) {
						Log.d("facebook", "User signed up and logged in through Facebook!");
						updateUserInfo();
					} else {
						Log.d("facebook", "User logged in through Facebook!");
						updateUserInfo();
					}
					
				}
			});		
	}
	
	private void updateUserInfo() {
		// Request user data and show the results
		Session session = ParseFacebookUtils.getSession();
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				Map<String, Object> map = user.asMap();
				Object emailObj = map.get("email");
				String email = emailObj.toString();
				
				ParseUser pUser = ParseUser.getCurrentUser();
				pUser.setEmail(email);
				pUser.put("firstname", user.getFirstName());

				Intent result = new Intent();
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		}).executeAsync();
	}
	
	private void logOut() {
		ParseUser.logOut();
		Intent result = new Intent();
		setResult(Activity.RESULT_OK, result);
		finish();
	}
}
