package org.mahoutdemo.model;

import java.io.IOException;
import java.io.InputStream;

public interface UserLoader {
	void addListener(Listener listener);
	
	void clearUserListener();
	
	void loadUser(InputStream is) throws IOException;

	public interface Listener {
		void userLoadStarted(String[] columns);
		
		void userLoaded(User  user);
		
		void userLoadEnded();
	}
}
