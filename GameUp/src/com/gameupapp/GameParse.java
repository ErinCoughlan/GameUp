package com.gameupapp;
import java.util.Date;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;

@ParseClassName("GameParse")
public class GameParse extends ParseObject {
	
	public String getGameID() {
		return getString("gameID");
	}
	
	public void setGameID(String gameID) {
		put("gameID", gameID);
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
	
	// TODO figure out relevant type returned by get("dateTime");
	//		I don't know if this cast is legit
	public Date getDateTime() {
		return (Date) get("dateTime"); 
	}
	
	// TODO figure out typing 
	public void setDateTime(Date dateTime) {
		put("dateTime", dateTime);
	}
	
	public void setMaxPlayers(int maxCount) {
		put("maxPlayers", maxCount);
	}
	
	public int getMaxPlayers() {
		return getInt("maxPlayers");
	}
	
	public int getCurrentPlayerCount() { 
		return getInt("currentPlayerCount");
	}
	
	public void setCurrentPlayerCount(int newCount) {
		put("currentPlayerCount", newCount);
	}
	
	// TODO this should take in a player account or something
	public boolean addPlayer() {
		int maxCount = getMaxPlayers();
		int currentCount = getCurrentPlayerCount();
		if(currentCount < maxCount) {
			setCurrentPlayerCount(currentCount + 1);
			// TODO add the player to the list
			return true;
		}
		
		return false;
	}
	
	
}
