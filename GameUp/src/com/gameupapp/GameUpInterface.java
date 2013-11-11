package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class GameUpInterface {
	private List<Activity> observers = new ArrayList<Activity>();
	
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
			"'id': 'fh4i09qp1'," +
			"'location': 'Parents Field'," +
			"'total_players': '30'," +
			"'players_joined': '18'}";
	
	private String gameString3 = "{'body': 'Hello this is a game'," +
			"'sport': 'Tennis'," +
			"'timestamp': '1384624800'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'fh4i09qp1'," +
			"'location': 'Biszantz Tennis Center'," +
			"'total_players': '4'," +
			"'players_joined': '3'}";
	
	private String gameString4 = "{'body': 'Hello this is a game'," +
			"'sport': 'Volleyball'," +
			"'timestamp': '1384662600'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'fh4i09qp1'," +
			"'location': 'Pitzer Mounds'," +
			"'total_players': '8'," +
			"'players_joined': '2'}";
	
	private String gameString5 = "{'body': 'Hello this is a game'," +
			"'sport': 'Baseball'," +
			"'timestamp': '1384720200'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'fh4i09qp1'," +
			"'location': 'Pritzlaff Field'," +
			"'total_players': '28'," +
			"'players_joined': '12'}";
	
	private String gameString6 = "{'body': 'Hello this is a game'," +
			"'sport': 'Soccer'," +
			"'timestamp': '1384797600'," +
			"'game_time': 'Monday, Nov. 13'," +
			"'id': 'fh4i09qp1'," +
			"'location': 'Parents Field'," +
			"'total_players': '20'," +
			"'players_joined': '3'}";
	
	private JSONObject jsonGame;
	private JSONObject jsonGame2;
	private JSONObject jsonGame3;
	private JSONObject jsonGame4;
	private JSONObject jsonGame5;
	private JSONObject jsonGame6;
	private String USER_ID;
	
	private static GameUpInterface instance;
	
	public GameUpInterface(String user_id) {
		USER_ID = user_id;
		
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
	
	public static GameUpInterface getInstance(String user_id) {
		if (instance == null) {
			instance = new GameUpInterface(user_id);
		}
		return instance;
	}
	
	public void registerObserver(Activity ob) {
		observers.add(ob);
	}
	
	public void removeObserver(Activity remOb) {
		for (Activity ob: observers) {
			if (ob == remOb) {
				observers.remove(remOb);
			}
		}
	}
	
	public void removeAllObservers() {
		observers = new ArrayList<Activity>();
	}
	
	public List<Game> getGames() {
		// TODO Make this use the API, whenever that gets done
		List<Game> games = new ArrayList<Game>();
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
		
		Log.d("games", "number of games: " + games.size());
		
		return games;
	}
}