package com.tajchert.hours;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WidgetListManager {
	private static String separator = "<!!!>";
	static String separatorBig = "<|||>";
	
	public static void saveToSharedPrefs(Set<String> cals, SharedPreferences prefs){
		//Set<String> cals = new HashSet<String>();
		prefs.edit().putStringSet(Tools.WIDGET_CALENDAR_LIST, cals).commit();
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
		cals.add(widgetId + separator + content);
		saveMap(setToMap(cals), prefs);
	}
	public static void addWidget(WidgetInstance widget, SharedPreferences prefs){
		Log.e(Tools.AWESOME_TAG, "Adding widget: " + widget.id);
		Log.d(Tools.AWESOME_TAG, "ADD new layout:" + widget.style);
		Gson gson = new Gson();
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		cals.add(widget.id + separator + gson.toJson(widget));
		//Log.d(Tools.AWESOME_TAG, "To Gson: " + gson.toJson(widget));
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
				  WidgetInstance widget = gson.fromJson(result.get(key), WidgetInstance.class);
				  return widget;
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
	
	public static void saveWidgets(ArrayList<WidgetInstance> arr, SharedPreferences prefs){
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		for(WidgetInstance widget: arr){
			if(result.containsKey(widget.id)){
				Gson gson = new Gson();
				result.put(widget.id, gson.toJson(widget));
				
			}else{
				addWidget(widget, prefs);
			}
		}
		saveMap(result, prefs);
	}
	
	public static ArrayList<Integer> getWidgetIds(SharedPreferences prefs){
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		Iterator<Integer> keySetIterator = result.keySet().iterator();
		while(keySetIterator.hasNext()){
		  Integer key = keySetIterator.next();
		  ids.add(key);
		}
		return ids;
	}
	public static boolean isExistsWidget(int widgetId, SharedPreferences prefs){
		Set<String> cals = new HashSet<String>(prefs.getStringSet(Tools.WIDGET_CALENDAR_LIST, new HashSet<String>()));
		HashMap<Integer, String> result = setToMap(cals);
		if(result.size()>0){
			if(result.containsKey(widgetId)){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	public static HashMap<Integer, String> setToMap(Set<String> cals){
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		for(String in: cals){
			String [] content = in.split(separator);
			result.put(Integer.parseInt(content[0]), content[1]);
		}
		return result;
	}
	
	public static void saveMap(HashMap<Integer, String> cals, SharedPreferences prefs){
		Set<String> calsSet = new HashSet<String>();
		//Log.d(Tools.AWESOME_TAG, "Size: " + cals.size());
		Iterator<Integer> keySetIterator = cals.keySet().iterator();
		while(keySetIterator.hasNext()){
		  Integer key = keySetIterator.next();
		  calsSet.add(key + separator + cals.get(key));
		  //Log.d(Tools.AWESOME_TAG, key + separator + cals.get(key));
		}
		saveToSharedPrefs(calsSet, prefs);
	}
}
