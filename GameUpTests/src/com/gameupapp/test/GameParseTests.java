package com.gameupapp.test;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.gameupapp.GameParse;
import com.gameupapp.R;
import com.gameupapp.Sport;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import junit.framework.TestCase;

public class GameParseTests extends TestCase {
	protected GameParse firstGame;
	protected GameParse secondGame;
	protected GameParse thirdGame;
	
	protected Sport soccer;
	protected Sport tennis;
	protected Sport pingpong;
	
	@Override
	protected void setUp() {
		// Parse information
		// Register GameParse subclass
		ParseObject.registerSubclass(GameParse.class);
		ParseObject.registerSubclass(Sport.class);
		// erin@gameupapp.com Parse
		Parse.initialize(this, "yYt3t3sH7XMU81BXgvYaXnWEsoahXCJb5dhupvP5",
				"dZCnn1DrZJMXyZOkZ7pbM7Z0ePwTyIJsZzgY77FU");
		// Phil's Parse
		// Parse.initialize(this, "a0k4KhDMvl3Mz2CUDcDMLAgnt5uaCLuIBxK41NGa",
		//		"3EJKdG7SuoK89gkFkN1rcDNbFvIgN71iH0mJyfDC");

		ParseFacebookUtils.initialize(getString(R.string.fb_app_id));
		
		
		// We need a few sports to make games.
		
		ParseQuery<Sport> query = ParseQuery.getQuery(Sport.class);
		
		try {
			soccer = query.whereMatches("sport", "soccer").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		try {
			tennis = query.whereMatches("sport", "tennis").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		try {
			pingpong = query.whereMatches("sport", "pingpong").getFirst();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		// We need a user.
		ParseUser.enableAutomaticUser();
		
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
		
		
		// Assert these so we die immediately if parse errors out on us. Note
		// that these implicitly test adding a player as well.
		assertTrue(firstGame.createGame(start1.getTime(), end1.getTime(), 3, 8, 
				"Parents Field", 37, 36, "Soccer"));
		assertEquals("Failed to properly add player on creation", 1, 
				firstGame.getCurrentPlayerCount());
		
		assertTrue(secondGame.createGame(start2.getTime(), end2.getTime(), 1, 10, "LAC", 37,
				37, "ping pong"));
		assertEquals("Failed to properly add player on creation", 1, 
				secondGame.getCurrentPlayerCount());
		
		assertTrue(thirdGame.createGame(start3.getTime(), end3.getTime(), 1, 10, 
				"Tennis Center", 37, 37, "tennis"));
		assertEquals("Failed to properly add player on creation", 1, 
				thirdGame.getCurrentPlayerCount());
	}
	
	@Override
	protected void tearDown() {
		try {
			firstGame.delete();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error cleaning up: could not delete games.");
		}
		
		try {
			secondGame.delete();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error cleaning up: could not delete games.");
		}
		
		try {
			thirdGame.delete();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error cleaning up: could not delete games.");
		}
		
		try {
			ParseUser.getCurrentUser().delete();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error cleaning up: couldn't delete anonymous user");
		}
	}


	
	@Test
	public void testJoinGamesAgain() {
		// These should fail, since we've already joined.
		assertFalse("Re-adding a player succeeded when should have failed", 
				firstGame.addPlayer());
		assertEquals("Player count was erroneoulsy incremented", 
				firstGame.getCurrentPlayerCount(), 1);
		
		assertFalse("Re-adding a player succeeded when should have failed", 
				secondGame.addPlayer());
		assertEquals("Player count was erroneoulsy incremented", 
				secondGame.getCurrentPlayerCount(), 1);
		
		assertFalse("Re-adding a player succeeded when should have failed", 
				thirdGame.addPlayer());
		assertEquals("Player count was erroneoulsy incremented", 
				thirdGame.getCurrentPlayerCount(), 1);
	}
	
	@Test
	public void testSetSport() {
		try {
			secondGame.setSport("tennis");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		String sport = secondGame.getSport();
		
		assertTrue("Setting sport failed.", sport.equals("tennis"));
	}
	
	
	
	

}
