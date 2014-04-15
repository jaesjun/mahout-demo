package org.mahoutdemo.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVModelLoader implements UserLoader, ItemLoader, PreferenceLoader {
	List<PreferenceLoader.Listener> preferenceLoaderListener = new ArrayList<PreferenceLoader.Listener>();
	List<ItemLoader.Listener> itemLoaderListener = new ArrayList<ItemLoader.Listener>();
	List<UserLoader.Listener> userLoaderListener = new ArrayList<UserLoader.Listener>();
	
	private String[] parseRecord(String record) {
		return record.split(",");
	}

	public void loadUser(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		
		String line = null;
		int rowIndex = 0;
		while ((line = br.readLine()) != null) {
			String[] columns = parseRecord(line);
			if (rowIndex++ == 0) {
				notifyLoadUserStarted(columns);
			} else if (columns != null && columns.length > 4) {
				User user = new User();
				try {
					user.setId(Long.parseLong(columns[0]));
					user.setAge(columns[1]);
					user.setGender(columns[2]);
					user.setJob(columns[3]);
					notifyLoad(user);
				} catch (NumberFormatException ne) {
					System.err.println("Skip user record : " + line);
				}
			}
		}
		
		notifyLoadUserEnded();
	}

	public void loadItem(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		
		String line = null;
		int rowIndex = 0;
		while ((line = br.readLine()) != null) {
			String[] columns = parseRecord(line);
			if (rowIndex++ == 0) {
				notifyLoadItemStarted(columns);
			} else if (columns != null && columns.length > 1) {
				try {
					Item item = new Item();
					item.setId(Long.parseLong(columns[0]));
					item.setName(columns[1]);
					notifyLoad(item);
				} catch (NumberFormatException ne) {
					System.err.println("Skip item record : " + line);
				}
			}
		}
		
		notifyLoadItemEnded();
	}

	public void loadPreference(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		
		String line = null;
		int rowIndex = 0;
		while ((line = br.readLine()) != null) {
			String[] columns = parseRecord(line);
			if (rowIndex++ == 0) {
				notifyLoadPreferenceStarted(columns);
			} else if (columns != null && columns.length > 3) {
				Preference preference = new Preference();
				try {
					preference.setUserId(Integer.parseInt(columns[0]));
					preference.setItemId(Integer.parseInt(columns[1]));
					preference.setPreference(Integer.parseInt(columns[2]));
					notifyLoad(preference);
				} catch (NumberFormatException ne) {
					System.err.println("Skip preference record : " + line);
				}
			}
		}
		
		notifyLoadPreferenceEnded();
	}

	private void notifyLoadUserStarted(String[] columns) {
		List<UserLoader.Listener> listeners = Collections.synchronizedList(userLoaderListener);	
		for (UserLoader.Listener listener : listeners) {
			listener.userLoadStarted(columns);
		}
	}

	private void notifyLoadItemStarted(String[] columns) {
		List<ItemLoader.Listener> listeners = Collections.synchronizedList(itemLoaderListener);	
		for (ItemLoader.Listener listener : listeners) {
			listener.itemLoadStarted(columns);
		}
	}

	private void notifyLoadPreferenceStarted(String[] columns) {
		List<PreferenceLoader.Listener> listeners = Collections.synchronizedList(preferenceLoaderListener);	
		for (PreferenceLoader.Listener listener : listeners) {
			listener.preferenceLoadStarted(columns);
		}
	}
	
	private void notifyLoadUserEnded() {
		List<UserLoader.Listener> listeners = Collections.synchronizedList(userLoaderListener);	
		for (UserLoader.Listener listener : listeners) {
			listener.userLoadEnded();
		}
	}

	private void notifyLoadItemEnded() {
		List<ItemLoader.Listener> listeners = Collections.synchronizedList(itemLoaderListener);	
		for (ItemLoader.Listener listener : listeners) {
			listener.itemLoadEnded();
		}
	}

	private void notifyLoadPreferenceEnded() {
		List<PreferenceLoader.Listener> listeners = Collections.synchronizedList(preferenceLoaderListener);	
		for (PreferenceLoader.Listener listener : listeners) {
			listener.preferenceLoadEnded();
		}
	}

	public void addListener(PreferenceLoader.Listener listener) {
		List<PreferenceLoader.Listener> listeners = Collections.synchronizedList(preferenceLoaderListener);	
		listeners.add(listener);
	}

	public void addListener(ItemLoader.Listener listener) {
		List<ItemLoader.Listener> listeners = Collections.synchronizedList(itemLoaderListener);	
		listeners.add(listener);
	}

	public void addListener(UserLoader.Listener listener) {
		List<UserLoader.Listener> listeners = Collections.synchronizedList(userLoaderListener);	
		listeners.add(listener);
	}

	private void notifyLoad(User user) {
		List<UserLoader.Listener> listeners = Collections.synchronizedList(userLoaderListener);	
		for (UserLoader.Listener listener : listeners) {
			listener.userLoaded(user);
		}
	}

	private void notifyLoad(Item item) {
		List<ItemLoader.Listener> listeners = Collections.synchronizedList(itemLoaderListener);	
		for (ItemLoader.Listener listener : listeners) {
			listener.itemLoaded(item);
		}
	}

	private void notifyLoad(Preference preference) {
		List<PreferenceLoader.Listener> listeners = Collections.synchronizedList(preferenceLoaderListener);	
		for (PreferenceLoader.Listener listener : listeners) {
			listener.preferenceLoaded(preference);
		}
	}

	public void clearPreferenceListener() {
		this.preferenceLoaderListener.clear();
	}

	public void clearItemListener() {
		this.itemLoaderListener.clear();
	}

	public void clearUserListener() {
		this.userLoaderListener.clear();
	}

}
