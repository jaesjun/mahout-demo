package org.mahoutdemo.gui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import org.mahoutdemo.model.TwoDimensionVector;

import com.google.common.collect.Lists;

public class ClusteringPlotChart extends ChartBase {
	private double maxX;
	private double minX;
	private double maxY;
	private double minY;
	
	private double plotMaxX;
	private double plotMinX;
	private double plotMaxY;
	private double plotMinY;
	
	
	private int xMarkCount = 10;
	private int yMarkCount = 10;
	private List<TwoDimensionVector> vectors = Lists.newArrayList();
	private List<List<TwoDimensionVector>> centers = Lists.newArrayList();
	private List<List<TwoDimensionVector>> radiuses = Lists.newArrayList();
	
	private Color baseLineColor = Color.darkGray;
	private Color pointColor = new Color(75, 75, 255);
	private Color clusterCenterColor = Color.red;
	
	private Color[] clusterCircleColor = { 
		new Color(255, 220, 220), new Color(220, 255, 220), new Color(220, 220, 255),
		new Color(255, 255, 220), new Color(255, 220, 255), new Color(220, 255, 255),
		new Color(128, 110, 110), new Color(110, 128, 110), new Color(110, 110, 128),
		new Color(128, 128, 110), new Color(128, 110, 128), new Color(110, 128, 128)
	};

