package com.gameupapp;

import org.apache.commons.lang3.text.WordUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Sport")
public class Sport extends ParseObject {
	public String getName() { 
		String sport = getString("sport");
		return WordUtils.capitalizeFully(sport);
	}
	
	public void setName(String name) {
		put("sport", name);
	}
}
