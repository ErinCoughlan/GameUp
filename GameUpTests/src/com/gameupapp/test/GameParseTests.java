package com.gameupapp.test;
import java.util.Calendar;
import java.util.Date;

import com.gameupapp.GameParse;
import com.gameupapp.Sport;
import com.parse.ParseException;
import com.parse.ParseQuery;

import junit.framework.TestCase;

public class GameParseTests extends TestCase {
	protected GameParse firstGame;
	protected GameParse secondGame;
	protected GameParse thirdGame;
	
	protected Sport soccer;
	protected Sport tennis;
	protected Sport pingpong;
	
	protected void setUp() {
		// We need a few sports to make games.
		
		ParseQuery<Sport> query = ParseQuery.getQuery(Sport.class);
		
		try {
			soccer = query.whereMatches("sport", "soccer").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			tennis = query.whereMatches("sport", "tennis").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			pingpong = query.whereMatches("sport", "pingpong").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void createGames() {
		
		Calendar start1 = Calendar.getInstance();
		Calendar start2 = Calendar.getInstance();
		Calendar start3 = Calendar.getInstance();
		Calendar end1 = Calendar.getInstance();
		Calendar end2 = Calendar.getInstance();
		Calendar end3 = Calendar.getInstance();
		
		// TODO dynamically set fields
		start1.set(2014, 6, 14, 9, 15, 36);
		start2.set(2014, 3, 14, 9, 15, 36);
		start3.set(2014, 9, 27, 15, 0, 12);
		
		end1.set(2014,6,14,10,15,0);
		end2.set(2014,3,15,2,14);
		end3.set(2014,9,27,23,0,0);
		
		
		firstGame.createGame(start1.getTime(), end1.getTime(), 3, 0, 
				"Parents Field", 37, 36, "Soccer");
		
		secondGame.createGame(start2.getTime(), end2.getTime(), 1, 0, "LAC", 37,
				37, "ping pong");
		
		thirdGame.createGame(start3.getTime(), end3.getTime(), 1, 0, 
				"Tennis Center", 37, 37, "tennis");
	}

}
