package com.gameupapp;

import java.util.List;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

/**
 * TODO add temporal filtering (ie allow excluding past games)
 */
public class FilterBuilder {
	private ParseQuery<GameParse> query;
	private GameUpInterface gameup;
	
	public FilterBuilder() {
		query = ParseQuery.getQuery(GameParse.class);
		gameup = GameUpInterface.getInstance();
	}
	
	/**
	 * TODO: Assumes miles
	 * @param radius
	 * @param latitude
	 * @param longitude
	 */
	public void setRadius(double radius, double latitude, double longitude) {
		ParseQuery<GameParse> radQuery = gameup.getQueriesWithinMiles(
				radius, latitude, longitude);
		query.whereMatchesQuery("location", radQuery);
	}
	
	/**
	 * 
	 * @param level Takes the desired ability level (from 1-5)
	 */
	public void setAbilityLevel(int level) {
		assert(level <= 4);
		assert(level > 0);
		
		ParseQuery<GameParse> abilityQuery = gameup.getQueryWithAbility(level);
		query.whereMatchesQuery("abilityLevel", abilityQuery);
	}
	
	public void setSport(String sport) {
		ParseQuery<GameParse> sportQuery = gameup.getQueryWithSportName(sport);
		query.whereMatchesQuery("sport", sportQuery);
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A query constructed to match all desired parameters 
	 */
	public ParseQuery<GameParse> getQuery() {
		query.include("sport");
		return query;
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A list of all games matching the constructed query
	 */
	public List<GameParse> executeFilter() {
		query.include("sport");
		return gameup.filterGamesWithQuery(query);
		
	}
	
}
