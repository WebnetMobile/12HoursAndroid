package com.tajchert.hours;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ColorManager {
	
	public static ArrayList<Integer> getList(SharedPreferences prefs){
		ArrayList<Integer> results = new ArrayList<Integer>();
		if(prefs == null){
			return null;
		}
		Set<String> colorSet = prefs.getStringSet(Tools.COLOR_LIST, new HashSet<String>());
		for(String str: colorSet){
			results.add(Integer.parseInt(str+""));
		}
		return results;
	}
	public static int[] getArray(SharedPreferences prefs){
		if(prefs == null){
			return null;
		}
		Set<String> colorSet = prefs.getStringSet(Tools.COLOR_LIST, new HashSet<String>());
		int results[] = new int[colorSet.size()];
		int i = 0;
		for(String str: colorSet){
			results[i] = Integer.parseInt(str+"");
			i++;
		}
		
		return results;
	}
	public static void saveList(SharedPreferences prefs, ArrayList<Integer> colors){
		if(prefs == null || colors == null){
			return;
		}
		Set<String> colorSet = new HashSet<String>();
		for(Integer col: colors){
			colorSet.add(col+"");
		}
		prefs.edit().putStringSet(Tools.COLOR_LIST, colorSet).apply();
	}
	
	public static void addColor(SharedPreferences prefs, Integer color){
		if(color == null){
			return;
		}
		ArrayList<Integer> existing =  getList(prefs);
		existing.add(color);
		saveList(prefs, existing);
	}
	
	public static void removeColor(SharedPreferences prefs, int position){
		if(prefs == null){
			return;
		}
		ArrayList<Integer> existing =  getList(prefs);
		existing.remove(position);
		saveList(prefs, existing);
	}
	public static void removeColor(SharedPreferences prefs, Integer color){
		if(color == null){
			return;
		}
		ArrayList<Integer> existing =  getList(prefs);
		existing.remove(color);
		//existing.remove(Color.parseColor(color+""));
		saveList(prefs, existing);
	}
}
