package org.mahoutdemo.model;

public class Preference {
	private long userId;
	private long itemId;
	private float preference;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public float getPreference() {
		return preference;
	}

	public void setPreference(float preference) {
		this.preference = preference;
	}

}
