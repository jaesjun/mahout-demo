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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.common.distance.ChebyshevDistanceMeasure;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.MahalanobisDistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;
import org.apache.mahout.common.distance.MinkowskiDistanceMeasure;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.common.distance.TanimotoDistanceMeasure;
import org.apache.mahout.common.distance.WeightedDistanceMeasure;
import org.apache.mahout.common.distance.WeightedEuclideanDistanceMeasure;
import org.apache.mahout.common.distance.WeightedManhattanDistanceMeasure;
import org.apache.mahout.math.Vector;
import org.mahoutdemo.clustering.ClusterGenerator;
import org.mahoutdemo.clustering.VectorGenerator;
import org.mahoutdemo.gui.chart.ClusteringPlotChart;
import org.mahoutdemo.model.TwoDimensionVector;

import com.google.common.collect.Lists;

public class ClusteringContainer extends JPanel implements ActionListener {
	private JTextField meanXField = new JTextField();
	private JTextField meanYField = new JTextField();
	private JTextField sdField = new JTextField();
	private JTextField countField = new JTextField();
	private JTextArea descArea = new JTextArea();
	
	private JTextField clusterSizeField = new JTextField();
	private JTextField maxIterationField = new JTextField();
	private JTextField convergenceDeltaField = new JTextField();
	private JTextField fuzinessField = new JTextField();
	private JTextField t1Field = new JTextField();
	private JTextField t2Field = new JTextField();
	
	private Object[] distanceMeasureOption = {"ChebyshevDistanceMeasure", "CosineDistanceMeasure", 
		"EuclideanDistanceMeasure", "MahalanobisDistanceMeasure", "ManhattanDistanceMeasure", 
		"MinkowskiDistanceMeasure", "SquaredEuclideanDistanceMeasure", "TanimotoDistanceMeasure", 
		"WeightedDistanceMeasure", "WeightedEuclideanDistanceMeasure", "WeightedManhattanDistanceMeasure"
	};
	private JComboBox distanceMeasureOptionBox = new JComboBox(distanceMeasureOption);

	private Object[] clusteringChoose = {"KMeans", "Fuzzy KMeans", "Cannopy"};
	private JComboBox clusteringChooseBox = new JComboBox(clusteringChoose);

	private JButton generateVectorBtn = new JButton("Generate Vector");
	private JButton clearVectorBtn = new JButton("Clear Vector");
	private JButton clusteringBtn = new JButton("Clustering");
		
	private ClusteringPlotChart clusteringPlotChart = new ClusteringPlotChart();

	private JPanel vectorDescPanel = new JPanel();
	
	private JPanel contentPanel = new JPanel();
	private JPanel clusterPanel = new JPanel();

	private GridBagLayout gridbag = new GridBagLayout();
	private GridBagConstraints gc = new GridBagConstraints();
	
	private List<Vector> vectors = new ArrayList<Vector>();

	public ClusteringContainer() {
		initUIs();
	}

