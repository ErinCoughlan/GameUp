package com.gameupapp;
import java.util.Date;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

@ParseClassName("GameParse")
public class GameParse extends ParseObject {
	
	public String getGameID() {
		return getString("objectID");
	}
	
	/**
	 * 
	 * @param latitude Double representing latitude, -90.0 to 90.0
	 * @param longitude Double representing longitude, -180.0 to 180.0
	 */
	public void setLocation(double latitude, double longitude) {
		ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);
		put("location", location);
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
	}
	
	public Date getStartDateTime() {
		return (Date) get("startDateTime");
	}
	
	public void setStartDateTime(Date dateTime) {
		put("startDateTime", dateTime);
	}
	
	public Date getEndDateTime() {
		return (Date) get("endDateTime");
	}
	
	public void setEndDateTime(Date dateTime) {
		put("endDateTime", dateTime);
	}
	
	public void setMaxPlayerCount(int maxCount) {
		put("maxPlayerCount", maxCount);
	}
	
	public int getMaxPlayerCount() {
		return getInt("maxPlayerCount");
	}
	
	public int getCurrentPlayerCount() { 
		return getInt("currentPlayerCount");
	}
	
	public void setCurrentPlayerCount(int newCount) {
		put("currentPlayerCount", newCount);
	}
	
	// TODO this should take in a player account or something
	public boolean addPlayer() {
		int maxCount = getMaxPlayerCount();
		int currentCount = getCurrentPlayerCount();
		if(currentCount < maxCount) {
			setCurrentPlayerCount(currentCount + 1);
			// TODO add the player to the list
			return true;
		}
		
		return false;
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
	}
	
	
	public String getSport() {
		Sport parseSport = (Sport) get("sport");
		return parseSport.getName();
	}
	
	public String getReadableLocation() {
		return getString("readableLocation");
	}
	
	public void setReadableLocation(String location) {
		put("readableLocation", location);
	}
	
	public int getAbilityLevel() {
		return getInt("abilityLevel");
	}
	
	public void setAbilityLevel(int level) {
		put("abilityLevel", level);
	}
}
