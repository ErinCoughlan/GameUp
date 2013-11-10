package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import com.gameupapp.R;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class GameFragment extends ListFragment {

	private GameAdapter adapter;
	private ArrayList<Game> gameList;
	private Context context;

	public interface OnGameClicked {
		public void onGameClicked(Game gameClicked, int position);
	}

	OnGameClicked gameClickedListener;

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		gameClickedListener = (OnGameClicked) a;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameList = new ArrayList<Game>();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Assign adapter to ListView
		setListAdapter(adapter);
	}


	@Override
	public ListView onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		ListView listView = (ListView) inflater.inflate(R.layout.game_fragment, container, false);

		context = inflater.getContext();
		adapter = new GameAdapter(context, R.layout.game, gameList);
		setListAdapter(adapter);

		return listView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Game gameClicked = (Game) adapter.getItem(position);

		gameClickedListener.onGameClicked(gameClicked, position);
	}

	public void update(List<Game> list) {

		// Must use clear and add for notifyDataSetChanged to work correctly
		gameList.clear();
		gameList.addAll(list);

		adapter.notifyDataSetChanged();

	}

}