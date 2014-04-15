package org.mahoutdemo.gui.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.mahoutdemo.model.User;

public class UserTable extends SingleSelectionTableBase {
	
	public void displayUser(List<User> users, List<String> header) {
		super.setAutoCreateRowSorter(false);
		setModel(new UserTableModel(users, header));
		getColumnModel().getColumn(0).setMaxWidth(45);
		getColumnModel().getColumn(1).setMaxWidth(56);
		getColumnModel().getColumn(2).setMaxWidth(56);
		
		TableRowSorter trs = new TableRowSorter(getModel());
		NumberComparator numberComparator = new NumberComparator();
		trs.setComparator(0, numberComparator);
		trs.setComparator(1, numberComparator);
		setRowSorter(trs);
	}
	
	public User getSelectedUser() {
		int[] rows = getSelectedRows();
		for (int row : rows) {
			return ((UserTableModel) getModel()).getSelectedUser(row);
		}
		
		return null;
	}

	public List<User> getAllUsers() {
		return ((UserTableModel)getModel()).users;
	}

	class UserTableModel extends AbstractTableModel {
		private List<User> users;
		private List<String> header;
		
		public UserTableModel(List<User> users, List<String> header) {
			this.users = users;
			this.header = header;
		}

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return users.size();
		}
		
		public User getSelectedUser(int rowIndex) {
			return users.get(rowIndex);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			User user = users.get(rowIndex);
			Object column = "";
			
			if (columnIndex == 0) {
				column = user.getId();
			} else if (columnIndex == 1) {
				column = Long.valueOf(user.getAge());
			} else if (columnIndex == 2) {
				column = user.getGender();
			} else {
				column = user.getJob();
			}
				
			return column;
		}
		
		@Override
	    public String getColumnName(int columnIndex) {
			return header.get(columnIndex);
	    }
		
	}

}
