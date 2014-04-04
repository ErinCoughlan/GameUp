package com.gameupapp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;

/**
 * TODO add temporal filtering (ie allow excluding past games)
 */
public class FilterBuilder {
	private ParseQuery<GameParse> query;
	private GameUpInterface gameup;
	private boolean refresh;
	
	public FilterBuilder() {
		query = ParseQuery.getQuery("GameParse");
		query.setMaxCacheAge(TimeUnit.MINUTES.toMillis(5));
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		gameup = GameUpInterface.getInstance();
	}
	
	/**
	 * TODO: Assumes miles
	 * @param radius
	 * @param latitude
	 * @param longitude
	 */
	public FilterBuilder setRadius(double radius, double latitude, double longitude) {
		ParseQuery<GameParse> radQuery = gameup.getQueriesWithinMiles(
				radius, latitude, longitude);
		query.whereMatchesKeyInQuery("objectId", "objectId", radQuery);
		return this;
	}
	
	/**
	 * 
	 * @param level Takes the desired ability level (from 1-4)
	 */
	public FilterBuilder setAbilityLevel(int level) {
		assert(level < AppConstant.ABILITY_LEVELS.size());
		assert(level >= 0);
		
		ParseQuery<GameParse> abilityQuery = gameup.getQueryWithAbility(level);
		query.whereMatchesKeyInQuery("abilityLevel", "abilityLevel", abilityQuery);
		return this;
	}
	
	/**
	 * 
	 * @param sport The name of the desired sport
	 */
	public FilterBuilder setSport(String sport) {
		ParseQuery<GameParse> sportQuery = gameup.getQueryWithSportName(sport);
		query.whereMatchesKeyInQuery("sport", "sport", sportQuery);
		return this;
	}
	
	/**
	 * Filters out all games that have already happened
	 */
	public FilterBuilder filterIntoFuture() {
		ParseQuery<GameParse> gameQuery = gameup.getQueryOnFutureGames();
		query.whereMatchesKeyInQuery("objectId", "objectId", gameQuery);
		return this;
	}
	
	public FilterBuilder addSortAscendingOnKey(String key) {
		query.addAscendingOrder(key);
		return this;
	}
	
	public FilterBuilder addSortDescendingOnKey(String key) {
		query.addDescendingOrder(key);
		return this;
	}
	
	public FilterBuilder exclusivelySortAscendingOnKey(String key) {
		query.orderByAscending(key);
		return this;
	}
	
	public FilterBuilder exclusivelySortDescendingOnKey(String key) {
		query.orderByDescending(key);
		return this;
	}
	
	/**
	 * 
	 * @param n How many games to skip
	 */
	public FilterBuilder getMoreGames(int n) {
		query.setSkip(n);
		return this;
	}
	
	/**
	 * 
	 * @param n How many results the query should return
	 */
	public FilterBuilder setQuerySize(int n) {
		query.setLimit(n);
		return this;
	}
	
	/**
	 * 
	 * @param cacheLength Time (in milliseconds) to remain cached
	 */
	public FilterBuilder setCached(long cacheLength) {
		query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
		query.setMaxCacheAge(cacheLength);
		return this;
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A query constructed to match all desired parameters 
	 */
	public ParseQuery<GameParse> build() {
		query.include("sport");
		return query;
	}
	
	public FilterBuilder shouldRefresh(boolean shouldRefresh) {
		refresh = shouldRefresh;
		return this;
	}
	
	/**
	 * TODO I'm not sure if we actually always need to include sport?
	 * @return A list of all games matching the constructed query
	 */
	public List<GameParse> execute() {
		query.include("sport");
		if (refresh) {
			query.clearCachedResult();
		}	
		
		return gameup.filterGamesWithQuery(query);
		
	}
}
