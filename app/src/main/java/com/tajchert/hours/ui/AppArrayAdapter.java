package com.tajchert.hours.ui;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tajchert.hours.R;
import com.tajchert.hours.WidgetInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
 
public class AppArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final static ArrayList<String> values = new ArrayList<String>();
	private final static TreeMap<String, Drawable> vals = new TreeMap<String, Drawable>(); 
	public static ArrayList<ApplicationInfo> appInstalled = new ArrayList<ApplicationInfo>();
 
	public AppArrayAdapter(Context context, PackageManager pck, WidgetInstance widget) {
		
		super(context, R.layout.list_apps, values);
		vals.clear();
		values.clear();
		//img.clear();
		appInstalled = new ArrayList<ApplicationInfo>();
		appInstalled = (ArrayList<ApplicationInfo>) pck.getInstalledApplications(PackageManager.GET_META_DATA);
		ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
		Drawable dr;
		for(ApplicationInfo app : appInstalled){
        	String name = (String) pck.getApplicationLabel(app);
        	if(name != null && name.length()>0){
        		dr = pck.getApplicationIcon(app);
        		values.add(name);
        		vals.put(name, dr);
        		//img.add(dr);
        	}
        }
		Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
		appInstalled = apps;
		this.context = context;
		//this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.list_apps, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
		textView.setText(values.get(position));
		imageView.setImageDrawable(vals.get(values.get(position)));
		
 
		return rowView;
	}
}