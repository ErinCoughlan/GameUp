package com.gameupapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Sport")
public class Sport extends ParseObject {
	public String getName() { 
		return getString("sport");
	}
	
	public void setName(String name) {
		put("sport", name);
	}
}
