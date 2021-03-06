package org.mahoutdemo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.mahoutdemo.gui.chart.PreferenceStarPlotChart;
import org.mahoutdemo.gui.table.ItemTable;
import org.mahoutdemo.gui.table.PreferenceCompareTable;
import org.mahoutdemo.gui.table.UserTable;
import org.mahoutdemo.model.Item;
import org.mahoutdemo.model.Preference;
import org.mahoutdemo.model.User;

public class CfContainer extends JPanel implements ActionListener, ListSelectionListener {
	private PreferenceStarPlotChart preferenceChart = new PreferenceStarPlotChart();
	private JPanel cotrolPanel = new JPanel();
	private JPanel contentPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	
	private UserTable userTable = new UserTable();
	private ItemTable itemTable = new ItemTable();
	private PreferenceCompareTable preferenceCompareTable;

	private Object[] similarClasses = {"LogLikelihoodSimilarity", "CityBlockSimilarity", "EuclideanDistanceSimilarity", 
		"PearsonCorrelationSimilarity", "SpearmanCorrelationSimilarity", "TanimotoCoefficientSimilarity", "UncenteredCosineSimilarity"};
	
	private Object[] similarCount = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
	private Object[] dataLoadOption = {"User", "Item", "Preference"};
	
	private JComboBox dataLoadOptionBox = new JComboBox(dataLoadOption);
	private JComboBox similarClassBox = new JComboBox(similarClasses);
	private JComboBox similarCountBox = new JComboBox(similarCount);
	
	private JButton dataLoadButton = new JButton("Data Load");
	private JButton recommendUserBaseButton = new JButton("UBR(User Based Recommendation)");
	private JButton similarUserButton = new JButton("Similar Users");
	private JButton similarItemButton = new JButton("Similar Items");
	private JButton recommendItemBaseButton = new JButton("IBR(Item Based Recommendation)");
	private JButton clearButton = new JButton("Clear");

	private GridBagLayout gridbag = new GridBagLayout();
	private GridBagConstraints gc = new GridBagConstraints();
	
	private List<Long> ubrInfluentialUsers = new ArrayList<Long>();
	
	private MahoutDemoUI mahoutDemoUI;
	
	public CfContainer(MahoutDemoUI mahoutDemoUI) {
		this.mahoutDemoUI = mahoutDemoUI;
		initUIs();
	}

