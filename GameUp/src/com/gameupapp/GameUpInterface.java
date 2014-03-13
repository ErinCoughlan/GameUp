package com.gameupapp;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.app.Activity;
import android.util.Log;

public class GameUpInterface {
	private List<GameParse> gameList;
	private Collection<Activity> observers;

	private static GameUpInterface instance;
	
	private String gameString = "{'body': 'Hello this is a game'," +
							"'sport': 'Basketball'," +
							"'timestamp': '1384547400'," +
							"'game_time': 'Monday, Nov. 13'," +
							"'id': 'fh4i09qp1'," +
							"'location': 'Ducey Gym'," +
							"'total_players': '10'," +
							"'players_joined': '7'}";
	
	private String gameString2 = "{'body': 'Hello this is a game'," +
			"'sport': 'Soccer'," +
			"'timestamp': '1384558200'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'kb4i09qp1'," +
			"'location': 'Parents Field'," +
			"'total_players': '30'," +
			"'players_joined': '18'}";
	
	private String gameString3 = "{'body': 'Hello this is a game'," +
			"'sport': 'Tennis'," +
			"'timestamp': '1384624800'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'yt4i09qp1'," +
			"'location': 'Biszantz Tennis Center'," +
			"'total_players': '4'," +
			"'players_joined': '3'}";
	
	private String gameString4 = "{'body': 'Hello this is a game'," +
			"'sport': 'Volleyball'," +
			"'timestamp': '1384662600'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'ps4i09qp1'," +
			"'location': 'Pitzer Mounds'," +
			"'total_players': '8'," +
			"'players_joined': '2'}";
	
	private String gameString5 = "{'body': 'Hello this is a game'," +
			"'sport': 'Baseball'," +
			"'timestamp': '1384720200'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'pp4i09qp1'," +
			"'location': 'Pritzlaff Field'," +
			"'total_players': '28'," +
			"'players_joined': '12'}";
	
	private String gameString6 = "{'body': 'Hello this is a game'," +
			"'sport': 'Soccer'," +
			"'timestamp': '1384797600'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'wz4i09qp1'," +
			"'location': 'Parents Field'," +
			"'total_players': '20'," +
			"'players_joined': '3'}";
	
	private JSONObject jsonGame;
	private JSONObject jsonGame2;
	private JSONObject jsonGame3;
	private JSONObject jsonGame4;
	private JSONObject jsonGame5;
	private JSONObject jsonGame6;
	
	/**
	 * Private constructor of a new GameUpInterface instance.
	 * 
	 * @param user_id	The unique ID of the user connecting to GameUp
	 */
	private GameUpInterface() {
			
		observers = new HashSet<Activity>();
		
		try {
			jsonGame = new JSONObject(gameString);
			jsonGame2 = new JSONObject(gameString2);
			jsonGame3 = new JSONObject(gameString3);
			jsonGame4 = new JSONObject(gameString4);
			jsonGame5 = new JSONObject(gameString5);
			jsonGame6 = new JSONObject(gameString6);
		} catch (JSONException e) {
			Log.e("json", "you fail");
		}
	}

	/**
	 * Use this method instead of a constructor; it enforces the singleton design
	 * pattern.
	 * 
	 * @param user_id	The unique ID of the user connecting to GameUp
	 * @return	an instance of GameUpInterface
	 */
	public static GameUpInterface getInstance() {
		if (instance == null) {
			instance = new GameUpInterface();
		}
		return instance;
	}
	
	public void registerObserver(Activity observer) {
		observers.add(observer);
	}

	public void removeObserver(Activity observer) {
		observers.remove(observer);
	}
	
	public void removeAllObservers() {
		observers.clear();
	}
	
