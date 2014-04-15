package org.mahoutdemo.gui.table;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.mahoutdemo.model.Item;
import org.mahoutdemo.model.Preference;

public class ItemTable extends SingleSelectionTableBase {
	private PreferenceCellRenderer orgPreferenceRenderer = new PreferenceCellRenderer();
	private PreferenceCellRenderer ubrPreferenceRenderer = new PreferenceCellRenderer();
	private PreferenceCellRenderer ibrPreferenceRenderer = new PreferenceCellRenderer();
	
	private TableRowSorter trs;
	
	public ItemTable() {
		orgPreferenceRenderer.setForeground(Color.darkGray);
		ubrPreferenceRenderer.setForeground(new Color(128,0,0));
		ibrPreferenceRenderer.setForeground(new Color(0,128,0));
	}
	
	public void displayItem(List<Item> items) {
		super.setAutoCreateRowSorter(true);
		setModel(new ItemTableModel(items));
		getColumnModel().getColumn(0).setMaxWidth(45);
		getColumnModel().getColumn(2).setMaxWidth(55);
		getColumnModel().getColumn(3).setMaxWidth(55);
		getColumnModel().getColumn(4).setMaxWidth(55);
		
		trs = new TableRowSorter(getModel());
		NumberComparator numberComparator = new NumberComparator();
		trs.setComparator(0, numberComparator);
		setRowSorter(trs);
	}

	public void setPreference(long itemId, float userPreference) {
		Item item = ((ItemTableModel) getModel()).getItem(itemId);
		if (item != null) {
			item.setUserPreference(String.valueOf(userPreference));
		}
	}

	public void clearPreference() {
		((ItemTableModel) getModel()).clearPreference();
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 2) {
			return orgPreferenceRenderer;
		} else if (column == 3) {
			return ubrPreferenceRenderer;
		} else if (column == 4) {
			return ibrPreferenceRenderer;
		}
		
		return super.getCellRenderer(row, column);
	}
	
	public List<Item> getAllItems() {
		return ((ItemTableModel) getModel()).items;
	}

	public void displayUserBasedRecommendation(List<Preference> recommendations) {
		ItemTableModel dataModel = ((ItemTableModel) getModel());
		dataModel.ubrMap.clear();
		
		for (Preference preference : recommendations) {
			dataModel.ubrMap.put(preference.getItemId(), preference.getPreference());
		}
		
		trs.toggleSortOrder(3);
		trs.toggleSortOrder(3);
	}

	public void displayItemBasedRecommendation(List<Preference> recommendations) {
		ItemTableModel dataModel = ((ItemTableModel) getModel());
		dataModel.ibrMap.clear();
		
		for (Preference preference : recommendations) {
			dataModel.ibrMap.put(preference.getItemId(), preference.getPreference());
		}
		
		trs.toggleSortOrder(4);
		trs.toggleSortOrder(4);
	}

	public Item getItem(Long itemId) {
		return ((ItemTableModel) getModel()).getItem(itemId);
	}

	class ItemTableModel extends AbstractTableModel {
		private Map<Long, Item> itemMap = new HashMap<Long, Item>();
		private List<Item> items;
		private Map<Long, Float> ubrMap = new HashMap<Long, Float>();
		private Map<Long, Float> ibrMap = new HashMap<Long, Float>();
		
		public ItemTableModel(List<Item> items) {
			this.items = items;
			for (Item item : items) {
				itemMap.put(item.getId(), item);
			}
		}

		public int getColumnCount() {
			return 5;
		}

		public int getRowCount() {
			return items.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Item item = items.get(rowIndex);
			Object column = "";
			
			if (columnIndex == 0) {
				column = item.getId();
			} else if (columnIndex == 1){
				column = item.getName();
			} else if (columnIndex == 2){
				column = item.getUserPreference();
			}  else if (columnIndex == 3){
				column = ubrMap.get(item.getId());
			} else {
				column = ibrMap.get(item.getId());
			}
				
			return column;
		}
		
		@Override
	    public String getColumnName(int columnIndex) {
			String column = "iid";
			
			if (columnIndex == 0) {
				column = "iid";
			} else if (columnIndex == 1){
				column = "Item Name";
			} else if (columnIndex == 2){
				column = "Pref";
			} else if (columnIndex == 3){
				column = "UBR";
			} else {
				column = "IBR";
			}
				
			return column;
	    }
		
		Item getItem(long itemId) {
			return itemMap.get(itemId);
		}
		
		void clearPreference() {
			ibrMap.clear();
			ubrMap.clear();
			for (Item item : items) {
				item.setUserPreference("");
			}
		}
	}

	
}
