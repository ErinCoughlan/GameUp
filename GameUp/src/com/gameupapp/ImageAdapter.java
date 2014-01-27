package com.gameupapp;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private List<Integer> objects;
	private Context context;
    
    public ImageAdapter(Context context, List<Integer> playerList) {
        this.objects = playerList;
		this.context = context;
    }

    public int getCount() {
        return objects.size();
    }

    public String getItem(int position) {
        return objects.get(position).toString();
    }

    public long getItemId(int position) {
        return objects.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(6, 6, 6, 6);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(objects.get(position));
        return imageView;
    }
}
