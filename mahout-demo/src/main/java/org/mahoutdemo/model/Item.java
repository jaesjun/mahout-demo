package org.mahoutdemo.model;

public class Item {
	long id;
	String name;
	String userPreference;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserPreference() {
		return userPreference == null ? "" : userPreference;
	}

	public void setUserPreference(String userPreference) {
		this.userPreference = userPreference;
	}
}
