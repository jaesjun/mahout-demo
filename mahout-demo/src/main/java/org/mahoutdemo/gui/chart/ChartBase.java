package org.mahoutdemo.gui.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class ChartBase extends JComponent implements MouseMotionListener, MouseListener, KeyListener {
    protected int leftMargin = 15;
    protected int topMargin = 15;
    protected int rightMargin = 15;
    protected int bottomMargin = 15;
    
    protected Color lineColor = new Color(220, 236, 240);
    protected Color markColor = new Color(128, 0, 128);
    protected Color labelColor = new Color(0, 0, 128);
    
    protected Color backgroundColor = Color.white;
    protected Color innerBackgrundColor = Color.white;//new Color(50, 50, 50);
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

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}
