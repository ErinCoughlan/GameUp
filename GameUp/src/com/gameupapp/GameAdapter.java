package com.gameupapp;

import java.util.List;
import java.util.Locale;

import com.gameupapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<GameParse> {

	private List<GameParse> objects;
	private int layout;
	private Context context;

	/* here we must override the constructor for ArrayAdapter
	 * the only variable we care about now is ArrayList<Item> objects,
	 * because it is the list of objects we want to display.
	 */
	public GameAdapter(Context context, int textViewResourceId, List<GameParse> gamesList) {
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
		GameParse i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.
			
			TextView timestamp = (TextView) v.findViewById(R.id.gameTimestamp);
			TextView location = (TextView) v.findViewById(R.id.gameLocation);
			TextView sport = (TextView) v.findViewById(R.id.gameSport);
			ImageView sportIcon = (ImageView) v.findViewById(R.id.gameSportIcon);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (timestamp != null){
				String date = HelperFunction.convertToDate(i.getStartDateTime());
				timestamp.setText(date);
			}
			
			if (location != null){
				//String locationString = HelperFunction.convertParseGeoToString(i.getLocation());
				//location.setText(locationString);
				location.setText("0.1 miles away");
			}
			
			
			if (sport != null){
				sport.setText(i.getSport());
			}
			
			if (sportIcon != null){
				String s = i.getSport().toLowerCase(Locale.US);
				int id = HelperFunction.getResId(s, context, R.drawable.class);
				if (id != -1) {
					sportIcon.setBackgroundResource(id);
				} else {
					sportIcon.setBackgroundResource(AppConstant.UNKNOWN_IMG);
				}
			}
		}

		// the view must be returned to our activity
		return v;
	}

	public List<GameParse> getGames(){
		return this.objects;
	}

	public int getPosition(GameParse game){
		return this.objects.indexOf(game);
	}
}