package com.gameupapp;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.gameupapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<GameParse> implements 
				GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener {

	private List<GameParse> objects;
	private int layout;
	private Context context;
	private LocationClient locationClient;
	private GameUpInterface gameup;
	private boolean PLAY_SERVICES = false;
	private double latitude = 0;
	private double longitude = 0;

	/* here we must override the constructor for ArrayAdapter
	 * the only variable we care about now is ArrayList<Item> objects,
	 * because it is the list of objects we want to display.
	 */
	public GameAdapter(Context context, int textViewResourceId, List<GameParse> gamesList) {
		super(context, textViewResourceId, gamesList);
		this.objects = gamesList;
		this.layout = textViewResourceId;
		this.context = context;
		
		this.gameup = GameUpInterface.getInstance();
		if (gameup.CAN_CONNECT) {
			Log.d("foo", "bar");
			PLAY_SERVICES = true;
			this.locationClient = new LocationClient(context, this, this);
			this.locationClient.connect();
		}
		
	}

	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		
		// assign the view we are converting to a local variable
		View v = convertView;
		if (PLAY_SERVICES) {
			Location location = locationClient.getLastLocation();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
		
		
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
			TextView textLocation = (TextView) v.findViewById(R.id.gameLocation);
			TextView sport = (TextView) v.findViewById(R.id.gameSport);
			ImageView sportIcon = (ImageView) v.findViewById(R.id.gameSportIcon);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (timestamp != null){
				String date = HelperFunction.convertToDate(i.getStartDateTime());
				timestamp.setText(date);
			}
			
			if (textLocation != null){
				//String locationString = HelperFunction.convertParseGeoToString(i.getLocation());
				//location.setText(locationString);
				
				if (gameup.CAN_CONNECT) {
					Object[] params = {i, textLocation};
					new SetDistanceText().execute(params);
				} else {
					textLocation.setText(i.getReadableLocation());
				}
			}
			
			
			if (sport != null){
				Object[] params = {i, sport};
				new SetSportText().execute(params);
			}
			
			if (sportIcon != null){
				// We need to save context because it can go out of scope
				Object[] params = {i, sportIcon, context};
				new SetSportIcon().execute(params);
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

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		PLAY_SERVICES = true;
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	private class SetDistanceText extends AsyncTask<Object, Integer, ImmutablePair<Double, Object>> {
		@Override
		protected ImmutablePair<Double, Object> doInBackground(Object... params) {
			double distance = 
					gameup.getDistanceBetweenLocationAndGame(latitude, 
							longitude, ((GameParse) params[0]).getGameId());
			
			ImmutablePair<Double, Object> toReturn = new ImmutablePair<Double, Object>(distance, params[1]);
			return toReturn;
		}
		
		@Override
		protected void onProgressUpdate(Integer...progress) {
			// TODO set progress percent here
		}
		
		@Override
		protected void onPostExecute(ImmutablePair<Double, Object> result) {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(1);
			
			((TextView) result.right).setText(df.format(result.left) + " mi. away");
		}
	}
	
	private class SetSportText extends AsyncTask<Object, Integer, ImmutablePair<String, Object>> {
		@Override
		protected ImmutablePair<String, Object> doInBackground(Object... params) {
			String sport = ((GameParse) params[0]).getSport();
			
			ImmutablePair<String, Object> toReturn = new ImmutablePair<String, Object>(sport, params[1]);
			return toReturn;
		}
		
		@Override
		protected void onProgressUpdate(Integer...progress) {
			// TODO set progress percent here
		}
		
		@Override
		protected void onPostExecute(ImmutablePair<String, Object> result) {
			((TextView) result.right).setText(result.left);
		}
	}
	
	
	private class SetSportIcon extends AsyncTask<Object, Integer, ImmutablePair<String, Object>> {
		// Shadow copy for when the broader class is done executing
		Context context;
		
		@Override
		protected ImmutablePair<String, Object> doInBackground(Object... params) {
			context = (Context) params[2];
			String sport = ((GameParse) params[0]).getSport();
			ImmutablePair<String, Object> toReturn = new ImmutablePair<String, Object>(sport, params[1]);
			return toReturn;
		}
		
		@Override
		protected void onProgressUpdate(Integer...progress) {
			// TODO set progress percent here
		}
		
		@Override
		protected void onPostExecute(ImmutablePair<String, Object> result) {
			int id = HelperFunction.getResId(result.left, context, 
					R.drawable.class);

			if (id != -1) {
				((ImageView) result.right).setBackgroundResource(id);
			} else {
				((ImageView) result.right).setBackgroundResource(AppConstant.UNKNOWN_IMG);
			};
		}
	}
}