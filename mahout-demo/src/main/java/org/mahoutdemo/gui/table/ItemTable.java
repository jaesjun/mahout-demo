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
	
	public void displayItem(List<String> header, List<Item> items) {
		super.setAutoCreateRowSorter(true);
		setModel(new ItemTableModel(header, items));
		getColumnModel().getColumn(0).setMaxWidth(45);
		
		getColumnModel().getColumn(header.size()).setMaxWidth(55);
		getColumnModel().getColumn(header.size() + 1).setMaxWidth(55);
		
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
		int headerSize = ((ItemTableModel)getModel()).header.size();
		
		if (column >= headerSize) {
			if (column == headerSize) {
				return orgPreferenceRenderer;
			} else if (column == headerSize + 1) {
				return ubrPreferenceRenderer;
			} else if (column == headerSize + 2) {
				return ibrPreferenceRenderer;
			}
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
		
		trs.toggleSortOrder(dataModel.header.size() + 1);
		trs.toggleSortOrder(dataModel.header.size() + 1);
	}

	public void displayItemBasedRecommendation(List<Preference> recommendations) {
		ItemTableModel dataModel = ((ItemTableModel) getModel());
		dataModel.ibrMap.clear();
		
		for (Preference preference : recommendations) {
			dataModel.ibrMap.put(preference.getItemId(), preference.getPreference());
		}
		
		trs.toggleSortOrder(dataModel.header.size() + 2);
		trs.toggleSortOrder(dataModel.header.size() + 2);
	}

	public Item getItem(Long itemId) {
		return ((ItemTableModel) getModel()).getItem(itemId);
	}

	class ItemTableModel extends AbstractTableModel {
		private List<String> header;
		private Map<Long, Item> itemMap = new HashMap<Long, Item>();
		private List<Item> items;
		private Map<Long, Float> ubrMap = new HashMap<Long, Float>();
		private Map<Long, Float> ibrMap = new HashMap<Long, Float>();
		
		public ItemTableModel(List<String> header, List<Item> items) {
			this.header = header;
			this.items = items;
			for (Item item : items) {
				itemMap.put(item.getId(), item);
			}
		}

		public int getColumnCount() {
			return header.size() + 3;
		}

		public int getRowCount() {
			return items.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Item item = items.get(rowIndex);
			Object column = "";
			
			if (columnIndex == 0) {
				column = item.getId();
			} else if (columnIndex < header.size()){
				column = item.getColumns().get(columnIndex);
			} else if (columnIndex == header.size()){
				column = item.getUserPreference();
			}  else if (columnIndex == header.size() + 1){
				column = ubrMap.get(item.getId());
			} else {
				column = ibrMap.get(item.getId());
			}
				
			return column;
		}
		
		@Override
	    public String getColumnName(int columnIndex) {
			String column = "iid";
			
			if (columnIndex < header.size()){
				column = header.get(columnIndex);
			} else if (columnIndex == header.size()){
				column = "Pref";
			} else if (columnIndex == header.size() + 1){
				column = "UBR";
			} else if (columnIndex == header.size() + 2){
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
