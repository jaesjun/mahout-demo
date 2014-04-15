package org.mahoutdemo.gui.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;

public class DrawUtil {

	public static void drawImage( Graphics pad, Image img, int width, int height, Component parent ) {
	    drawImage( pad, img, 0, 0, width, height, parent );
	}

	public static void drawImage( Graphics pad, Image img, int x, int y, int width, int height, Component parent ) {
	    if( img == null )
	        return;
	        
	    int imgWidth = img.getWidth(parent);
	    int imgHeight = img.getHeight(parent);
	    
	    if( imgWidth < 0 || imgHeight < 0 )
	        return;

	    int startX=x;
	    int startY=y;
	    for( startY=x; startY<height; startY+= imgHeight )
	    {
	        for( startX=x; startX<width; startX +=imgWidth )
	            pad.drawImage(img,startX,startY,imgWidth, imgHeight,parent);
	        startX -= imgWidth;
	        pad.drawImage(img,startX,startY,imgWidth, imgHeight,parent);
	    }
	    
	    startY -= imgHeight;
        for( startX=x; startX<width; startX +=imgWidth )
            pad.drawImage(img,startX,startY,imgWidth, imgHeight,parent);
        startX -= imgWidth;
        pad.drawImage(img,startX,startY,imgWidth, imgHeight,parent);
	}

	public static void fillRect( Graphics offLineBuffer, Color color, int x, int y, int width, int height ) {
		offLineBuffer.setColor(color);
		offLineBuffer.fillRect(x, y, width, height);
	}

	public static void fillGradientRect( Graphics2D offLineBuffer, Color startColor, Color endColor, int x, int y, int width, int height ) {
		Paint prevPaint = offLineBuffer.getPaint();
        GradientPaint gradPaint = new GradientPaint(x,y, startColor, 
            	x, y+height, endColor, true);
        offLineBuffer.setPaint(gradPaint);
		offLineBuffer.fillRect(x, y, width, height);
		
		offLineBuffer.setPaint( prevPaint );
	}

	public static void fill3DRect( Graphics2D offLineBuffer, Color color, int x, int y, int width, int height ) {
		offLineBuffer.setColor(color);
		offLineBuffer.fill3DRect(x, y, width, height, true);
	}

	public static void drawRect( Graphics offLineBuffer, Color color, int x, int y, int width, int height ) {
		offLineBuffer.setColor(color);
		offLineBuffer.drawRect(x, y, width, height);
	}

	public static void draw3DRect( Graphics offLineBuffer, Color color, int x, int y, int width, int height ) {
		offLineBuffer.setColor(color);
		offLineBuffer.draw3DRect(x, y, width, height, true);
	}

	public static void drawBorder( Graphics offLineBuffer, Color borderColor, int x, int y, int width, int height ) {
		offLineBuffer.setColor(borderColor);
		offLineBuffer.drawRect(x, y, width, height );
	}

	public static void drawLine( Graphics offLineBuffer, Color lineColor, int startX, int startY, int endX, int endY ) {
		offLineBuffer.setColor( lineColor );
		offLineBuffer.drawLine( startX, startY, endX, endY );
	}

	public static void drawDashedLine( Graphics offLineBuffer, Color lineColor, int x1, int y1, int x2, int y2, double dashLength, double spaceLength) {
		offLineBuffer.setColor( lineColor );
		drawDashedLine( offLineBuffer, x1, y1, x2, y2, dashLength, spaceLength);
	}

	public static void drawDashedLine( Graphics offLineBuffer, int x1, int y1, int x2, int y2, double dashLength, double spaceLength)
	{
	    if ((x1 == x2) && (y1 == y2))
	    {
	    	offLineBuffer.drawLine(x1, y1, x2, y2);
	            return;
	    }

	    double linelength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	    double xincdashspace = (x2 - x1) / (linelength / (dashLength + spaceLength));
	    double yincdashspace = (y2 - y1) / (linelength / (dashLength + spaceLength));
	    double xincdash = (x2 - x1) / (linelength / (dashLength));
	    double yincdash = (y2 - y1) / (linelength / (dashLength));
	    int counter = 0;
	    for (double i = 0; i < linelength - dashLength; i += dashLength + spaceLength)
	    {
	    	offLineBuffer.drawLine((int) (x1 + xincdashspace * counter), (int) (y1 + yincdashspace * counter), (int) (x1 + xincdashspace * counter + xincdash), (int) (y1 + yincdashspace * counter + yincdash));
            counter++;
	    }
	    if ((dashLength + spaceLength) * counter <= linelength)
	    {
	    	offLineBuffer.drawLine((int) (x1 + xincdashspace * counter), (int) (y1 + yincdashspace * counter), x2, y2);
	    }
	}

	public static void drawString( Graphics offLineBuffer, Color textColor, String text, int x, int y ) {
		offLineBuffer.setColor( textColor );
		offLineBuffer.drawString( text, x, y );
	}	

    public static Color makeBrighter( Color curColor, int distance )
    {
    	int red = curColor.getRed();
    	int green = curColor.getGreen();
    	int blue = curColor.getBlue();
    	
    	red += distance;
    	green += distance;
    	blue += distance;
    	
    	if( red > 255 ) red = 255;
    	if( green > 255 ) green = 255;
    	if( blue > 255 ) blue = 255;
    	
    	return new Color( red, green, blue );
    }

    public static Color makeDarker( Color curColor, int distance )
    {
    	int red = curColor.getRed();
    	int green = curColor.getGreen();
    	int blue = curColor.getBlue();
    	
    	red -= distance;
    	green -= distance;
    	blue -= distance;
    	
    	if( red <0) red = 0;
    	if( green < 0 ) green = 0;
    	if( blue < 0 ) blue = 0;
    	
    	return new Color( red, green, blue );
    }

    public static int getFontWidth( Graphics offLineBuffer, Font font, String text ) {
    	offLineBuffer.setFont(font);
        FontMetrics fontmetrics = offLineBuffer.getFontMetrics();
        return fontmetrics.stringWidth(text);
    }

    public static int getFontHeight( Graphics offLineBuffer, Font font ) {
    	offLineBuffer.setFont(font);
        FontMetrics fontmetrics = offLineBuffer.getFontMetrics();
        return fontmetrics.getHeight();
    }

}
