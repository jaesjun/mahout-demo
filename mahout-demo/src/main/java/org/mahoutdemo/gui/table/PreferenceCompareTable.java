package org.mahoutdemo.gui.table;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.mahoutdemo.cf.DataModelBuilder;
import org.mahoutdemo.model.Item;
import org.mahoutdemo.model.Preference;
import org.mahoutdemo.model.User;
import org.mahoutdemo.model.UserPreferenceRepository;

public class PreferenceCompareTable extends JTable {
	private PreferenceCellRenderer preferenceRenderer = new PreferenceCellRenderer();
	private PreferenceCellRenderer basePreferenceRenderer = new PreferenceCellRenderer();
	private boolean displayPreferenceForItemBecause = false;
	private TableRowSorter trs;
	
	public PreferenceCompareTable() {
		preferenceRenderer.setForeground(Color.darkGray);
		basePreferenceRenderer.setForeground(Color.darkGray);
		basePreferenceRenderer.setBackground(new Color(200, 200, 255));
	}
	
	private void initResize() {
		super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		super.setAutoCreateRowSorter(true);
	}

	private void initColumnSize(int columnSize) {
		getColumnModel().getColumn(0).setMaxWidth(40);
		getColumnModel().getColumn(1).setPreferredWidth(200);
		for (int i = 0; i < columnSize; i++) {
			getColumnModel().getColumn(2 + i).setPreferredWidth(80);
		}
	}

	private void initSort() {
		trs = new TableRowSorter(getModel());
		NumberComparator numberComparator = new NumberComparator();
		trs.setComparator(0, numberComparator);
		setRowSorter(trs);
		
	}

	public void displayPreferenceForUser(List<Item> items, List<Long> userIds, UserPreferenceRepository userRepository) {
		initResize();
		
		PreferenceCompareTableModel model = new PreferenceCompareTableModel(true, userIds, userRepository);
		model.setItems(items);
		setModel(model);

		initColumnSize(userIds.size());
		initSort();
		trs.toggleSortOrder(2);
		trs.toggleSortOrder(2);
	}

	public void displayPreferenceForItem(List<User> users, List<Long> itemIds, DataModelBuilder userRepository) {
		initResize();
		
		PreferenceCompareTableModel model = new PreferenceCompareTableModel(false, itemIds, userRepository);
		model.setUsers(users);
		setModel(model);
		
		initColumnSize(itemIds.size());
		initSort();
		trs.toggleSortOrder(2);
		trs.toggleSortOrder(2);
	}

	public void displayPreferenceForItemBecause(List<Item> selectedItem, List<Float> orgPreference, 
			List<Double> recommendedPreference, String recommendation) {
		displayPreferenceForItemBecause = true;
		super.setAutoCreateRowSorter(true);
		
		ItemBecauseCompareTableModel model = new ItemBecauseCompareTableModel(selectedItem, orgPreference, recommendedPreference, recommendation);
		setModel(model);
		
		getColumnModel().getColumn(0).setMaxWidth(100);
		getColumnModel().getColumn(2).setPreferredWidth(200);
		getColumnModel().getColumn(3).setPreferredWidth(300);
		getColumnModel().getColumn(2).setMaxWidth(200);
		getColumnModel().getColumn(3).setMaxWidth(300);
		
		initSort();
	}

	public void setPreference(long itemId, float userPreference) {
		Item item = ((PreferenceCompareTableModel) getModel()).getItem(itemId);
		if (item != null) {
			item.setUserPreference(String.valueOf(userPreference));
		}
		
	}

	public void clearPreference() {
		((PreferenceCompareTableModel) getModel()).clearPreference();
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (displayPreferenceForItemBecause) {
			if (column == 2) {
				return basePreferenceRenderer;
			}
		} else if (column >= 2) {
			return column == 2 ? basePreferenceRenderer : preferenceRenderer;
		} 
		
		return super.getCellRenderer(row, column);
	}

