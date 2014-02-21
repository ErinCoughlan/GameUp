package com.gameupapp;

import java.util.Arrays;
import java.util.List;

public class AppConstant {

	// Intent information ids
	static final String GAME = "game";
	static final String USER = "user";
	static final String LOGIN = "logged_in";
	
	// Response from activities
	static final int DETAIL_ID = 0;
	static final int CREATE_ID = 1;
	static final int LOGIN_ID = 2;
	
	// Initial List of Sports	
	static final List<String> sports = Arrays.asList(
			"Baseball", "Basketball", "Football", "Soccer",
			"Tennis", "Volleyball", "Frisbee");
	
	/*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    static final int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    static final int MAP_ZOOM = 18;
    static final int RQS_GooglePlayServices = 1;
    
    // Debug list of player images
    static final Integer[] PLAYERS = {
    	R.drawable.player, R.drawable.player,
    	R.drawable.player_absent, R.drawable.player_absent
    };
    
    static final Integer PLAYER = R.drawable.player;
    static final Integer PLAYER_ABSENT = R.drawable.player_absent;
}
