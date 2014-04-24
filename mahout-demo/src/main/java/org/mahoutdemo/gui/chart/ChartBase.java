package org.mahoutdemo.gui.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public abstract class ChartBase extends JComponent {
    protected int leftMargin = 15;
    protected int topMargin = 15;
    protected int rightMargin = 15;
    protected int bottomMargin = 15;
    
    protected Color lineColor = new Color(220, 236, 240);
    protected Color markColor = new Color(128, 0, 128);
    protected Color labelColor = new Color(0, 0, 128);
    
    protected Color backgroundColor = Color.white;
    protected Color innerBackgrundColor = Color.white;
    protected Font fontLabel = new Font( "Arial", Font.PLAIN, 10) ;
    
	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(Color labelColor) {
		this.labelColor = labelColor;
	}

	public Color getMarkColor() {
		return markColor;
	}

	public void setMarkColor(Color markColor) {
		this.markColor = markColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}

	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}

	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}

	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	protected void fillBackground(Graphics2D g2) {
        DrawUtil.fillRect(g2, backgroundColor, 0, 0, getSize().width, getSize().height);
        DrawUtil.fillRect(g2, innerBackgrundColor, leftMargin+1, topMargin+1, 
        		getSize().width-(leftMargin + rightMargin + 1), getSize().height - (topMargin + bottomMargin + 1));
    }
	
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        fillBackground(g2);
        drawChart(g2);
	}
	
	public abstract void drawChart(Graphics2D g2);


}
