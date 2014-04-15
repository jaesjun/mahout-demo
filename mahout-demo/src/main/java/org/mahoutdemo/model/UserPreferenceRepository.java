package org.mahoutdemo.model;

import java.util.List;

public interface UserPreferenceRepository {
	List<Preference> getUserPreferences(Long userId);
	
	Preference getUserItemPreference(Long userId, Long itemId);
}
