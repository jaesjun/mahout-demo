package org.mahoutdemo.gui.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public abstract class SingleSelectionTableBase extends JTable {
	public SingleSelectionTableBase() {
		setFillsViewportHeight(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}
