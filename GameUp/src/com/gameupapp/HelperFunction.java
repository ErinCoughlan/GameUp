package com.gameupapp;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
	
	public static String convertToDateSimple(Date date) {
		DateFormat format = new SimpleDateFormat("EEE, MMM. d '\n'h:mm a", Locale.getDefault());
		return format.format(date);
	}
	
	public static String convertToDate(Date date) {
		DateFormat format = new SimpleDateFormat("EEE, MMM. d '\n'h:mm a", Locale.getDefault());
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    Calendar today = Calendar.getInstance();
	    Calendar tomorrow = Calendar.getInstance();
	    tomorrow.add(Calendar.DATE, 1);
	    DateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());

	    if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
	    		calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
	        return "Today \n" + timeFormatter.format(date);
	    } else if (calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
	    		calendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
	        return "Tomorrow \n" + timeFormatter.format(date);
	    } else {
	        return format.format(date);
	    }
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
	
	public static AlertDialog createGameAlert(int message, boolean success,
				final Activity a, final boolean loggedIn) {
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		
		// Get the layout inflater
	    LayoutInflater inflater = a.getLayoutInflater();
	    
	    View v = inflater.inflate(R.layout.success_dialog, null);
	    if (!success) {
	    	ImageView iv = (ImageView) v.findViewById(R.id.dialog_image);
	    	if (iv != null) {
	    		iv.setImageResource(R.drawable.failure);
	    		iv.setContentDescription(a.getResources().getString(R.string.access_failure));
	    	}
	    }
	    TextView tv = (TextView) v.findViewById(R.id.dialog_message);
	    if (tv != null) {
    		tv.setText(message);
    	}
	    
    	builder.setView(v)
    	       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Intent result = new Intent();
		        	   result.putExtra(AppConstant.LOGIN, loggedIn);
		       		   a.setResult(Activity.RESULT_OK, result);
		               a.finish();
		           }
    	});
    	
    	return builder.create();
	}

}
