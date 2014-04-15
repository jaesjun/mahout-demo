package org.mahoutdemo.model;

import java.util.ArrayList;
import java.util.List;

public class MahoutModel {
	private long id;
	private List<String> columns = new ArrayList<String>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void addColumn(String column) {
		columns.add(column);
	}
	
	public String getAllColumns(String connector) {
		String allColumn = "";
		for (int i = 0; i< columns.size(); i++) {
			allColumn += columns.get(i);
			if (i < columns.size() - 1) {
				allColumn += connector;
			}
		}
		
		return allColumn;
	}

}
