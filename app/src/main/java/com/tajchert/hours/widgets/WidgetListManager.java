package com.tajchert.hours.widgets;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.tajchert.hours.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WidgetListManager {
	private static String SEPARATOR = "<!!!>";

	public static void saveToSharedPrefs(Set<String> cals, SharedPreferences prefs){
		prefs.edit().putStringSet(Tools.WIDGET_CALENDAR_LIST, cals).apply();
	}
	public static void removeWidget(int widgetId, SharedPreferences prefs){
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		if(result.size()>0){
			if(result.containsKey(widgetId)){
				result.remove(widgetId);
				saveMap(result, prefs);
			}else{
				//No widgetId on list
			}
		}else{
			//No list at all
		}
	}
	public static void updateWidget(int widgetId, SharedPreferences prefs, WidgetInstance newWidget){
		Log.d(Tools.AWESOME_TAG, "UPDATE size:" + newWidget.calendarNames.length);
		Log.d(Tools.AWESOME_TAG, "UPDATE new layout:" + newWidget.style);
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		if(result.size()>0){
			if(result.containsKey(widgetId)){
				Gson gson = new Gson();
				result.put(widgetId, gson.toJson(newWidget));
				saveMap(result, prefs);
			}else{
				addWidget(newWidget, prefs);
				//No widgetId on list
			}
		}else{
			//No list at all
		}
	}
	public static void addWidget(int widgetId, String content, SharedPreferences prefs){
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		cals.add(widgetId + SEPARATOR + content);
		saveMap(setToMap(cals), prefs);
	}
	public static void addWidget(WidgetInstance widget, SharedPreferences prefs){
		Log.e(Tools.AWESOME_TAG, "Adding widget: " + widget.id);
		Log.d(Tools.AWESOME_TAG, "ADD new layout:" + widget.style);
		Gson gson = new Gson();
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		cals.add(widget.id + SEPARATOR + gson.toJson(widget));
		saveMap(setToMap(cals), prefs);
	}
	
	public static WidgetInstance getWidgetInstance(SharedPreferences prefs, String id){
		
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		
		HashMap<Integer, String> result = setToMap(cals);
		if(result.size()>0){
			Iterator<Integer> keySetIterator = result.keySet().iterator();
			while(keySetIterator.hasNext()){
			  Integer key = keySetIterator.next();
			  if((key+"").equals(id)){
				  Gson gson = new Gson();
                  return gson.fromJson(result.get(key), WidgetInstance.class);
			  }
			}
		}else{
			//No list at all
			return null;
		}
		return null;
	}
	public static ArrayList<WidgetInstance> getWidgets(SharedPreferences prefs){
		ArrayList<WidgetInstance> ids = new ArrayList<WidgetInstance>();
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		Gson gson = new Gson();
		Iterator<Integer> keySetIterator = result.keySet().iterator();
		while(keySetIterator.hasNext()){
		  Integer key = keySetIterator.next();
		  ids.add(gson.fromJson(result.get(key), WidgetInstance.class));
		}
		return ids;
	}
	public static HashMap<Integer, String> setToMap(Set<String> cals){
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		for(String in: cals){
			String [] content = in.split(SEPARATOR);
			result.put(Integer.parseInt(content[0]), content[1]);
		}
		return result;
	}
	
	public static void saveMap(HashMap<Integer, String> cals, SharedPreferences prefs){
		Set<String> calsSet = new HashSet<String>();
		Iterator<Integer> keySetIterator = cals.keySet().iterator();
		while(keySetIterator.hasNext()){
		  Integer key = keySetIterator.next();
		  calsSet.add(key + SEPARATOR + cals.get(key));
		}
		saveToSharedPrefs(calsSet, prefs);
	}
}
