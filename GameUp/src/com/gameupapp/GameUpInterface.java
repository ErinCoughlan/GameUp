package com.gameupapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class GameUpInterface {
	private List<Game> gameList;
	private Collection<Activity> observers;
	
	// Information for database/server
	private String BASE_URL = "http://134.173.43.64:5000";
	
	private String USER_ID;
	
	// Filtering
	private int orderBy = 0;
	private int numComments = 20;

	private static GameUpInterface instance;
	//private HttpTaskProvider httpTaskProvider;
	
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
	 * Private constructor of a new PodiumInterface instance.
	 * 
	 * @param user_id	The unique ID of the user connecting to GameUp
	 */
	private GameUpInterface(String user_id) {
			
		USER_ID = user_id;
		observers = new HashSet<Activity>();
		//httpTaskProvider = new HttpTaskProvider();
		
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
	public static GameUpInterface getInstance(String user_id) {
		if (instance == null) {
			instance = new GameUpInterface(user_id);
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
	
	public List<Game> getGames() {
		// TODO Make this use the API, whenever that gets done
		List<Game> games = new ArrayList<Game>();
		
		if (AppConstant.DEBUG) {
			Game g = new Game(jsonGame);
			games.add(g);
			g = new Game(jsonGame2);
			games.add(g);
			g = new Game(jsonGame3);
			games.add(g);
			g = new Game(jsonGame4);
			games.add(g);
			g = new Game(jsonGame5);
			games.add(g);
			g = new Game(jsonGame6);
			games.add(g);
			
			gameList = games;
		}
		
		Log.d("games", "number of games: " + games.size());
		
		return games;
	}
	
	public Game getGame(String gameId) {
		// TODO Make this use the API, whenever that gets done
		if (AppConstant.DEBUG) {
			for (Game g : gameList) {
				if (g.getGameId().equals(gameId)) {
					return g;
				}
			}
		}
		// Game was not found; return an error
		return null;
	}
}