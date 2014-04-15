package org.mahoutdemo.cf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.mahoutdemo.model.Preference;
import org.mahoutdemo.model.UserPreferenceRepository;

public class DataModelBuilder implements UserPreferenceRepository {
	private Map<Long, List<Preference>> preferenceMap = new HashMap<Long, List<Preference>>();
	
	public void addPreference(Preference preference) {
		List<Preference> preferences = preferenceMap.get(preference.getUserId());
		if (preferences == null) {
			preferences = new ArrayList<Preference>();
			preferenceMap.put(preference.getUserId(), preferences);
		}
		
		preferences.add(preference);
	}
	
	public List<Preference> getUserPreferences(Long userId) {
		return preferenceMap.get(userId);
	}
	
	public DataModel buildDataModel() {
		FastByIDMap<PreferenceArray> preferenceIDMap = new FastByIDMap<PreferenceArray>();
		
		for (Long userId : preferenceMap.keySet()) {
			List<Preference> preferences = preferenceMap.get(userId);
			PreferenceArray prefForUser = new GenericUserPreferenceArray(preferences.size());
			prefForUser.setUserID(0, userId);
			int itemIndex = 0;
			for (Preference pref : preferences) {
				prefForUser.setItemID(itemIndex, pref.getItemId());
				prefForUser.setValue(itemIndex++, pref.getPreference());
			}
			
			preferenceIDMap.put(userId, prefForUser);
		}
		
		return new GenericDataModel(preferenceIDMap);
	}
	
	public DataModel buildDataModel(File modelFile) throws IOException {
		return new FileDataModel(modelFile);
	}

	public Preference getUserItemPreference(Long userId, Long itemId) {
		for (Preference preference : getUserPreferences(userId)) {
			if (preference.getItemId() == itemId) {
				return preference;
			}
		}
		return null;
	}
}
