package com.gameupapp;

import java.util.Arrays;
import java.util.List;

public class AppConstant {
	// Debug information
	static final boolean DEBUG = true;
	static final String SHARED_PREF = "settings";
	static final int NUM_GAMES = 10;

	// Intent information ids
	static final String GAME = "game";
	static final String USER = "user";
	static final String LOGIN = "logged_in";
	static final String SETTINGS = "settings";
	
	// Stored variables in Shared Preferences
	static final String SPORT = "sport";
	static final String ABILITY = "ability";
	static final String DISTANCE = "distance";
	
	// Response from activities
	static final int DETAIL_ID = 0;
	static final int CREATE_ID = 1;
	static final int LOGIN_ID = 2;
	static final int SETTINGS_ID = 3;
	
	// Types of filters
	static final int FILTER_SPORT = 50;
	static final int FILTER_LOCATION = 51;
	static final int FILTER_TIME = 52;
	static final int FILTER_ABILITY = 53;
	static final int FILTER_DISTANCE = 54;
	static final int FILTER_ALL = 55;
	static final int FILTER_CLEAR = 500;
	
	// Initial List of Sports	
	static final List<String> SPORTS = Arrays.asList(
			"Baseball", "Basketball", "Football", "Soccer",
			"Tennis", "Volleyball", "Frisbee");
	
	// Ability Levels
	static final List<String> ABILITY_LEVELS = Arrays.asList(
			"Beginner", "Intermediate", "Advanced", "All Star");
	static final List<String> ABILITY_LEVELS_ANY = Arrays.asList(
			"Any", "Beginner", "Intermediate", "Advanced", "All Star");
	
	/*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    static final int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    static final int MAP_ZOOM = 18;
    static final int RQS_GooglePlayServices = 1;
    
    // Unknown sport icon
    static final Integer UNKNOWN_IMG = R.drawable.unknown;
    
    // Parse RequestCodes
    static final int FB_REQUEST = 32665;
	static final int MAX_GEOCODER_RESULTS = 1;
	
	static final boolean SHOULD_TRACE = false;
	static final boolean ANALYTICS = true;
}
