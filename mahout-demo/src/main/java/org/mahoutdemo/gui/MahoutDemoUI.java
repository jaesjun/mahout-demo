package org.mahoutdemo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
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
import org.apache.mahout.math.Vector;
import org.mahoutdemo.cf.DataModelBuilder;
import org.mahoutdemo.cf.UserItemRecommender;
import org.mahoutdemo.clustering.VectorGenerator;
import org.mahoutdemo.gui.chart.ClusteringPlotChart;
import org.mahoutdemo.gui.chart.PreferenceStarPlotChart;
import org.mahoutdemo.gui.table.ItemTable;
import org.mahoutdemo.gui.table.PreferenceCompareTable;
import org.mahoutdemo.gui.table.UserTable;
import org.mahoutdemo.model.Item;
import org.mahoutdemo.model.ItemLoader;
import org.mahoutdemo.model.Preference;
import org.mahoutdemo.model.PreferenceLoader;
import org.mahoutdemo.model.TwoDimensionVector;
import org.mahoutdemo.model.User;
import org.mahoutdemo.model.UserLoader;

public class MahoutDemoUI {
	private CfContainer cfContainer;
	private ClusteringContainer clusteringContainer;
	
	private JTabbedPane tabPane = new JTabbedPane();
	
	private Container container;
	
	private DataModelBuilder builder = new DataModelBuilder();
	private UserItemRecommender userItemRecommender;
	private UserLoader userLoader;
	private ItemLoader itemLoader;
	private PreferenceLoader preferenceLoader;
	
	public enum LoadEnum {
		USER, ITEM, PREFERENCE
	};
	
	public MahoutDemoUI(Container container, UserLoader userLoader, ItemLoader itemLoader, PreferenceLoader preferenceLoader) {
		this.container = container;
		this.userLoader = userLoader;
		this.itemLoader = itemLoader;
		this.preferenceLoader = preferenceLoader;
		initUIs();
	}

	private void initUIs() {
		cfContainer = new CfContainer(this);
		clusteringContainer = new ClusteringContainer();
		tabPane.addTab("Collaborative Filtering", cfContainer);
		tabPane.addTab("Clustering", clusteringContainer);
		
		container.add(tabPane, BorderLayout.CENTER);
	}

	public void loadPreference(InputStream is) throws IOException {
		PreferenceLoader.Listener listener = new PreferenceLoader.Listener() {
			public void preferenceLoadStarted(String[] columns) {
				cfContainer.clearPreferenceChart();
			}

			public void preferenceLoaded(Preference preference) {
				cfContainer.addPreference(preference);
				builder.addPreference(preference);
			}

			public void preferenceLoadEnded() {
			}
		};
		
		preferenceLoader.addListener(listener);
		preferenceLoader.loadPreference(is);
		userItemRecommender = new UserItemRecommender(builder.buildDataModel());
	}

	public void loadUser(InputStream is) throws IOException {
		final List<User> users = new ArrayList<User>();
		final List<String> header = new ArrayList<String>();
		final List<Long> maxUserId = new ArrayList<Long>();
		maxUserId.add(-1L);
		
		UserLoader.Listener listener = new UserLoader.Listener() {
			public void userLoaded(User user) {
				if (maxUserId.get(0) < user.getId()) {
					maxUserId.remove(0);
					maxUserId.add(user.getId());
				}
				users.add(user);
			}
			
			public void userLoadStarted(String[] columns) {
				for (String column : columns) {
					header.add(column);
				}
			}
			
			public void userLoadEnded() {
			}
		};
		
		userLoader.addListener(listener);
		userLoader.loadUser(is);
		
		cfContainer.setMaxUserId(maxUserId.get(0));
		cfContainer.displayUser(header, users);
	}

	public void loadItem(InputStream is) throws IOException {
		final List<Item> items = new ArrayList<Item>();
		final List<String> header = new ArrayList<String>();
		final List<Long> maxItemId = new ArrayList<Long>();
		maxItemId.add(-1L);
		
		ItemLoader.Listener listener = new ItemLoader.Listener() {
			public void itemLoaded(Item item) {
				if (maxItemId.get(0) < item.getId()) {
					maxItemId.remove(0);
					maxItemId.add(item.getId());
				}
				
				items.add(item);
			}
			
			public void itemLoadStarted(String[] columns) {
				for (String column : columns) {
					header.add(column);
				}
			}
			
			public void itemLoadEnded() {
			}
		};
		
		itemLoader.addListener(listener);
		itemLoader.loadItem(is);
		
		cfContainer.setMaxItemId(maxItemId.get(0));
		cfContainer.displayItem(header, items);
	}

	public void setPreferredSize(Dimension preferredSize) {
		container.setPreferredSize(preferredSize);
	}

	public void loadData(LoadEnum loadEnum) {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(container);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			FileInputStream fis = null;
			
			try {
				fis = new FileInputStream(file);
				if (loadEnum == LoadEnum.USER) {
					loadUser(fis);
				} else if (loadEnum == LoadEnum.ITEM) {
					loadItem(fis);
				} else {
					loadPreference(fis);
				}
			} catch (Exception e) {
				JOptionPane.showConfirmDialog(container, e.getMessage());
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	public DataModelBuilder getDataModelBuilder() {
		return builder;
	}

	public UserItemRecommender getUserItemRecommender() {
		return userItemRecommender;
	}

}
