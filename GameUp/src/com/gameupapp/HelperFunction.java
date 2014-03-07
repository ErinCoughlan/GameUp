package com.gameupapp;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
		DateFormat format = new SimpleDateFormat("EEE, MMM. d '\n'h:mm a", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());
		String formatted = format.format(date);
		return formatted;
	}
	
	public static String convertToDate(Date date) {
		DateFormat format = new SimpleDateFormat("EEE, MMM. d '\n'h:mm a", Locale.getDefault());
		String outDate = format.format(date);
		return outDate;
	}
	
	public static String convertToDate(Date date1, Date date2) {
		DateFormat format = new SimpleDateFormat("EEE, MMM. d h:mm a", Locale.getDefault());
		DateFormat dateOnlyFormat = new SimpleDateFormat("EEE, MMM. d '\n'", Locale.getDefault());
		DateFormat timeOnlyFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

		// Change string format based on whether the start and end time are the same day
		if (sameDate(date1, date2)) {
			String startDate = dateOnlyFormat.format(date1);
			String startTime = timeOnlyFormat.format(date1);
			String endTime = timeOnlyFormat.format(date2);
			return startDate + startTime + " - " + endTime;
		} else {
			String startDate = format.format(date1);
			String endDate = format.format(date2);
			return startDate + " - \n" + endDate;
		}
	}
	
	public static boolean sameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		return sameDay;
	}
	
	public static String convertParseGeoToString(ParseGeoPoint location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		String locationStr = latitude + ", " + longitude;
		return locationStr;
	}

}
