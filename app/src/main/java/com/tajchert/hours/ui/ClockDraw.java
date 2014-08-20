package com.tajchert.hours.ui;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;

import com.tajchert.hours.ColorManager;
import com.tajchert.hours.Tools;
import com.tajchert.hours.WidgetInstance;

import java.util.Map.Entry;
import java.util.TreeMap;

public class ClockDraw {
	
	public  float radiusIn;
	public  float radiusOut;
	public  float widthIn = 133;
	public  float widthOut = 14;
	
	private  int maxVal;
	private  int maxValColor;
	private  int minVal;
	private  int minValColor;
	
	public SharedPreferences prefs;

	TreeMap<Long, Integer> colors = new TreeMap<Long, Integer>(); 
	public void drawEvent(int startAngle, int EndAngle, Canvas canvas, float opacityInner, float opacityOuter, int calColor, long startDate, int size, WidgetInstance widget){
		int [] colorList = Tools.colors_mild;
		int listType;
		int transparencyCenter;
		final RectF oval = new RectF();
		final RectF ovalOut = new RectF();
		if(widget == null && prefs != null){
			listType = prefs.getInt(Tools.COLOR_LIST_TYPE, 0);
			transparencyCenter = prefs.getInt(Tools.TRANSPARENCY_CENTER, Tools.gradientTransparency);
		}else{
			listType = widget.colorPallete;
			transparencyCenter = widget.transparencyCenter;
		}
		size = size/2;
		int sweepAngle = 0;
		if(EndAngle >= startAngle){
			 sweepAngle = EndAngle- startAngle;
		}else{
			sweepAngle = (360 - startAngle) +  EndAngle;
		}
		if(sweepAngle==359){
			sweepAngle=360;
		}
		Paint p = new Paint();
		// smooths
		p.setAntiAlias(true);
		switch(listType){
			case 0:
				colorList = Tools.colors_mild;
				break;
			case 1:
				colorList = Tools.colors_aggressive;
				break;
			case 2:
				colorList = Tools.colors_blind;
				break;
			case 3:
				colorList = ColorManager.getArray(prefs);
				
				break;
		}
		int colorId = (int) (Math.random() *( colorList.length+1));
		while (colorId == closestUp(startDate, colors) || colorId == closestDown(startDate, colors)) {
			colorId = (int) (Math.random() * colorList.length);
		}
		if(startAngle>maxVal){
			maxVal = startAngle;
			while(colorId == minValColor){
				colorId = (int) (Math.random() *( colorList.length+1));
			}
			maxValColor = colorId;
		}
		if(colors.size()>0 && startAngle < minVal){
			minVal = startAngle;
			while(colorId == maxValColor){
				colorId =(int) (Math.random() *( colorList.length+1));
			}
			minValColor = colorId;
		}
		startAngle = startAngle-90;
		colors.put(startDate, colorId);
		RadialGradient gradient;
		colorId--;
		if(transparencyCenter == 0){
			transparencyCenter=1;
		}
		if(calColor != 0){
			gradient = new RadialGradient(size, size, transparencyCenter, Color.TRANSPARENT, calColor, android.graphics.Shader.TileMode.CLAMP);
		}else{
			gradient = new RadialGradient(size, size, transparencyCenter, Color.TRANSPARENT, colorList[colorId], android.graphics.Shader.TileMode.CLAMP);
		}
		p.setDither(true);
	    p.setShader(gradient);

		p.setStyle(Paint.Style.STROKE); 
		p.setStrokeWidth(widthIn);
		p.setAlpha((int) opacityInner);
		
		oval.set(size - radiusIn, size - radiusIn, size + radiusIn, size + radiusIn);
		canvas.drawArc(oval, startAngle, sweepAngle-0.0001f, false, p);

		p.setColor(colorList[colorId]);
		p.setStyle(Paint.Style.STROKE); 
		p.setStrokeWidth(widthOut);
		p.setAlpha((int) opacityOuter);
		
		ovalOut.set(size - radiusOut, size - radiusOut, size + radiusOut, size + radiusOut);
		canvas.drawArc(ovalOut, startAngle, sweepAngle-0.0001f, false, p);
	}
	
	/**
	 * Closest down.
	 *
	 * @param insertedColourTime the of
	 * @param in the TreeMap<Long, Integer> of values
	 * @return the int
	 */
	public int closestDown(long insertedColourTime, TreeMap<Long, Integer>in) {
	    long min = Integer.MAX_VALUE;
	    Integer closest = 0;
	    for(Entry<Long, Integer> entry : in.entrySet()) {
	    	  Long entryTime = entry.getKey();
	    	  Integer entryValue = entry.getValue();
	    	  if(entryTime < insertedColourTime){
		    	  final long diff = Math.abs(entryTime - insertedColourTime);
			        if (diff < min) {
			            min = diff;
			            closest = entryValue;
			        }
	    	  }
	    	}
	    return closest;
	}
	public int closestUp(long insertedColourTime, TreeMap<Long, Integer>in) {
	    long min = Integer.MAX_VALUE;
	    Integer closest = 0;
	    for(Entry<Long, Integer> entry : in.entrySet()) {
	    	  Long entryTime = entry.getKey();
	    	  Integer entryValue = entry.getValue();
	    	  if(entryTime > insertedColourTime){
		    	  final long diff = Math.abs(entryTime - insertedColourTime);
			        if (diff < min) {
			            min = diff;
			            closest = entryValue;
			        }
	    	  }
	    	}
	    return closest;
	}
	

}