	private void initUIs() {
		setLayout(new BorderLayout());
		initVectorButtons();
		initClusterButton();
		initContents();
		
		add(vectorDescPanel, BorderLayout.WEST);
		add(clusterPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
	}

	private void initClusterButton() {
		clusterPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		clusterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		clusterPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		clusterPanel.add(new JLabel("Distance Measure"));
		clusterPanel.add(distanceMeasureOptionBox);

		clusterPanel.add(new JLabel(" Cluster Size"));
		clusterSizeField.setPreferredSize(new Dimension(40, 20));
		clusterSizeField.setText("5");
		clusterPanel.add(clusterSizeField);

		clusteringChooseBox.addActionListener(this);
		clusterPanel.add(new JLabel(" Clustering Method"));
		clusterPanel.add(clusteringChooseBox);

		clusterPanel.add(new JLabel(" Max Iteration"));
		maxIterationField.setPreferredSize(new Dimension(40, 20));
		maxIterationField.setText("50");
		clusterPanel.add(maxIterationField);

		clusterPanel.add(new JLabel(" Convergence Delta"));
		convergenceDeltaField.setPreferredSize(new Dimension(60, 20));
		convergenceDeltaField.setText("0.001");
		clusterPanel.add(convergenceDeltaField);

		clusterPanel.add(new JLabel(" Fuziness"));
		fuzinessField.setPreferredSize(new Dimension(40, 20));
		fuzinessField.setText("2.0");
		fuzinessField.setEnabled(false);
		clusterPanel.add(fuzinessField);

		clusterPanel.add(new JLabel(" T1"));
		t1Field.setPreferredSize(new Dimension(40, 20));
		t1Field.setText("3.0");
		t1Field.setEnabled(false);
		clusterPanel.add(t1Field);

		clusterPanel.add(new JLabel(" T2"));
		t2Field.setPreferredSize(new Dimension(40, 20));
		t2Field.setText("1.5");
		t2Field.setEnabled(false);
		clusterPanel.add(t2Field);

		clusteringBtn.addActionListener(this);
		clusterPanel.add(clusteringBtn);
	}

	private void initContents() {
		clusteringPlotChart.setBorder(BorderFactory.createLineBorder(Color.gray));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout(2,2));
		contentPanel.add(clusteringPlotChart, BorderLayout.CENTER);
	}

