package org.mahoutdemo.gui.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mahoutdemo.model.Preference;

public class PreferenceStarPlotChart extends ChartBase {
	
	private long maxItemId = 1682;
	private long maxUserId = 943;
	private int itemMarkingCount = 18;
	private int userMarkingCount = 26;
	
	private List<Long> selectedUserIds = new ArrayList<Long>();
	private List<Long> selectedItemIds = new ArrayList<Long>();
	private List<Long> similarUserIds = new ArrayList<Long>();
	private List<Double> similarUserScores = new ArrayList<Double>();
	private List<Long> similarItemIds = new ArrayList<Long>();
	private List<Double> similarItemScores = new ArrayList<Double>();

    private Color userHilightColor = new Color(255, 10, 10);
    private Color itemHilightColor = new Color(10, 10, 255);
    private Color inActiveColor = new Color(190, 190, 200);
    private Color activeColor = new Color(100, 100, 255);
    
    private Color[] preferenceColor = {new Color(255,0,0), new Color(255,0,255), new Color(255,255,0), new Color(0,255,0), new Color(0,0,255)};
    private Color evenBackgroundColor = new Color(180,180,180);

	private List<Preference> allPreference = new ArrayList<Preference>();
	private List<Preference> selectedUser = new ArrayList<Preference>();
	private List<Preference> selectedItem = new ArrayList<Preference>();
	private List<Preference> similarUser = new ArrayList<Preference>();
	private List<Preference> similarItem = new ArrayList<Preference>();

	private DecimalFormat scoreDf = new DecimalFormat("#.####");
	
	public long getMaxItemId() {
		return maxItemId;
	}

	public void setMaxItemId(long maxItemId) {
		this.maxItemId = maxItemId;
	}

	public long getMaxUserId() {
		return maxUserId;
	}

	public void setMaxUserId(long maxUserId) {
		this.maxUserId = maxUserId;
	}

	public int getItemMarkingCount() {
		return itemMarkingCount;
	}

	public void setItemMarkingCount(int itemMarkingCount) {
		this.itemMarkingCount = itemMarkingCount;
	}

	public int getUserMarkingCount() {
		return userMarkingCount;
	}

	public void setUserMarkingCount(int userMarkingCount) {
		this.userMarkingCount = userMarkingCount;
	}