	class PreferenceCompareTableModel extends AbstractTableModel {

		private List<Item> items;
		private List<User> users;
		private List<Long> comparedIds;
		private UserPreferenceRepository userRepository;
		private boolean userCompare;
		
		public PreferenceCompareTableModel(boolean userCompare, List<Long> comparedIds, UserPreferenceRepository userRepository) {
			this.userCompare = userCompare;
			this.comparedIds = comparedIds;
			this.userRepository = userRepository;
		}
		
		public void setItems(List<Item> items) {
			this.items = items;
		}

		public void setUsers(List<User> users) {
			this.users = users;
		}

		public int getColumnCount() {
			return comparedIds.size() + 2;
		}

		public int getRowCount() {
			return userCompare ? items.size() : users.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return userCompare ? getItemValue(rowIndex, columnIndex) : getUserValue(rowIndex, columnIndex);
		}
		
		private Object getUserValue(int rowIndex, int columnIndex) {
			User user = users.get(rowIndex);
			Object column = "";
			
			if (columnIndex == 0) {
				column = user.getId();
			} else if (columnIndex == 1){
				column =  user.getAllColumns(" | ");
			} else {
				Preference preference = userRepository.getUserItemPreference(user.getId(), comparedIds.get(columnIndex - 2));
				if (preference != null) {
					column = String.valueOf(preference.getPreference());
				}
			}
				
			return column;
		}

		private Object getItemValue(int rowIndex, int columnIndex) {
			Item item = items.get(rowIndex);
			Object column = "";
			
			if (columnIndex == 0) {
				column = item.getId();
			} else if (columnIndex == 1){
				column = item.getAllColumns(" | ");
			} else {
				Preference preference = userRepository.getUserItemPreference(comparedIds.get(columnIndex - 2), item.getId());
				if (preference != null) {
					column = String.valueOf(preference.getPreference());
				}
			}
				
			return column;
		}

		@Override
	    public String getColumnName(int columnIndex) {
			String column = "iid";
			
			if (columnIndex == 0) {
				column = "iid";
			} else if (columnIndex == 1){
				column = userCompare ? "Item Name" : "User";
			} else {
				column = userCompare ? "uid-" + comparedIds.get(columnIndex - 2) : "iid-" + comparedIds.get(columnIndex - 2);
			}
				
			return column;
	    }
		
		Item getItem(long itemId) {
			for (Item item : items) {
				if (item.getId() == itemId) {
					return item;
				}
			}
			
			return null;
		}
		
		void clearPreference() {
			for (Item item : items) {
				item.setUserPreference("");
			}
		}
	}

	class ItemBecauseCompareTableModel extends AbstractTableModel {
		private String recommendation;
		private List<Item> items;
		private List<Float> orgPreference;
		private List<Double> similarScore;
		private DecimalFormat scoreDf = new DecimalFormat("#.#######");
		
		public ItemBecauseCompareTableModel(List<Item> items, List<Float> orgPreference, 
				List<Double> similarScore, String recommendation) {
			this.recommendation = recommendation;
			this.items = items;
			this.orgPreference = orgPreference;
			this.similarScore = similarScore;
		}
		
		public int getColumnCount() {
			return 4;
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
				column = item.getAllColumns(" | ");
			} else if (columnIndex == 2){
				column = orgPreference.get(rowIndex);
			}  else {
				column = scoreDf.format(similarScore.get(rowIndex));
			}
				
			return column;
		}

		@Override
	    public String getColumnName(int columnIndex) {
			String column = "iid";
			
			if (columnIndex == 0) {
				column = "iid";
			} else if (columnIndex == 1){
				column = "Most Influential Items to a given user";
			} else if (columnIndex == 2){
				column = "Original Preference";
			} else {
				column = "Similarity with : " + recommendation;
			}
				
			return column;
	    }
	}
}
