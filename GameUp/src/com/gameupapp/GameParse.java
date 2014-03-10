package com.gameupapp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("GameParse")
public class GameParse extends ParseObject {
	
	public String getGameID() {
		return getObjectId();
	}
	
	/**
	 * 
	 * @param latitude Double representing latitude, -90.0 to 90.0
	 * @param longitude Double representing longitude, -180.0 to 180.0
	 */
	public void setLocation(double latitude, double longitude) {
		ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);
		put("location", location);
		saveInBackground();
	}
	
	public ParseGeoPoint getLocation() {
		return getParseGeoPoint("location");
	}
	
	/**
	 * @deprecated Use start and end time instead
	 * @return Date object for the start date/time of a game
	 */
	public Date getDateTime() {
		return (Date) get("startDateTime"); 
	}
	
	/**
	 * @deprecated Use start and end time instead
	 * @param dateTime Date object with the start date/time of a game
	 */
	public void setDateTime(Date dateTime) {
		put("startDateTime", dateTime);
		saveInBackground();
	}
	
	public Date getStartDateTime() {
		return (Date) get("startDateTime");
	}
	
	public boolean setStartDateTime(Date dateTime) {
		put("startDateTime", dateTime);
		saveInBackground();
		
		return true;
	}
	
	public Date getEndDateTime() {
		return (Date) get("endDateTime");
	}
	
	public void setEndDateTime(Date dateTime) {
		put("endDateTime", dateTime);
		saveInBackground();
	}
	
	public void setMaxPlayerCount(int maxCount) {
		put("maxPlayerCount", maxCount);
		saveInBackground();
	}
	
	public int getMaxPlayerCount() {
		return getInt("maxPlayerCount");
	}
	
	public int getCurrentPlayerCount() { 
		return getInt("currentPlayerCount");
	}
	
	public void setCurrentPlayerCount(int newCount) {
		put("currentPlayerCount", newCount);
		saveInBackground();
	}
	
	public void incrementCurrentPlayerCount() {
		increment("currentPlayerCount");
		saveInBackground();
	}
	
	public void setSport(String sport) {
		ParseQuery<Sport> sportQuery = ParseQuery.getQuery(Sport.class);
		sportQuery.whereEqualTo("sport", sport);
		Sport parseSport;
		try {
			parseSport = sportQuery.getFirst();
		} catch (ParseException e) {
			Log.e("GameParse setSport", "Failed to lookup sport", e);
			return;
		}
		put("sport", parseSport);
		saveInBackground();
	}
	
	public String getSport() {
		Sport sport = (Sport) getParseObject("sport");
		try {
			sport.fetchIfNeeded();
		} catch (ParseException e) {
			Log.e("GameParse getSport", "Couldn't fetch sport from server");
		}
		return sport.getName();
	}
	
	public String getReadableLocation() {
		return getString("readableLocation");
	}
	
	public void setReadableLocation(String location) {
		put("readableLocation", location);
		saveInBackground();
	}
	
	public int getAbilityLevel() {
		return getInt("abilityLevel");
	}
	
	public void setAbilityLevel(int level) {
		put("abilityLevel", level);
		saveInBackground();
	}

	public JSONArray getPlayers() {
		return getJSONArray("Users");
	}

	
	// TODO throw error up to UI
	public boolean addPlayer() {
		try {
			refresh();
		} catch (ParseException e) {
			Log.d("addPlayer", "Couldn't refresh game", e);
		}
		
		int maxCount = getMaxPlayerCount();
		int currentCount = getCurrentPlayerCount();
		if(currentCount < maxCount) {
			incrementCurrentPlayerCount();
			ParseUser currentUser = ParseUser.getCurrentUser();
			addUnique("Users", currentUser);
			return true;
		}
		
		saveInBackground();
		
		return false;
	}
	
	public void removePlayer() {
		List<ParseUser> currentUser = new ArrayList<ParseUser>();
		currentUser.add(ParseUser.getCurrentUser());
		removeAll("Users", currentUser);
		
		saveInBackground();
	}
	
	public boolean createGame(Date startDate, Date endDate, int abilityLevel,
			String readableLocation, double latitude, double longitude, 
			String sport) {
		// start date must come before end date
		if(endDate.before(startDate)) {
			return false;
		}
		
		setStartDateTime(startDate);
		setEndDateTime(endDate);
		setAbilityLevel(abilityLevel);
		setLocation(latitude,longitude);
		setReadableLocation(readableLocation);
		setSport(sport);
		
		saveEventually();
		return true;
	}
	
}
