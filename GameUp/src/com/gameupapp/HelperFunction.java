package com.gameupapp;

import java.lang.reflect.Field;

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

}
