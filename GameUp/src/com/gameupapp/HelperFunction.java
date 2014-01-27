package com.gameupapp;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;

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
	        e.printStackTrace();
	        return -1;
	    } 
	}
	
	/**
	 * Converts a unix timestamp to a localized date time.
	 */
	public static String convertToDate(long time){
		time = time * 1000;
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("EEE., MMM d '\n@' h:mm a", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());
		String formatted = format.format(date);
		return formatted;
	}

}
