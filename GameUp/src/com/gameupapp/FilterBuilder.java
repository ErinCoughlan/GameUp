package com.gameupapp;

import java.util.List;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

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
		assert(level < 4);
		assert(level >= 0);
		
		ParseQuery<GameParse> abilityQuery = gameup.getQueryWithAbility(level);
		query.whereMatchesQuery("abilityLevel", abilityQuery);
	}
	
	/**
	 * 
	 * @param sport The name of the desired sport
	 */
	public void setSport(String sport) {
		ParseQuery<GameParse> sportQuery = gameup.getQueryWithSportName(sport);
		query.whereMatchesQuery("sport", sportQuery);
	}
	
	/**
	 * Filters out all games that have already happened
	 */
	public void filterIntoFuture() {
		ParseQuery<GameParse> temporalFilter = gameup.getQueryOnFutureGames();
		query.whereMatchesQuery("startDateTime", temporalFilter);
	}
	
	public void addSortAscendingOnKey(String key) {
		query.addAscendingOrder(key);
	}
	
	public void addSortDescendingOnKey(String key) {
		query.addDescendingOrder(key);
	}
	
	public void exclusivelySortAscendingOnKey(String key) {
		query.orderByAscending(key);
	}
	
	public void exclusivelySortDescendingOnKey(String key) {
		query.orderByDescending(key);
	}
	
	/**
	 * 
	 * @param n How many games to skip
	 */
	public void getMoreGames(int n) {
		query.setSkip(n);
	}
	
	/**
	 * 
	 * @param n How many results the query should return
	 */
	public void setQuerySize(int n) {
		query.setLimit(n);
	}
	
	/**
	 * 
	 * @param cacheLength Time (in milliseconds) to remain cached
	 */
	public void setCached(long cacheLength) {
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(cacheLength);
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
