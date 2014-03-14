package com.gameupapp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	public void decrementCurrentPlayerCount() {
		increment("currentPlayerCount", -1);
	}
	
	/**
	 * 
	 * @param sport The name of the sport to be added
	 * @throws ParseException Throws an exception if the sport could not be found.
	 */
	public void setSport(String sport) throws ParseException {
		// Sport must be lowercased
		sport = sport.toLowerCase(Locale.US);
		ParseQuery<Sport> sportQuery = ParseQuery.getQuery(Sport.class);
		sportQuery.whereEqualTo("sport", sport);
		Sport parseSport;
		parseSport = sportQuery.getFirst();
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

	/**
	 * TODO Throw error up to UI
	 * @return True on successful add. False if the player had already joined,
	 * 				the game is full, or the save fails.
	 */
	public boolean addPlayer() {
		if(checkPlayerJoined()) {
			return false;
		}
		
		try {
			refresh();
		} catch (ParseException e) {
			Log.d("addPlayer", "Couldn't refresh game", e);
		}
		
		int maxCount = getMaxPlayerCount();
		int currentCount = getCurrentPlayerCount();
		if(currentCount < maxCount) {
			// Update game
			incrementCurrentPlayerCount();
			ParseUser currentUser = ParseUser.getCurrentUser();
			addUnique("Users", currentUser.getObjectId());
			
			// Update user
			currentUser.addUnique("listOfGames", getObjectId());
			try {
				/** TODO handle possible inconsistent state if one save
				 * succeeds but the other fails.
				 */
				save();
				currentUser.save();
				return true;
			} catch (ParseException e) {
				Log.e("addPlayer", "Failed to add player", e);
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Remove a player from the game
	 * @return True if the player was successfully removed. False if the remove 
	 * 		   		failed or player hadn't joined.
	 */
	public boolean removePlayer() {
		if(checkPlayerJoined()) {
			// Update game
			ParseUser currentUser = ParseUser.getCurrentUser();
			List<String> currentUserList = new ArrayList<String>();
			currentUserList.add(currentUser.getObjectId());
			removeAll("Users", currentUserList);
			decrementCurrentPlayerCount();
			
			// Update user
			List<String> game = new ArrayList<String>();
			game.add(getObjectId());
			currentUser.removeAll("listOofGames", game);
			
			try {
				/** TODO handle possible inconsistent state if one save
				 * succeeds but the other fails.
				 */
				save();
				currentUser.save();
				return true;
			} catch (ParseException e) {
				Log.e("removePlayer", "Failed to fuly remove player", e);
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the current user has already joined a game
	 * @return true if the user has joined. False otherwise (including on error).
	 */
	public boolean checkPlayerJoined() { 
		JSONArray joinedPlayers = getJSONArray("Users");
		if (joinedPlayers == null) {
			// We have no players
			return false;
		}
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		
		// Anonymous users can't have joined 
		if(currentUser == null) {
			return false;
		}
		String currentUID = currentUser.getObjectId();
		for (int i = 0; i < joinedPlayers.length(); i++) {
			String candidateUID;
			try {
				candidateUID = joinedPlayers.getString(i);
			} catch (JSONException e) {
				Log.e("checkPlayerJoined", "Couldn't parse array of users", e);
				return false;
			}
			if (candidateUID.equals(currentUID)) {
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
	 * @param playerCount Maximum number of players for a game (must be >0)
	 * @param readableLocation A human-readable (ie address, place name) location
	 * @param latitude Latitude of game 
	 * @param longitude Longitude of game
	 * @param sport Name of sport
	 * @return True on successful creation, false on error or validation problem.
	 */
	public boolean createGame(Date startDate, Date endDate, int abilityLevel,
			int playerCount, String readableLocation, double latitude,
			double longitude, String sport) {
		
		assert(abilityLevel <= 4);
		assert(abilityLevel > 0);
		assert(playerCount > 0);
		
		// start date must come before end date
		if(endDate.before(startDate)) {
			return false;
		}
		
		setStartDateTime(startDate);
		setEndDateTime(endDate);
		setAbilityLevel(abilityLevel);
		setMaxPlayerCount(playerCount);
		setLocation(latitude,longitude);
		setReadableLocation(readableLocation);
		setDebug();
		
		try {
			setSport(sport);
		} catch (ParseException e1) {
			Log.e("createGame", "Invalid sport. Deleting object.", e1);
			deleteEventually();
			return false;
		}
		
		try {
			save();
		} catch (ParseException e) {
			Log.e("createGame", "Couldn't save game", e);
		}
		
		// And automatically add the player who created the game
		// TODO: Figure out what to do if add fails, but create works
		addPlayer();
		return true;
	}
	
	public boolean getDebug() {
		return getBoolean("isDebugGame");
	}
	
	public void setDebug() {
		put("isDebugGame", AppConstant.DEBUG);
	}
	
}
