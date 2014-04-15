package org.mahoutdemo.model;


public class Item extends MahoutModel {
	String userPreference;

	public String getUserPreference() {
		return userPreference == null ? "" : userPreference;
	}

	public void setUserPreference(String userPreference) {
		this.userPreference = userPreference;
	}
}