	private void initVectorButtons() {
		JPanel vectorPanel = new JPanel();
		vectorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0), 
			BorderFactory.createLineBorder(Color.gray)));
		vectorPanel.setLayout(gridbag);
		
		meanXField.setText("0.0");
		meanYField.setText("0.0");
		sdField.setText("0.5");
		countField.setText("5000");
		
		meanXField.setPreferredSize(new Dimension(40, 20));
		meanYField.setPreferredSize(new Dimension(40, 20));
		sdField.setPreferredSize(new Dimension(40, 20));
		countField.setPreferredSize(new Dimension(60, 20));
		
		generateVectorBtn.setPreferredSize(new Dimension(80, 20));
		clearVectorBtn.setPreferredSize(new Dimension(80, 20));
		
		generateVectorBtn.addActionListener(this);
		clearVectorBtn.addActionListener(this);
		
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		gc.ipady = 10;
		gc.gridy = 0;
		JLabel meanX = new JLabel(" Mean X");
		gridbag.setConstraints(meanX, gc);
		vectorPanel.add(meanX);
		
		gc.gridx = 1;
		gridbag.setConstraints(meanXField, gc);
		vectorPanel.add(meanXField);
		
		JLabel meanY = new JLabel(" Mean Y");
		gc.gridx = 0;
		gc.gridy = 1;
		gridbag.setConstraints(meanY, gc);
		vectorPanel.add(meanY);
		
		gc.gridx = 1;
		gridbag.setConstraints(meanYField, gc);
		vectorPanel.add(meanYField);
		
		JLabel sdLabel = new JLabel(" Standard Deviation");
		gc.gridx = 0;
		gc.gridy = 2;
		gridbag.setConstraints(sdLabel, gc);
		vectorPanel.add(sdLabel);
		
		gc.gridx = 1;
		gridbag.setConstraints(sdField, gc);
		vectorPanel.add(sdField);
		
		JLabel vcLabel = new JLabel(" Vector Count");
		gc.gridx = 0;
		gc.gridy = 3;
		gridbag.setConstraints(vcLabel, gc);
		vectorPanel.add(vcLabel);
		
		gc.gridx = 1;
		gridbag.setConstraints(countField, gc);
		vectorPanel.add(countField);
		
		gc.gridx = 0;
		gc.gridy = 4;
		gc.gridwidth = 2;
		gridbag.setConstraints(generateVectorBtn, gc);
		vectorPanel.add(generateVectorBtn);
		
		gc.gridx = 0;
		gc.gridy = 5;
		gc.gridwidth = 2;
		gridbag.setConstraints(clearVectorBtn, gc);
		vectorPanel.add(clearVectorBtn);
		
		vectorDescPanel.setLayout(gridbag);
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.CENTER;
		gc.ipady = 10;
		gc.gridy = 0;
		gc.gridx = 0;
		gc.weighty = 0;
		
		gridbag.setConstraints(vectorPanel, gc);
		vectorDescPanel.add(vectorPanel);
		
		descArea.setBackground(vectorDescPanel.getBackground());
		JScrollPane scPanel = new JScrollPane(descArea);
		scPanel.setOpaque(false);
		scPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0), 
				BorderFactory.createLineBorder(Color.gray)));
		gc.gridy = 1;
		gc.weighty = 1.0;
		
		gridbag.setConstraints(scPanel, gc);
		vectorDescPanel.add(scPanel);
	}

	private void drawVector(List<Vector> vectors) {
		this.vectors.addAll(vectors);
		for (Vector v : vectors) {
			clusteringPlotChart.addVector(new TwoDimensionVector(v.get(0), v.get(1)));
		}
	}
	
	private void generateVector() {
		try {
			double meanX = Double.parseDouble(meanXField.getText());
			double meanY = Double.parseDouble(meanYField.getText());
			double sd = Double.parseDouble(sdField.getText());
			int count = Integer.parseInt(countField.getText());
			
			drawVector(VectorGenerator.generateRandomVector(count, meanX, meanY, sd));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == generateVectorBtn) {
			generateVector();
		} else if (e.getSource() == clearVectorBtn) {
			clusteringPlotChart.clear();
			vectors.clear();
			descArea.setText("");
		} else if (e.getSource() == clusteringBtn) {
			clusteringPlotChart.clearClustering();
			doClustering();
		} else if (e.getSource() == clusteringChooseBox) {
			checkOptionInputField();
		}
		
	}

	private void checkOptionInputField() {
		if (clusteringChooseBox.getSelectedIndex() == 0) {
			clusterSizeField.setEnabled(true);
			maxIterationField.setEditable(true);
			convergenceDeltaField.setEnabled(true);
			fuzinessField.setEnabled(false);
			t1Field.setEnabled(false);
			t2Field.setEnabled(false);
		} else if (clusteringChooseBox.getSelectedIndex() == 1) {
			clusterSizeField.setEnabled(true);
			maxIterationField.setEditable(true);
			convergenceDeltaField.setEnabled(true);
			fuzinessField.setEnabled(true);
			t1Field.setEnabled(false);
			t2Field.setEnabled(false);
		} else if (clusteringChooseBox.getSelectedIndex() == 2) {
			clusterSizeField.setEnabled(false);
			maxIterationField.setEditable(false);
			convergenceDeltaField.setEnabled(false);
			fuzinessField.setEnabled(false);
			t1Field.setEnabled(true);
			t2Field.setEnabled(true);
		} 
		
	}

	private void doClustering() {
		try {
			if (clusteringChooseBox.getSelectedIndex() == 0 || clusteringChooseBox.getSelectedIndex() == 1) {
				doKMeansClustering();
			} else {
				doCanopyClustering();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doCanopyClustering() throws Exception {
		Class<? extends DistanceMeasure> clazz = getDistanceMesureClass();
		DistanceMeasure measure = clazz.newInstance();
		double t1 = Double.parseDouble(t1Field.getText());
		double t2 = Double.parseDouble(t2Field.getText());
		
		List<List<Cluster>> clusters = ClusterGenerator.runCanopyClusterer(vectors, measure, t1, t2);
		displayCluster(clusters);
	}

	private void doKMeansClustering() throws Exception {
		Class<? extends DistanceMeasure> clazz = getDistanceMesureClass();
		DistanceMeasure measure = clazz.newInstance();
		int numClusters = Integer.parseInt(clusterSizeField.getText());
		int maxIterations = Integer.parseInt(maxIterationField.getText());
		double convergenceDelta = Double.parseDouble(convergenceDeltaField.getText());
		List<List<Cluster>> clusters = null;
	
		if (clusteringChooseBox.getSelectedIndex() == 0) {
			clusters = ClusterGenerator.runKMeansClusterer(vectors, measure, numClusters, maxIterations, convergenceDelta);
		} else if (clusteringChooseBox.getSelectedIndex() == 1) {
			float fuziness = Float.parseFloat(fuzinessField.getText());
			clusters = ClusterGenerator.runFuzzyKMeansClusterer(vectors, measure, numClusters, maxIterations, fuziness, convergenceDelta);
		}
		
		displayCluster(clusters);
	}
	
	private void displayCluster(List<List<Cluster>> clusters) {
		Map<Integer, List<TwoDimensionVector>> centerMap = new TreeMap<Integer, List<TwoDimensionVector>>();
		Map<Integer, List<TwoDimensionVector>> radiusMap = new TreeMap<Integer, List<TwoDimensionVector>>();
		
		for (int i = 0; i < clusters.size(); i++) {
			List<Cluster> cluster = clusters.get(i);
			
			for (int j = 0; j < cluster.size(); j++) {
				Cluster clst = cluster.get(j);
				TwoDimensionVector center = new TwoDimensionVector(clst.getCenter().get(0), clst.getCenter().get(1));
				TwoDimensionVector radius = new TwoDimensionVector(clst.getRadius().get(0), clst.getRadius().get(1));
				
				List<TwoDimensionVector> centers = centerMap.get(j);
				if (centers == null) {
					centers = Lists.newArrayList();
					centerMap.put(j, centers);
				}

				List<TwoDimensionVector> radiuses = radiusMap.get(j);
				if (radiuses == null) {
					radiuses = Lists.newArrayList();
					radiusMap.put(j, radiuses);
				}

				centers.add(center);
				radiuses.add(radius);
			}
		}
		
		DecimalFormat df = new DecimalFormat("###,###,###,###.###");

		String clusterDesc = "";
		descArea.setText("");
		for (int key : centerMap.keySet()) {
			List<TwoDimensionVector> centers = centerMap.get(key);
			List<TwoDimensionVector> radiuses = radiusMap.get(key);
			clusteringPlotChart.drawCluster(centers, radiuses);
			
			clusterDesc += "CLUSTER[" + key + "]\n";
			TwoDimensionVector center = centers.get(centers.size() - 1);
			TwoDimensionVector radius = radiuses.get(centers.size() - 1);
			clusterDesc += "center : " + df.format(center.getX()) + ", " + df.format(center.getY()) + "\n";
			clusterDesc += "width  : " + df.format(radius.getX()) + "\n";
			clusterDesc += "height : " + df.format(radius.getY()) + "\n\n";
		}
		
		descArea.setText(clusterDesc);
	}

	private Class<? extends DistanceMeasure> getDistanceMesureClass() {
		Class<? extends DistanceMeasure> clazz = null;
		
		if (distanceMeasureOptionBox.getSelectedIndex() == 0) {
			clazz = ChebyshevDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 1) {
			clazz = CosineDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 2) {
			clazz = EuclideanDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 3) {
			clazz = MahalanobisDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 4) {
			clazz = ManhattanDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 5) {
			clazz = MinkowskiDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 6) {
			clazz = SquaredEuclideanDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 7) {
			clazz = TanimotoDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 8) {
			clazz = WeightedDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 9) {
			clazz = WeightedEuclideanDistanceMeasure.class; 
		} else if (distanceMeasureOptionBox.getSelectedIndex() == 10) {
			clazz = WeightedManhattanDistanceMeasure.class; 
		}
		
		return clazz;
	}

}
