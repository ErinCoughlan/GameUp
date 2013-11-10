package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Game {
	private String content;
	private String sport;
	private String gameId;
	private String location;
	// private List<String> users;
	private String gameTime;
	private long timestamp;
	private int totalPlayers;
	private int playersJoined;
	
	/**
	 * Creates a new Game instance from a JSON representation of a game.
	 * 
	 * @param json	the JSON representation of the game.
	 */
	public Game(JSONObject json) {
		try {
			content = json.getString("body");
			sport = json.getString("sport");
			timestamp = json.getLong("timestamp");
			gameTime = json.getString("game_time");
			gameId = json.getString("id");
			location = json.getString("location");
			totalPlayers = json.getInt("total_players");
			playersJoined = json.getInt("players_joined");
			
		} catch (JSONException e) {
			Log.d("json", "failed: trying to create game");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Translates a JSONArray into a List of Game instances.
	 * 
	 * @param json the JSON representation of a list of games
	 * @return a List of Game
	 * @throws JSONException
	 */
	// Factory method for getting an array of games
	public static List<Game> getGamesAsArray(JSONArray json) throws JSONException {
		List<Game> gameList = new ArrayList<Game>();
		if (json != null) {
			for (int i = 0; i < json.length(); i++){
				Game c = new Game(json.getJSONObject(i));
				gameList.add(c);
			}
		}
		return gameList;
	}
	
	public String getContent() {
		return this.content;
	}

	public String getLocation() {
		return this.location;
	}
	
	public String getSport() {
		return this.sport;
	}
	
	public String getGameTime() {
		return this.gameTime;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public String getGameId(){
		return this.gameId;
	}
	
	public int getTotalPlayers(){
		return this.totalPlayers;
	}
	
	public int getPlayersJoined(){
		return this.playersJoined;
	}
}