	private void initUIs() {
		initCfButtons();
		initCfTables();
		initCfContents();

		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.PAGE_START);
		add(cotrolPanel, BorderLayout.LINE_START);
		add(contentPanel, BorderLayout.CENTER);
	}

	private void initCfContents() {
		preferenceChart.setBackgroundColor(Color.white);
		preferenceChart.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 7, 6));
		contentPanel.setLayout(new BorderLayout(2,2));
		contentPanel.add(preferenceChart, BorderLayout.CENTER);
	}

	private void initCfButtons() {
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		buttonPanel.add(dataLoadOptionBox);
		buttonPanel.add(dataLoadButton);
		dataLoadButton.addActionListener(this);
		
		similarClassBox.addActionListener(this);
		buttonPanel.add(new JLabel("Similarity : "));
		buttonPanel.add(similarClassBox);
		similarCountBox.setSelectedIndex(1);
		buttonPanel.add(similarCountBox);
		
		similarUserButton.setEnabled(false);
		buttonPanel.add(similarUserButton);
		similarUserButton.setToolTipText("Get similar users with the selected users");
		similarUserButton.addActionListener(this);

		similarItemButton.setEnabled(false);
		buttonPanel.add(similarItemButton);
		similarItemButton.setToolTipText("Get similar items with the selected items");
		similarItemButton.addActionListener(this);

		recommendUserBaseButton.setEnabled(false);
		buttonPanel.add(recommendUserBaseButton);
		recommendUserBaseButton.addActionListener(this);
		recommendUserBaseButton.setToolTipText("User based recommendation for the selected users");

		recommendItemBaseButton.setEnabled(false);
		buttonPanel.add(recommendItemBaseButton);
		recommendItemBaseButton.setToolTipText("Item based recomenddaton for the selected users");
		recommendItemBaseButton.addActionListener(this);

		buttonPanel.add(clearButton);
		clearButton.setToolTipText("Clear hilight selected user and items");
		clearButton.addActionListener(this);
	}

	private void initCfTables() {
		cotrolPanel.setLayout(gridbag);
		cotrolPanel.setPreferredSize(new Dimension(350, 700));
		cotrolPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 2));
		
		initUserTable();
		initItemTable();
	}

	private void initItemTable() {
		itemTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane itemTableScroll = new JScrollPane(itemTable);
		
		gc.weighty = 0.7;
		gc.gridy = 2;
		gridbag.setConstraints(itemTableScroll, gc);
		cotrolPanel.add(itemTableScroll);
	}

	private void initUserTable() {

		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 0.3;
		gc.gridy = 1;
		gc.gridwidth = 1;
		
		userTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane userTableScroll = new JScrollPane(userTable);
		
		gridbag.setConstraints(userTableScroll, gc);
		cotrolPanel.add(userTableScroll);
	}

	private void initPreferenceCompareTableForUser(List<Long> userIds) {
		if (contentPanel.getComponentCount() == 2) {
			contentPanel.remove(1);
		}
		
		preferenceCompareTable = new PreferenceCompareTable();

		JScrollPane prefTableScroll = new JScrollPane(preferenceCompareTable);
		prefTableScroll.setPreferredSize(new Dimension(400, 200));
		preferenceCompareTable.displayPreferenceForUser(itemTable.getAllItems(), userIds, mahoutDemoUI.getDataModelBuilder());
		
		contentPanel.add(prefTableScroll, BorderLayout.PAGE_END);
		contentPanel.validate();
	}

	private void initPreferenceCompareTableForItem(List<Long> itemIds) {
		if (contentPanel.getComponentCount() == 2) {
			contentPanel.remove(1);
		}
		
		preferenceCompareTable = new PreferenceCompareTable();

		JScrollPane prefTableScroll = new JScrollPane(preferenceCompareTable);
		prefTableScroll.setPreferredSize(new Dimension(400, 200));
		preferenceCompareTable.displayPreferenceForItem(userTable.getAllUsers(), itemIds, mahoutDemoUI.getDataModelBuilder());
		
		contentPanel.add(prefTableScroll, BorderLayout.PAGE_END);
		contentPanel.validate();
	}

	private void initPreferenceCompareTableForItemBecause(long userId, List<RecommendedItem> itemsBecause, List<Double> similarScore,
			String recommendation) {
		if (contentPanel.getComponentCount() == 2) {
			contentPanel.remove(1);
		}
		
		List<Item> selectedItem = new ArrayList<Item>();
		List<Float> orgScores = new ArrayList<Float>();
		
		for (RecommendedItem item : itemsBecause) {
			selectedItem.add(itemTable.getItem(item.getItemID()));
			orgScores.add(mahoutDemoUI.getDataModelBuilder().getUserItemPreference(userId, item.getItemID()).getPreference());
		}
		
		preferenceCompareTable = new PreferenceCompareTable();

		JScrollPane prefTableScroll = new JScrollPane(preferenceCompareTable);
		prefTableScroll.setPreferredSize(new Dimension(400, 200));
		preferenceCompareTable.displayPreferenceForItemBecause(selectedItem, orgScores, similarScore, recommendation);
		
		contentPanel.add(prefTableScroll, BorderLayout.PAGE_END);
		contentPanel.validate();
	}

	private void clearSelection() {
		preferenceChart.setLeftMargin(15);
		preferenceChart.setRightMargin(15);
		preferenceChart.setTopMargin(15);
		preferenceChart.setBottomMargin(15);

		userTable.clearSelection();
		itemTable.clearSelection();
		preferenceChart.clearSelection();
	}
	
	private Class<? extends UserSimilarity> getSelectedUserSimilarityClass() {
		Class<? extends UserSimilarity> clazz = LogLikelihoodSimilarity.class;
		
		if (similarClassBox.getSelectedIndex() == 1) {
			clazz = CityBlockSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 2) {
			clazz = EuclideanDistanceSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 3) {
			clazz = PearsonCorrelationSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 4) {
			clazz = SpearmanCorrelationSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 5) {
			clazz = TanimotoCoefficientSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 6) {
			clazz = UncenteredCosineSimilarity.class;
		} 
		return clazz;
	}

	private Class<? extends ItemSimilarity> getSelectedItemSimilarityClass() {
		Class<? extends ItemSimilarity> clazz = LogLikelihoodSimilarity.class;
		
		if (similarClassBox.getSelectedIndex() == 1) {
			clazz = CityBlockSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 2) {
			clazz = EuclideanDistanceSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 3) {
			clazz = PearsonCorrelationSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 4) {
			clazz = null;
		} else if (similarClassBox.getSelectedIndex() == 5) {
			clazz = TanimotoCoefficientSimilarity.class;
		} else if (similarClassBox.getSelectedIndex() == 6) {
			clazz = UncenteredCosineSimilarity.class;
		} 
		return clazz;
	}

	private void recommendUserBased() {
		getSimilarUser();
		
		int[] rows = userTable.getSelectedRows();
		for (int row : rows) {
			String userId = userTable.getValueAt(row, 0).toString();
			preferenceChart.addSelectedUserId(Integer.parseInt(userId));
			
			try {
				List<Preference> recommendations = mahoutDemoUI.getUserItemRecommender().recommendBasedUser(
						getSelectedUserSimilarityClass(), Long.parseLong(userId), 
						Integer.parseInt(similarCountBox.getSelectedItem().toString()));
				itemTable.displayUserBasedRecommendation(recommendations);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		itemTable.updateUI();
	}

	private void getSimilarUser() {
		preferenceChart.clearSelection();
		preferenceChart.setLeftMargin(15);
		preferenceChart.setRightMargin(15);
		preferenceChart.setTopMargin(25);
		preferenceChart.setBottomMargin(30);
		
		int row = userTable.getSelectedRow();
		long lUserId = Long.parseLong(userTable.getValueAt(row, 0).toString());
		ubrInfluentialUsers.clear();
		
		preferenceChart.addSelectedUserId(lUserId);
		ubrInfluentialUsers.add(lUserId);
		
		try {
			long[] userIds = mahoutDemoUI.getUserItemRecommender().getSimilarUsers(getSelectedUserSimilarityClass(), 
					lUserId, Integer.parseInt(similarCountBox.getSelectedItem().toString()));
			List<Long> userIdList = new ArrayList<Long>();
			for (long userId : userIds) {
				userIdList.add(userId);
				ubrInfluentialUsers.add(userId);
			}
			
			Collections.sort(userIdList);
			
			List<Double> score = new ArrayList<Double>();
			
			for (long similarUser : userIdList) {
				score.add(mahoutDemoUI.getUserItemRecommender().getSimilarScoreForUser(getSelectedUserSimilarityClass(), lUserId, similarUser));
			}
			
			int index = 0;
			for (long similarUser : userIdList) {
				preferenceChart.addSimilarUser(similarUser, score.get(index++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initPreferenceCompareTableForUser(ubrInfluentialUsers);
	}

	private void getSimilarItem() {
		preferenceChart.clearSelection();
		preferenceChart.setLeftMargin(30);
		preferenceChart.setRightMargin(40);
		preferenceChart.setTopMargin(15);
		preferenceChart.setBottomMargin(15);
		
		int[] rows = itemTable.getSelectedRows();
		List<Long> comparedItems = new ArrayList<Long>();
		
		for (int row : rows) {
			long lItemId = Long.parseLong(itemTable.getValueAt(row, 0).toString());
			preferenceChart.addSelectedItemId(lItemId);
			comparedItems.add(lItemId);
			
			try {
				List<RecommendedItem> recommendedItem = mahoutDemoUI.getUserItemRecommender().getSimilarItems(
						getSelectedItemSimilarityClass(), lItemId, Integer.parseInt(similarCountBox.getSelectedItem().toString()));
				
				List<Long> itemIdList = new ArrayList<Long>();
				for (RecommendedItem item : recommendedItem) {
					itemIdList.add(item.getItemID());
					comparedItems.add(item.getItemID());
				}
				
				Collections.sort(itemIdList);
				
				List<Double> score = new ArrayList<Double>();
				
				for (long similarItem : itemIdList) {
					score.add(mahoutDemoUI.getUserItemRecommender().getSimilarScoreForItem(
							getSelectedItemSimilarityClass(), lItemId, similarItem));
				}
				
				int index = 0;
				for (long similarItem : itemIdList) {
					preferenceChart.addSimilarItem(similarItem, score.get(index++));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		initPreferenceCompareTableForItem(comparedItems);
	}

	private void recommendItemBased() {
		int[] rows = userTable.getSelectedRows();
		for (int row : rows) {
			String userId = userTable.getValueAt(row, 0).toString();
			preferenceChart.addSelectedUserId(Integer.parseInt(userId));
			
			try {
				List<RecommendedItem> recommenedItems = mahoutDemoUI.getUserItemRecommender().recommendBasedItem(
						getSelectedItemSimilarityClass(), Long.parseLong(userId), 
						Integer.parseInt(similarCountBox.getSelectedItem().toString()));
				List<Preference> recommendations = new ArrayList<Preference>();
				
				for (RecommendedItem item : recommenedItems) {
					Preference preference = new Preference();
					preference.setItemId(item.getItemID());
					preference.setPreference(item.getValue());
					recommendations.add(preference);
				}
				itemTable.displayItemBasedRecommendation(recommendations);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		itemTable.updateUI();
	}
	
	private void showInfluentialItemUser() {
		if (userTable.getSelectedRowCount() > 0) {
			int row = itemTable.getSelectedRow();
			if (itemTable.getValueAt(row, 3) != null) {
				initPreferenceCompareTableForUser(ubrInfluentialUsers);
			} else if (itemTable.getValueAt(row, 4) != null) {
				compareItemBecause();
			}
		}
	}
	
	private void compareItemBecause() {
		preferenceChart.clearSelection();
		preferenceChart.setLeftMargin(30);
		preferenceChart.setRightMargin(40);
		preferenceChart.setTopMargin(15);
		preferenceChart.setBottomMargin(15);
		
		int row = itemTable.getSelectedRow();
		
		long lItemId = Long.parseLong(itemTable.getValueAt(row, 0).toString());
		String recommendPreference = itemTable.getValueAt(row, 4).toString();
		String recommendation = itemTable.getValueAt(row, 1).toString();
		
		if (recommendPreference != null) {
			preferenceChart.addSelectedItemId(lItemId);
			
			try {
				long lUserId = Long.parseLong(userTable.getValueAt(userTable.getSelectedRow(), 0).toString());
				List<RecommendedItem> itemsBecause = mahoutDemoUI.getUserItemRecommender().recommendBasedItemBecause(
						getSelectedItemSimilarityClass(), lUserId, lItemId, 50);
				
				List<Long> itemIdList = new ArrayList<Long>();
				for (RecommendedItem item : itemsBecause) {
					itemIdList.add(item.getItemID());
				}
				
				Collections.sort(itemIdList);
				
				List<Double> score = new ArrayList<Double>();
				
				for (long similarItem : itemIdList) {
					score.add(mahoutDemoUI.getUserItemRecommender().getSimilarScoreForItem(getSelectedItemSimilarityClass(), lItemId, similarItem));
				}
				
				int index = 0;
				for (long similarItem : itemIdList) {
					preferenceChart.addSimilarItem(similarItem, score.get(index++));
				}
				
				initPreferenceCompareTableForItemBecause(lUserId, itemsBecause, score, recommendation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == recommendUserBaseButton) {
			recommendUserBased();
		} else if (event.getSource() == similarUserButton) {
			getSimilarUser();
		} else if (event.getSource() == similarItemButton) {
			getSimilarItem();
		} else if (event.getSource() == recommendItemBaseButton) {
			recommendItemBased();
		} else if (event.getSource() == clearButton) {
			clearSelection();
		} else if (event.getSource() == similarClassBox) {
			checkSimilarClassAvailability();
		} else if (event.getSource() == dataLoadButton) {
			mahoutDemoUI.loadData(dataLoadOptionBox.getSelectedIndex() == 0 ? MahoutDemoUI.LoadEnum.USER :
				(dataLoadOptionBox.getSelectedIndex() == 1 ? MahoutDemoUI.LoadEnum.ITEM : MahoutDemoUI.LoadEnum.PREFERENCE));
		}
	}


	private void checkSimilarClassAvailability() {
		similarItemButton.setEnabled(similarClassBox.getSelectedIndex() != 4 && itemTable.getSelectedRowCount() > 0);
		recommendItemBaseButton.setEnabled(similarClassBox.getSelectedIndex() != 4 && userTable.getSelectedRowCount() > 0);
	}

	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			if (event.getSource() == userTable.getSelectionModel()) {
				clearPreferenceCompareTable();
	 			User selectedUser = userTable.getSelectedUser();
				if (selectedUser != null) {
					long userId = selectedUser.getId();
					itemTable.clearPreference();
					List<Preference> preferences = mahoutDemoUI.getDataModelBuilder().getUserPreferences(userId);
					for (Preference preference : preferences) {
						itemTable.setPreference(preference.getItemId(), preference.getPreference());
					}
					
					itemTable.updateUI();
				}
	
				similarUserButton.setEnabled(userTable.getSelectedRowCount() > 0);
				recommendUserBaseButton.setEnabled(userTable.getSelectedRowCount() > 0);
				recommendItemBaseButton.setEnabled(similarClassBox.getSelectedIndex() != 4 && userTable.getSelectedRowCount() > 0);
			} else if (event.getSource() == itemTable.getSelectionModel()) {
				similarItemButton.setEnabled(similarClassBox.getSelectedIndex() != 4 && itemTable.getSelectedRowCount() > 0);
				if (itemTable.getSelectedRowCount() > 0) {
					showInfluentialItemUser();
				}
			}
		}
	}

	private void clearPreferenceCompareTable() {
		if (contentPanel.getComponentCount() == 2) {
			contentPanel.remove(1);
		}
		contentPanel.validate();
	}
	
	public void clearPreferenceChart() {
		preferenceChart.clearPreference();
		preferenceChart.clearSelection();
		
	}
	
	public void addPreference(Preference preference) {
		preferenceChart.addPreference(preference);
	}
	
	public void setMaxUserId(long maxUserId) {
		preferenceChart.setMaxUserId(maxUserId);
	}

	public void setMaxItemId(long maxItemId) {
		preferenceChart.setMaxItemId(maxItemId);
	}

	public void displayUser(List<String> header, List<User> users) {
		userTable.displayUser(header, users);
		userTable.setRowSelectionInterval(0, 0);
	}

	public void displayItem(List<String> header, List<Item> items) {
		itemTable.displayItem(header, items);
	}

}
