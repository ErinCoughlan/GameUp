package com.gameupapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class ImageFragment extends Fragment {
	
	private ImageAdapter adapter;
	private ArrayList<Integer> playerList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		playerList = new ArrayList<Integer>();
	}
	
	@Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.photogridview);
        
        adapter = new ImageAdapter(view.getContext(), playerList);
        gridView.setAdapter(adapter);
        
        return view;
    }
	
	public void update(List<Integer> list) {

		// Must use clear and add for notifyDataSetChanged to work correctly
		playerList.clear();
		playerList.addAll(list);

		adapter.notifyDataSetChanged();
	}
}