	/**
	 *  TODO filter on "isn't already in the past"
	 * @return The first 10 games in the DB
	 */
	public List<GameParse> getGames() {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setLimit(10);
		query.include("sport");
		// This is just a really, really general filter, so we can still use
		// filterGamesWithQuery
		gameList = filterGamesWithQuery(query);
		
		Log.d("getGames", "number of games: " + gameList.size());
		
		return gameList;
	}
	
	/**
	 * 
	 * @param gameId Unique identifier for the desired game
	 * @return a GameParse object representing the desired game
	 */
	public GameParse getGame(String gameId) {
		GameParse game;
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.include("sport");
		query.whereEqualTo("objectId", gameId);
		
		// Can't use filter method because we only want one.
		try {
			game = query.getFirst();
			return game;
		} catch(ParseException e) {
			Log.d("getGame", "lookup game with ID " + gameId +" failed");
		}
		
		// Game was not found; return an error
		return null;
	}

	/**
	 * TODO Filter on "isn't already in the past"
	 * @param sportName Name of the sport to be selected
	 * @return A list with the first 10 games of that sport.
	 */
	public List<GameParse> getGamesWithSportName(String sportName) {
		List<GameParse> games;
		ParseQuery<Sport> sportQuery = ParseQuery.getQuery(Sport.class);
		
		// This query should always return the same thing, so setting it to 
		// try cache first should be safe, as should a very long cache.
		sportQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		sportQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(14));
		
		sportQuery.whereEqualTo("sport", sportName);
		
		ParseQuery<GameParse> gameQuery = ParseQuery.getQuery(GameParse.class);
		gameQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		
		gameQuery.whereMatchesQuery("sport", sportQuery);
		gameQuery.setLimit(10);
		
		games = filterGamesWithQuery(gameQuery);
		return games;
	}
	
	/**
	 * 
	 * @param miles The radius, in miles, within which we look
	 * @param latitude User's current latitude
	 * @param longitude User's current longitude
	 * @return List of games within the given geographical area
	 */
	public List<GameParse> getGamesWithinMiles(double miles, double latitude, 
			double longitude) {
		List<GameParse> games;
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.include("sport");
		ParseGeoPoint currentLocation = new ParseGeoPoint(latitude, longitude);
		query.whereWithinMiles("location", currentLocation, miles);
		
		games = filterGamesWithQuery(query);
		return games;
	}
	
	/**
	 * 
	 * @param kilometers The radius, in miles, within which we look
	 * @param latitude User's current latitude
	 * @param longitude User's current longitude
	 * @return List of games within the given geographical area
	 */
	public List<GameParse> getGamesWithinKilometers(double kilometers, 
			double latitude, double longitude) {
		List<GameParse> games;
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.include("sport");
		ParseGeoPoint currentLocation = new ParseGeoPoint(latitude, longitude);
		query.whereWithinKilometers("location", currentLocation, kilometers);
		
		games = filterGamesWithQuery(query);
		return games;
	}
	
	/**
	 * 
	 * @param query The query to be filtered on
	 * @return A list exactly matching the filter
	 */
	private List<GameParse> filterGamesWithQuery(ParseQuery<GameParse> query) {
		query.whereEqualTo("isDebugGame", AppConstant.DEBUG);
		
		try {
			return query.find();
		} catch (ParseException e) {
			Log.e("filterGames", "Find failed", e);
			return null;
		}
	}
	
	public List<Sport> getAllSports() {
		ParseQuery<Sport> query = ParseQuery.getQuery(Sport.class);
		
		// The sports list is essentially static, so we can make it mostly
		// just use cache
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));
		
		query.whereExists("sport");
		try {
			return query.find();
		} catch (ParseException e) {
			Log.e("getAllSports", "Find failed", e);
			return null;
		}
	}
	
	public boolean postJoinGame(GameParse game) {
		return game.addPlayer();
	}
	
	public boolean postUnjoinGame(GameParse game) {
		return game.removePlayer();
	}
	
	public boolean checkPlayerJoined(GameParse game) {
		return game.checkPlayerJoined();
	}
}