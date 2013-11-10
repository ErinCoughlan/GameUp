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
							"'timestamp': '1243567890'," +
							"'game_time': 'Monday, Nov. 13'," +
							"'id': 'fh4i09qp1'," +
							"'location': 'Ducey Gym'," +
							"'total_players': '10'," +
							"'players_joined': '7'}";
	
	private JSONObject jsonGame;
	private String USER_ID;
	
	private static GameUpInterface instance;
	
	public GameUpInterface(String user_id) {
		USER_ID = user_id;
		
		try {
			jsonGame = new JSONObject(gameString);
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
		for (int i=0; i<10; i++) {
			games.add(g);
		}
		
		return games;
	}
}