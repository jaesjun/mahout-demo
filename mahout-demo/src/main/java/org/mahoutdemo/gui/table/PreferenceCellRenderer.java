package org.mahoutdemo.gui.table;

import javax.swing.table.DefaultTableCellRenderer;

public class PreferenceCellRenderer extends DefaultTableCellRenderer {
	public PreferenceCellRenderer() {
	}
	
	@Override
	public void setValue(Object value) {
		if (value != null) {
			String preferenceStr = value.toString();
			
			if (preferenceStr != null && preferenceStr.length() > 0) {
				float preference = Float.parseFloat(value.toString());
				
				String stars = "";
				int count = 1;
				while (count++ <= preference) {
					stars = stars + "★";
				}
				super.setText(stars);
			} else {
				super.setValue(value);
			}
		} else {
			super.setText("");
		}
	}

}
