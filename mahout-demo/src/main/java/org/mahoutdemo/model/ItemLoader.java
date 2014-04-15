package org.mahoutdemo.model;

import java.io.IOException;
import java.io.InputStream;

public interface ItemLoader {
	void addListener(Listener listener);
	
	void clearItemListener();
	
	void loadItem(InputStream is) throws IOException;

	public interface Listener {
		void itemLoadStarted(String[] columns);
		
		void itemLoaded(Item item);
		
		void itemLoadEnded();
	}
}