	private Color[] singleIterationColor = { 
		new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255),
		new Color(255, 255, 0), new Color(255, 0, 255), new Color(0, 255, 255),
		new Color(128, 0, 0), new Color(0, 128, 0), new Color(0, 0, 128),
		new Color(128, 128, 0), new Color(128, 0, 128), new Color(0, 128, 128)
	};

	private int[][] clusterCircleColorDelta = {
		{0, -10, -10}, {-10, 0, -10}, {-10, -10, 0},
		{0, 0, -10}, {0, -10, 0}, {0, -10, -10},
		{0, -5, -5}, {-5, 0, -5}, {-5, -5, 0},
		{0, 0, -5}, {0, -5, 0}, {0, -5, -5}
	};
	
	
	private DecimalFormat markingDf = new DecimalFormat("###,###,###.##");
		
	public void clear() {
		vectors.clear();
		centers.clear();
		radiuses.clear();
		
		maxX = Double.MIN_VALUE;
		minX = Double.MIN_VALUE;
		maxY = Double.MIN_VALUE;
		minY = Double.MIN_VALUE;
		
		super.repaint();
	}
	
	public void clearClustering() {
		centers.clear();
		radiuses.clear();
		super.repaint();
	}
	
	public void addVector(TwoDimensionVector vector) {
		checkMaxValue(vector);
		vectors.add(vector);
		super.repaint();
	}
	
	private void checkMaxValue(TwoDimensionVector vector) {
		if (maxX < vector.getX()) {
			maxX = vector.getX();
		}

		if (maxY < vector.getY()) {
			maxY = vector.getY();
		}

		if (minX > vector.getX()) {
			minX = vector.getX();
		}

		if (minY > vector.getY()) {
			minY = vector.getY();
		}
	}
	
	private void calcPlotMaxValue() {
		double max = Math.max(Math.abs(maxX), Math.abs(minX));
		plotMaxX = max;
		plotMinX = 0 - max;
		
		max = Math.max(Math.abs(maxY), Math.abs(minY));
		plotMaxY = max;
		plotMinY = 0 - max;
	}

	public void addVector(List<TwoDimensionVector> vectors) {
		for (TwoDimensionVector vector : vectors) {
			checkMaxValue(vector);
			this.vectors.add(vector);
		}
		super.updateUI();
	}
	
	@Override
	public void drawChart(Graphics2D g2) {
		//generateRandomPreferences();
		calcPlotMaxValue();
		drawMarking(g2);
		drawVector(g2, pointColor, vectors);
		drawCluster(g2);
	}
	
	private void drawCluster(Graphics2D g2) {
		
		for (List<TwoDimensionVector> center : centers) {
			drawVector(g2, clusterCenterColor, center);
		}
		
		for (int i = 0; i < centers.size(); i++) {
			Color lineColor = i < clusterCircleColor.length ? clusterCircleColor[i] : clusterCircleColor[clusterCircleColor.length-1];
			List<TwoDimensionVector> center = centers.get(i);
			List<TwoDimensionVector> radius = radiuses.get(i);
			
			if (center.size() == 1) {
				lineColor = i < singleIterationColor.length ? singleIterationColor[i] : singleIterationColor[singleIterationColor.length-1];
			}

			System.out.println("CLUSTER INDEX : " + i);
			for (int j = 0; j <center.size(); j++) {
				System.out.println("center : " + center.get(j).getX() + "," + center.get(j).getY() + 
					", radius : " + radius.get(j).getX() + "," + radius.get(j).getY());
				g2.setStroke(new BasicStroke(j == center.size() - 1 ? 3 : 1));
				if (!Double.isNaN(center.get(j).getX()) && !Double.isNaN(center.get(j).getY())) {
					drawClusterCircle(g2, lineColor, center.get(j), radius.get(j), j);
					int rd = i < clusterCircleColorDelta.length ? clusterCircleColorDelta[i][0] : clusterCircleColorDelta[clusterCircleColorDelta.length - 1][0];
					int gd = i < clusterCircleColorDelta.length ? clusterCircleColorDelta[i][1] : clusterCircleColorDelta[clusterCircleColorDelta.length - 1][1];
					int bd = i < clusterCircleColorDelta.length ? clusterCircleColorDelta[i][2] : clusterCircleColorDelta[clusterCircleColorDelta.length - 1][2];
					lineColor = makeGradient(lineColor, rd, gd, bd); 
				}
			}
		}
	}
	
	private Color makeGradient(Color color, int rd, int gd, int bd) {
		
		int r = color.getRed() + rd;
		int g = color.getGreen() + gd;
		int b = color.getBlue() + bd;
		
		r = r > 255 ? 255 : r;
		r = r < 0 ? 0 : r;
		g = g > 255 ? 255 : g;
		g = g < 0 ? 0 : g;
		b = b > 255 ? 255 : b;
		b = b < 0 ? 0 : b;
		
		return new Color(r, g, b);
	}

	private void drawClusterCircle(Graphics2D g2, Color lineColor, 
			TwoDimensionVector center, TwoDimensionVector radius, int iteration) {
		double valPerPointY = (getSize().height - bottomMargin - topMargin) / (plotMaxY - plotMinY);
		double valPerPointX = (getSize().width - leftMargin - rightMargin) / (plotMaxX - plotMinX);
		double xPos = getSize().width / 2 + valPerPointX * (center.getX() - radius.getX());
		double yPos = getSize().height / 2 - valPerPointY * (center.getY() + radius.getY());
		
		double width = valPerPointX * radius.getX() * 2;
		double height = valPerPointY * radius.getY() * 2;
		
		g2.setColor(lineColor);
	    g2.draw(new Ellipse2D.Double(xPos, yPos, width, height));
		g2.drawString(String.valueOf(iteration), (int)xPos, (int)yPos);

	}

	private void drawVector(Graphics2D g2, Color drawColor, List<TwoDimensionVector> vectors) {
		double valPerPointY = (getSize().height - bottomMargin - topMargin) / (plotMaxY - plotMinY);
		double valPerPointX = (getSize().width - leftMargin - rightMargin) / (plotMaxX - plotMinX);
		
		for (TwoDimensionVector v : vectors) {
			int xPos = (int) Math.round(getSize().width / 2 + valPerPointX * v.getX());
			int yPos = (int) Math.round(getSize().height / 2 - valPerPointY * v.getY());
			drawPoint(g2, drawColor, xPos, yPos);
		}
	}
	
	private void drawPoint(Graphics2D g2, Color drawColor, int xPos, int yPos) {
		g2.setColor(drawColor);

		if ( xPos > getSize().width - rightMargin - 3) {
			xPos = getSize().width - rightMargin - 3;
		}
		if ( yPos > getSize().height - bottomMargin - 3) {
			yPos = getSize().height - bottomMargin - 3;
		}

		g2.fillRect(xPos, yPos, 1, 1 );
		g2.fillRect(xPos+1, yPos+1, 1, 1 );
		g2.fillRect(xPos+2, yPos+2, 1, 1 );
		g2.fillRect(xPos+2, yPos, 1, 1 );
		g2.fillRect(xPos, yPos+2, 1, 1 );
	}


	private void drawMarking(Graphics2D g2) {
        float yMarkingHeight = (getSize().height - (topMargin + bottomMargin)) / (float)yMarkCount;
        int baseLineIndex = yMarkCount / 2;
        int startY = topMargin;
        double delta = (plotMaxY - plotMinY) / yMarkCount;
        
        g2.setFont(fontLabel);
        FontMetrics fm = g2.getFontMetrics();
        
        for (int i = 0; i <= yMarkCount; i++) {
        	startY = Math.round(topMargin + i * yMarkingHeight);
        	
        	DrawUtil.drawLine(g2, i == 0 || i == baseLineIndex || i == yMarkCount? baseLineColor : lineColor, 
        			leftMargin, startY, getSize().width - rightMargin, startY);

        	double mark = plotMaxY - i * delta;
    		String markStr = markingDf.format(mark);
    		int markLen = fm.stringWidth(markStr);
    		g2.setColor(baseLineColor);
    		g2.drawString(markStr, getSize().width / 2 - markLen, startY);
        }
        
        float xMarkingWidth = (getSize().width - (leftMargin + rightMargin)) / (float)xMarkCount;
        int startX = 0;
        baseLineIndex = xMarkCount / 2;
        delta = (plotMaxX - plotMinX) / xMarkCount;
        
        for (int i = 0; i <= xMarkCount; i++) {
        	startX = Math.round(leftMargin + i * xMarkingWidth);
        	DrawUtil.drawLine(g2, i == 0 || i == baseLineIndex || i == xMarkCount? baseLineColor : lineColor, 
        			startX, topMargin, startX, getSize().height - bottomMargin);

        	double mark = plotMinX + i * delta;
    		String markStr = markingDf.format(mark);
    		int markLen = fm.stringWidth(markStr);
    		g2.setColor(baseLineColor);
    		g2.drawString(markStr, startX - markLen / 2, getSize().height / 2 + fm.getHeight());
        }
	}

	private void generateRandomPreferences() {
		vectors.clear();
		
		for (int i = 0; i < 100000; i++) {
			TwoDimensionVector v = new TwoDimensionVector();
			double x = 10 * new Random(new Random().nextLong()).nextDouble();
			double y = 10 * new Random(new Random().nextLong()).nextDouble();
			
			switch (i % 4) {
			case 1 :
				x = 0 - x;
				break;
			case 2 :
				x = 0 - x;
				y = 0 - y;
				break;
			case 3 :
				y = 0 - y;
				break;
			}
			
			v.setX(x);
			v.setY(y);

			addVector(v);
		}
	}

	public void drawCluster(List<TwoDimensionVector> center, List<TwoDimensionVector> radius) {
		centers.add(center);
		radiuses.add(radius);
		repaint();
	}


}