	private boolean containNumber(List<Long> container, Long target) {
		for (long num : container) {
			if (num == target) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clearPreference() {
		allPreference.clear();
	}
	
	public void clearSelection() {
		selectedUserIds.clear();
		selectedItemIds.clear();
		selectedUser.clear();
		selectedItem.clear();
		
		similarUserIds.clear();
		similarUserScores.clear();

		similarItemIds.clear();
		similarItemScores.clear();

		similarUser.clear();
		similarItem.clear();

		super.repaint();
	}

	public void addSelectedUserId(long selectedUserId) {
		if (!containNumber(selectedUserIds, selectedUserId)) {
			selectedUserIds.add(selectedUserId);
			
			for (Preference preference : allPreference) {
				if (preference.getUserId() == selectedUserId) {
					selectedUser.add(preference);
				}
			}
		}
		super.repaint();
	}

	public void addSelectedItemId(long selectedItemId) {
		if (!containNumber(selectedItemIds, selectedItemId)) {
			selectedItemIds.add(selectedItemId);
			for (Preference preference : allPreference) {
				if (preference.getItemId() == selectedItemId) {
					selectedItem.add(preference);
				}
			}
		}
		super.repaint();
	}
	
	public void addSimilarUser(long userId, double score) {
		if (!containNumber(similarUserIds, userId)) {
			similarUserIds.add(userId);
			similarUserScores.add(score);
			for (Preference preference : allPreference) {
				if (preference.getUserId() == userId) {
					similarUser.add(preference);
				}
			}
		}
		super.repaint();
	}

	public void addSimilarItem(long itemId, double score) {
		if (!containNumber(similarItemIds, itemId)) {
			similarItemIds.add(itemId);
			similarItemScores.add(score);
			for (Preference preference : allPreference) {
				if (preference.getItemId() == itemId) {
					similarItem.add(preference);
				}
			}
		}
		super.repaint();
	}


	public Color getUserHilightColor() {
		return userHilightColor;
	}

	public void setUserHilightColor(Color hilightColor) {
		this.userHilightColor = hilightColor;
	}
	
	public Color getItemHilightColor() {
		return itemHilightColor;
	}

	public void setItemHilightColor(Color itemHilightColor) {
		this.itemHilightColor = itemHilightColor;
	}

	public Color getInActiveColor() {
		return inActiveColor;
	}

	public void setInActiveColor(Color inActiveColor) {
		this.inActiveColor = inActiveColor;
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public void setActiveColor(Color activeColor) {
		this.activeColor = activeColor;
	}

	public Color getOddBackgroundColor() {
		return evenBackgroundColor;
	}

	public void setOddBackgroundColor(Color oddBackgroundColor) {
		this.evenBackgroundColor = oddBackgroundColor;
	}

	public void addPreference(Preference preference) {
		allPreference.add(preference);
		super.repaint();
	}
	
	private void drawPreferences(Graphics2D g2) {
        if (selectedUserIds.size() > 0 || selectedItemIds.size() > 0 || similarUser.size() > 0 || similarItem.size() > 0) {
        	drawPreferences(g2, allPreference, inActiveColor);
        	
        	drawUsermarker(g2, selectedUserIds, new Color(128,0,0), false);
        	drawUsermarker(g2, similarUserIds, Color.darkGray, true);
        	drawItemMarker(g2, selectedItemIds, new Color(128,0,0), false);
        	drawItemMarker(g2, similarItemIds, Color.darkGray, true);
        	
        	drawPreferences(g2, selectedUser);
        	drawPreferences(g2, selectedItem);
        	drawPreferences(g2, similarUser);
        	drawPreferences(g2, similarItem);
        } else {
        	drawPreferences(g2, allPreference);
        }
	}

	private void drawUsermarker(Graphics2D g2, List<Long> userIds, Color markerColor, boolean drawScore) {
		float ruleWidth = (getSize().width - (leftMargin + rightMargin)) / (float)maxUserId;
		boolean odd = true;
		int index = 0;
		for (long userId : userIds) {
			int xPos = leftMargin + (int) (ruleWidth * userId) + 1;
			
			DrawUtil.drawDashedLine(g2, markerColor, xPos, topMargin + 1, xPos, getSize().height - bottomMargin - 1, 1, 4);

			g2.setColor(markColor);
	        g2.setFont(fontLabel);
	        FontMetrics fontmetrics = g2.getFontMetrics();
	        String userIdStr = String.valueOf(userId);
        	int labelWidth = fontmetrics.stringWidth(userIdStr);

        	int yPos = odd ? getSize().height - bottomMargin + fontmetrics.getHeight() : 
        		getSize().height - bottomMargin + 3 * fontmetrics.getHeight() / 2 + 2;

	        g2.drawString(userIdStr, xPos - labelWidth/2, yPos);
	        if (drawScore) {
	        	String score = String.valueOf(scoreDf.format(similarUserScores.get(index++))); 
	        	labelWidth = fontmetrics.stringWidth(score);
	        	yPos = odd ? topMargin - 2 : topMargin - fontmetrics.getHeight();
	        	g2.drawString(score, xPos - labelWidth/2, yPos);
	        }
			odd = !odd;
		}
	}

	private void drawItemMarker(Graphics2D g2, List<Long> itemIds, Color markerColor, boolean drawScore) {
		float ruleHeight = (getSize().height - (topMargin + bottomMargin)) / (float)maxItemId;
		boolean odd = true;
		int index = 0;
		
		for (long itemId : itemIds) {
			int yPos = topMargin + (int) (ruleHeight * itemId) + 2;
			
			DrawUtil.drawDashedLine(g2, markerColor, leftMargin+1, yPos, getSize().width - rightMargin-1, yPos, 1, 4);

			g2.setColor(markColor);
	        g2.setFont(fontLabel);
	        FontMetrics fontmetrics = g2.getFontMetrics();
	        String userIdStr = String.valueOf(itemId);
	        int labelWidth = fontmetrics.stringWidth(userIdStr);

	        int xPos = odd ? leftMargin - labelWidth : leftMargin - 3 * labelWidth / 2;
	        g2.drawString(userIdStr, xPos, yPos + fontmetrics.getHeight() / 3);

	        if (drawScore) {
	        	String score = String.valueOf(scoreDf.format(similarItemScores.get(index++))); 
	        	xPos = odd ? getSize().width - rightMargin + 2 : getSize().width - rightMargin + 15;
	        	g2.drawString(score, xPos, yPos + fontmetrics.getHeight() / 3);
	        }
			odd = !odd;

		}
	}

	private void drawPreferences(Graphics2D g2, List<Preference> preferences) {
		
		float ruleHeight = (getSize().height - (topMargin + bottomMargin)) / (float)maxItemId;
		float ruleWidth = (getSize().width - (leftMargin + rightMargin)) / (float)maxUserId;
		for (Preference item : preferences) {
			int yPos = topMargin + (int) (ruleHeight * item.getItemId()) + 1;
			int xPos = leftMargin + (int) (ruleWidth * item.getUserId());
			
			int colorIndex = (int)(item.getPreference() - 1);
			if (colorIndex < 0) {
				colorIndex = 0;
			} else if (colorIndex > 4) {
				colorIndex = 4;
			}
			
			drawPreference(g2, xPos, yPos, preferenceColor[colorIndex]);
			
			/**
			for (int i=0; i<item.getPreference(); i++) {
				drawPreference(g2, xPos + i*2, yPos, activeColor);
			}
			*/
		}
	}

	private void drawPreferences(Graphics2D g2, List<Preference> preferences, Color preferenceColor) {
		
		float ruleHeight = (getSize().height - (topMargin + bottomMargin)) / (float)maxItemId;
		float ruleWidth = (getSize().width - (leftMargin + rightMargin)) / (float)maxUserId;
		for (Preference item : preferences) {
			int yPos = topMargin + (int) (ruleHeight * item.getItemId()) + 1;
			int xPos = leftMargin + (int) (ruleWidth * item.getUserId());
			
			drawPreference(g2, xPos, yPos, preferenceColor);
			/**
			for (int i=0; i<item.getPreference(); i++) {
				drawPreference(g2, xPos + i*2, yPos, preferenceColor);
			}
			*/
		}
	}

	private void drawPreference(Graphics2D g2, int xPos, int yPos, Color xColor ) {
		g2.setColor(xColor);

		if ( xPos > getSize().width - rightMargin-3) {
			xPos = getSize().width - rightMargin -3;
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

	private void drawPreferenceDescription(Graphics2D g2) {
		int xPos = leftMargin + 8;
		String stars = "★";
		
        FontMetrics fontmetrics = g2.getFontMetrics();
        int labelWidth = fontmetrics.stringWidth("★★★★★★★★★★★★★★★");
		int yPos = getSize().height - bottomMargin - fontmetrics.getHeight();
        
        g2.setColor(Color.white);
        g2.fillRect(xPos -2, yPos - 5, labelWidth + 14*5, fontmetrics.getHeight());

        labelWidth = fontmetrics.stringWidth(stars);
		for (Color colorIndex : preferenceColor) {
			g2.setColor(colorIndex);
			g2.fillRect(xPos, yPos, 1, 1 );
			g2.fillRect(xPos+1, yPos+1, 1, 1 );
			g2.fillRect(xPos+2, yPos+2, 1, 1 );
			g2.fillRect(xPos+2, yPos, 1, 1 );
			g2.fillRect(xPos, yPos+2, 1, 1 );
			
			g2.setColor(Color.darkGray);
			g2.drawString(stars, xPos + 6, yPos + fontmetrics.getHeight()/2);
			
			stars += "★";
			labelWidth = fontmetrics.stringWidth(stars);
			xPos += labelWidth + 5;
		}
	}


	private void drawMarking(Graphics2D g2, boolean fillEvenBackground) {
        float itemMarkingHeight = (getSize().height - (topMargin + bottomMargin)) / (float)itemMarkingCount;
        int startY = 0;
        
        g2.setFont(fontLabel);
        
        for (int i = 1; i < itemMarkingCount; i++) {
        	startY = Math.round(topMargin + i * itemMarkingHeight);
        	DrawUtil.drawLine(g2, lineColor, leftMargin, startY, getSize().width - rightMargin, startY);
        	if (i % 2 == 1 && fillEvenBackground) {
        		DrawUtil.fillRect(g2, evenBackgroundColor, leftMargin + 1, startY + 1, getSize().width - rightMargin - leftMargin - 1, 
        				Math.round(topMargin + (i + 1) * itemMarkingHeight - startY - 1));
        	}
        }
        
        float userMarkingWidth = (getSize().width - (leftMargin + rightMargin)) / (float)userMarkingCount;
        int startX = 0;
        for (int i = 1; i < userMarkingCount; i++) {
        	startX = Math.round(leftMargin + i * userMarkingWidth);
        	DrawUtil.drawLine(g2, lineColor, startX, topMargin, startX, getSize().height - bottomMargin);
        }

        DrawUtil.drawRect(g2, lineColor, leftMargin, topMargin, 
            	getSize().width - (leftMargin + rightMargin), getSize().height - (topMargin + bottomMargin));

	}
	
	private void drawMarkDescription(Graphics2D g2) {
		AffineTransform fontAT = new AffineTransform();
	    Font theFont = g2.getFont();

	    FontMetrics fontmetrics = g2.getFontMetrics();
	    fontAT.rotate(90 * java.lang.Math.PI/180);
	    Font theDerivedFont = theFont.deriveFont(fontAT);

	    g2.setColor(labelColor);
	    g2.setFont(theDerivedFont);
	    g2.drawString("[Item]", 4 , topMargin);
	    g2.drawString("[Item Similarity Score]", getSize().width - fontmetrics.getHeight() , topMargin);

	    g2.setFont(theFont);
	    g2.drawString("[User]", leftMargin, getSize().height - 4);
	    g2.drawString("[User Similarity Score]", leftMargin, fontmetrics.getHeight());

	}
	
	private void generateRandomPreferences() {
		allPreference.clear();
		selectedUser.clear();
		for (int i = 0; i < 100000; i++) {
			Preference item = new Preference();
			item.setPreference((int) (5 * new Random(new Random().nextLong()).nextDouble()));
			item.setItemId((int) (maxItemId * new Random(new Random().nextLong()).nextDouble()));
			item.setUserId((int) (maxUserId * new Random(new Random().nextLong()).nextDouble()));
		
			allPreference.add(item);
		}
	}

	@Override
	public void drawChart(Graphics2D g2) {
        drawMarking(g2, true);
        drawPreferences(g2);
        drawMarkDescription(g2);
        drawPreferenceDescription(g2);
	}


}
