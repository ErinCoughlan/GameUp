package com.gameupapp;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.gameupapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<Game> {

	// declaring our ArrayList of items
	private List<Game> objects;
	private int layout;
	private Context context;

	/* here we must override the constructor for ArrayAdapter
	 * the only variable we care about now is ArrayList<Item> objects,
	 * because it is the list of objects we want to display.
	 */
	public GameAdapter(Context context, int textViewResourceId, List<Game> gamesList) {
		super(context, textViewResourceId, gamesList);
		this.objects = gamesList;
		this.layout = textViewResourceId;
	}

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			context = getContext();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(layout, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		Game i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView timestamp = (TextView) v.findViewById(R.id.gameTimestamp);
			TextView location = (TextView) v.findViewById(R.id.gameLocation);
			TextView players = (TextView) v.findViewById(R.id.gamePlayers);
			TextView sport = (TextView) v.findViewById(R.id.gameSport);
			ImageView sportIcon = (ImageView) v.findViewById(R.id.gameSportIcon);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (timestamp != null){
				String date = convertToDate(i.getTimestamp());
				timestamp.setText(date);
			}
			
			if (location != null){
				location.setText(i.getLocation());
			}
			
			if (players != null){
				String str = i.getPlayersJoined() + " out of " + i.getTotalPlayers();
				players.setText(str);
			}
			
			if (sport != null){
				sport.setText(i.getSport());
			}
			
			if (sportIcon != null){
				String s = i.getSport().toLowerCase(Locale.US);
				int id = getResId(s, context, R.drawable.class);
				if (id != -1) {
					sportIcon.setBackgroundResource(id);
				}
			}
		}

		// the view must be returned to our activity
		return v;
	}

	public List<Game> getGames(){
		return this.objects;
	}

	public int getPosition(Game game){
		return this.objects.indexOf(game);
	}

	public static String convertToDate(long time){
		time = time * 1000;
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("EEE., MMM d '\n@' h:mm a", Locale.getDefault());
		format.setTimeZone(TimeZone.getDefault());
		String formatted = format.format(date);
		return formatted;
	}
	
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