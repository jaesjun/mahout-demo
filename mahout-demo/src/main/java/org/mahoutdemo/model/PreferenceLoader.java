package org.mahoutdemo.model;

import java.io.IOException;
import java.io.InputStream;

public interface PreferenceLoader {
	void addListener(Listener listener);
	
	void clearPreferenceListener();
	
	void loadPreference(InputStream is) throws IOException;

	public interface Listener {
		void preferenceLoadStarted(String[] columns);
		
		void preferenceLoaded(Preference  preference);
		
		void preferenceLoadEnded();
	}

}
