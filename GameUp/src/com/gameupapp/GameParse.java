package com.gameupapp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("GameParse")
public class GameParse extends ParseObject {
	
	public String getGameId() {
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
	
	public boolean setStartDateTime(Date dateTime) {
		put("startDateTime", dateTime);		
		return true;
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
	
	public void incrementCurrentPlayerCount() {
		increment("currentPlayerCount");
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
	}
	
	public int getAbilityLevel() {
		return getInt("abilityLevel");
	}
	
	public void setAbilityLevel(int level) {
		put("abilityLevel", level);
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
			addUnique("Users", currentUser.getObjectId());
			
			try {
				save();
				return true;
			} catch (ParseException e) {
				Log.e("addPlayer", "Failed to add player", e);
				return false;
			}
		}
		
		return false;
	}
	
	public boolean removePlayer() {
		List<String> currentUser = new ArrayList<String>();
		currentUser.add(ParseUser.getCurrentUser().getObjectId());
		removeAll("Users", currentUser);
		// TODO decrement currentPlayerCount
		
		try {
			save();
			return true;
		} catch (ParseException e) {
			Log.e("removePlayer", "Failed to remove player", e);
			return false;
		}
	}
	
	/**
	 * Checks if the current user has already joined a game
	 * @return true if the user has joined. False otherwise (including on error).
	 */
	public boolean checkPlayerJoined() { 
		JSONArray joinedPlayers = getJSONArray("Users");
		ParseUser currentUser = ParseUser.getCurrentUser();
		String currentUID = currentUser.getObjectId();
		for(int i = 0; i < joinedPlayers.length(); i++) {
			String candidateUID;
			 
			try {
				candidateUID = joinedPlayers.getString(i);
			} catch (JSONException e) {
				Log.e("checkPlayerJoined", "Couldn't parse array of users", e);
				return false;
			}
			if(candidateUID == currentUID) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param startDate Start date/time of game
	 * @param endDate End date/time of game (must be after startDate)
	 * @param abilityLevel Level of players expected in this game (1-4)
	 * @param readableLocation A human-readable (ie address, place name) location
	 * @param latitude Latitude of game 
	 * @param longitude Longitude of game
	 * @param sport Name of sport
	 * @return True on successful creation, false on error or validation problem.
	 */
	public boolean createGame(Date startDate, Date endDate, int abilityLevel,
			String readableLocation, double latitude, double longitude, 
			String sport) {
		
		assert(abilityLevel < 4);
		assert(abilityLevel >0);
		
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
		
		try {
			save();
		} catch (ParseException e) {
			Log.e("createGame", "Couldn't save game", e);
		}
		return true;
	}
	
}
