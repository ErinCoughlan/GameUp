package com.gameupapp;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Venue {
	private String readableLocation;
	private String name;
	private ImmutablePair<Double, Double> location;
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param place
	 * @param aName
	 */
	public Venue(double latitude, double longitude, String place, String aName) {
		location = new ImmutablePair<Double, Double>(latitude, longitude);
		readableLocation = place;
		name = aName;
	}
	
	public String getReadableLocation() {
		return readableLocation; 
	}
	
	public ImmutablePair<Double, Double> getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
}
