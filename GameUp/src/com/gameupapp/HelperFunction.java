package com.gameupapp;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseGeoPoint;

public class HelperFunction {
	
	/**
	 * Finds the id of a resource given its string name, class, and context.
	 * Returns -1 if no resource is found.
	 * 
	 * Usage: getResId("icon", context, Drawable.class);
	 */
	public static int getResId(String variableName, Context context, Class<?> c) {
	    try {
	        Field idField = c.getDeclaredField(variableName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        return -1;
	    } 
	}
	
	/**
	 * Converts a unix timestamp to a localized date time.
	 */
	public static String convertToDate(long time){
		time = time * 1000;
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("EEE., MMM d '\n'h:mm a", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());
		String formatted = format.format(date);
		return formatted;
	}
	
	public static String convertToDate(Date date) {
		DateFormat format = new SimpleDateFormat("EEE., MMM d '\n'h:mm a", Locale.getDefault());
		String outDate = format.format(date);
		return outDate;
	}
	
	public static String convertToDate(Date date1, Date date2) {
		DateFormat format = new SimpleDateFormat("EEE., MMM d h:mm a", Locale.getDefault());
		DateFormat shortFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
		String startDate = format.format(date1);
		String endDate = shortFormat.format(date2);
		return startDate + " - \n" + endDate;
	}
	
	public static String convertParseGeoToString(ParseGeoPoint location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		String locationStr = latitude + ", " + longitude;
		return locationStr;
	}

}
