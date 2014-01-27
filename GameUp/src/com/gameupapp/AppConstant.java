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
}