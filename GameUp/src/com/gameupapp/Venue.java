package com.gameupapp;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Venue")
public class Venue extends ParseObject {
		public String getReadableLocation() {
		return getString("readableLocation"); 
	}
	
	public void setReadableLocation(String readableLocation) {
		put("readableLocation", readableLocation);
		saveEventually();
	}
	
	public void setLocation(ImmutablePair<Double, Double> location) {
		ParseGeoPoint geoLocation = new ParseGeoPoint(location.left, location.right);
		put("location", geoLocation);
		saveEventually();
	}
	
	public ImmutablePair<Double, Double> getLocation() {
		ParseGeoPoint location = getParseGeoPoint("location");
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		ImmutablePair<Double, Double> latLong = 
				new ImmutablePair<Double, Double>(latitude, longitude);
		return latLong;
	}
	
	public String getName() {
		return getString("name");
	}
	
	public void setName(String name) {
		put("name", name);
		saveEventually();
	}
}
