package com.gameupapp;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.Activity;
import android.util.Log;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class GameUpInterface {
	
	/* set to true when location services are connected
	 * Defaults to true because of a really weird race condition that leads to
	 * GameAdapter's constructor seeing this as false and then its main methods
	 * seeing it as true; I don't even know how it can get called before it's set
	 * to true.
	*/
	public boolean CAN_CONNECT = false;
	public boolean SEEN_MAPS_ALERT = false;
	
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
	 * TODO filter on "isn't already in the past"
	 * @return The first 10 games in the DB
	 */
	public List<GameParse> getGames() {
		ParseQuery<GameParse> query = getGamesQuery();
		query.include("sport");
		// This is just a really, really general filter, so we can still use
		// filterGamesWithQuery
		
		gameList = filterGamesWithQuery(query);
		Log.d("getGames", "number of games: " + gameList.size());
		
		return gameList;
	}
	
	/**
	 * TODO filter on "isn't already in the past"
	 * @return The first 10 games in the DB
	 */
	public ParseQuery<GameParse> getGamesQuery() {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setLimit(10);
		
		return query;
	}
	
	public List<GameParse> getFutureGames() {
		ParseQuery<GameParse> query = getQueryOnFutureGames();
		query.setLimit(10);
		
		return filterGamesWithQuery(query);
	}
	
	/**
	 * 
	 * @param gameId Unique identifier for the desired game
	 * @return a GameParse object representing the desired game
	 */
	public GameParse getGame(String gameId) {
		GameParse game;
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		
		// We hit this too often not to use cache when we can.
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(30));
		
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
	 * 
	 * @param ability Ability level to filter with
	 * @return List of games at the given ability level
	 */
	public List<GameParse> getGamesWithAbility(int ability) {
		ParseQuery<GameParse> query = getQueryWithAbility(ability);
		query.include("sport");
		
		
		return filterGamesWithQuery(query);
	}
	
	/**
	 * 
	 * @param ability  Ability level to filter with
	 * @return Query on games matching the given ability level
	 */
	public ParseQuery<GameParse> getQueryWithAbility(int ability) {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.whereEqualTo("abilityLevel", ability);
		return query;
	}

	/**
	 * TODO Filter on "isn't already in the past"
	 * @param sportName Name of the sport to be selected
	 * @return A list with the first 10 games of that sport.
	 */
	public ParseQuery<GameParse> getQueryWithSportName(String sportName) {		
		sportName = sportName.toLowerCase(Locale.US);
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
		
		return gameQuery;
	}
	
	/**
	 * 
	 * TODO Filter on "isn't already in the past"
	 * @param sportName Name of the sport to be selected
	 * @return A query on the first 10 games of that sport.
	 */
	public List<GameParse> getGamesWithSportName(String sportName) {
		List<GameParse> games;
		ParseQuery<GameParse> gameQuery = getQueryWithSportName(sportName);
		gameQuery.include("sport");
		
		games = filterGamesWithQuery(gameQuery);
		return games;
	}
	
	/**
	 * 
	 * @param miles The radius, in miles, within which we look
	 * @param latitude User's current latitude
	 * @param longitude User's current longitude
	 * @return List of games within the given geographic area
	 */
	public List<GameParse> getGamesWithinMiles(double miles, double latitude, 
			double longitude) {
		List<GameParse> games;
		ParseQuery<GameParse> query = getQueriesWithinMiles(miles, 
				latitude, longitude);
		query.include("sport");
		
		games = filterGamesWithQuery(query);
		return games;
	}
	
	/**
	 * 
	 * @param miles The radius, in miles, within which we look
	 * @param latitude User's current latitude 
	 * @param longitude User's current longitude
	 * @return Query for games within the given geographic area
	 */
	public ParseQuery<GameParse> getQueriesWithinMiles(double miles, 
			double latitude, double longitude) {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.include("sport");
		ParseGeoPoint currentLocation = new ParseGeoPoint(latitude, longitude);
		query.whereWithinMiles("location", currentLocation, miles);
		
		return query;
	}
	
	/**
	 * 
	 * @param kilometers The radius, in kilometers, within which we look
	 * @param latitude User's current latitude
	 * @param longitude User's current longitude
	 * @return List of games within the given geographical area
	 */
	public List<GameParse> getGamesWithinKilometers(double kilometers, 
			double latitude, double longitude) {
		List<GameParse> games;
		ParseQuery<GameParse> query = getQueriesWithinKilometers(kilometers, 
				latitude, longitude);
		query.include("sport");
		
		games = filterGamesWithQuery(query);
		return games;

	}
	
	
	/**
	 * 
	 * @param kilometers The radius, in kilometers, within which we look
	 * @param latitude User's current latitude 
	 * @param longitude User's current longitude
	 * @return Query for games within the given geographic area
	 */
	public ParseQuery<GameParse> getQueriesWithinKilometers(double kilometers, 
			double latitude, double longitude) {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		query.include("sport");
		ParseGeoPoint currentLocation = new ParseGeoPoint(latitude, longitude);
		query.whereWithinKilometers("location", currentLocation, kilometers);
		
		return query;
	}
	
	
	/**
	 * 
	 * @param query The query to be filtered on
	 * @return A list exactly matching the filter
	 */
	public List<GameParse> filterGamesWithQuery(ParseQuery<GameParse> query) {
		query.whereEqualTo("isDebugGame", AppConstant.DEBUG);
		
		try {
			return query.find();
		} catch (ParseException e) {
			Log.e("filterGames", "Find failed", e);
			return null;
		}
	}
	
	public List<Sport> getAllSports() {
		ParseQuery<Sport> query = getAllSportsQuery();
		
		try {
			return query.find();
		} catch (ParseException e) {
			Log.e("getAllSports", "Find failed", e);
			return null;
		}
	}
	
	public ParseQuery<Sport> getAllSportsQuery() {
		ParseQuery<Sport> query = ParseQuery.getQuery(Sport.class);
		
		// The sports list is essentially static, so we can make it mostly
		// just use cache
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(TimeUnit.DAYS.toMillis(7));
		
		query.whereExists("sport");
		return query;
	}
	
	/**
	 * This method deletes all data on a user everywhere in our database.
	 * TODO Audit this code
	 * After this function returns, the user object will be invalid and CANNOT
	 * SAFELY BE ACCESSED.
	 * @param user The user to be deleted
	 * @throws JSONException Thrown if we can't parse a gameId at some point
	 * @throws ParseException Thrown either if removeUser fails on one of the
	 * user's games, or if deleting the user fails
	 */
	public void removeUserFromApp(ParseUser user) throws JSONException, ParseException {
		JSONArray gameIds = user.getJSONArray("listOfGames");
		if (gameIds != null) {
			for(int i = 0; i < gameIds.length(); i++) {
				String gameId = gameIds.getString(i);
				GameParse game = getGame(gameId);
				boolean removed = game.removePlayer();
				if(!removed) {
					ParseException e = new ParseException(
							ParseException.OTHER_CAUSE,"Removing user from game with "
									+ "id " + gameId + " failed.");
					throw(e);
				}
			}
		}
		ParseFacebookUtils.unlink(user);
		ParseUser.logOut();
		user.delete();
	}
	
	public boolean createGame(Date startDate, Date endDate, int abilityLevel,
			int playerCount, String readableLocation, double latitude,
			double longitude, String sport) {
		GameParse game = new GameParse(); 
		return game.createGame(startDate, endDate, abilityLevel, playerCount,
				readableLocation, latitude, longitude, sport);
	}
	
	/**
	 * 
	 * @param gameId The ID of the game to be found
	 * @return An ImmutablePair (tuple) of latitude, longitude.
	 */
	public ImmutablePair<Double, Double> getLatLongOfGame(String gameId) {
		GameParse game = getGame(gameId);
		ParseGeoPoint location = game.getLocation();
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		return new ImmutablePair<Double, Double>(latitude, longitude);
	}
	

	/**
	 * 
	 * TODO Assumes miles
	 * @param latitude Latitude of current user location
	 * @param longitude Longitude of current user location
	 * @param gameId ID of game to be looked up
	 * @return The distance (in miles) between current location and game location
	 */
	public double getDistanceBetweenLocationAndGame(double latitude, 
			double longitude, String gameId) {
		GameParse game = getGame(gameId);
		ParseGeoPoint gameLocation = game.getLocation();
		ParseGeoPoint location = new ParseGeoPoint(latitude, longitude);
		
		return gameLocation.distanceInMilesTo(location);
		
	}
	
	/**
	 * 
	 * @return A query matching all games starting in the future
	 */
	public ParseQuery<GameParse> getQueryOnFutureGames() {
		ParseQuery<GameParse> query = ParseQuery.getQuery(GameParse.class);
		Date currentDate = new Date(System.currentTimeMillis());
		query.whereGreaterThan("startDateTime", currentDate);
		
		return query;
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