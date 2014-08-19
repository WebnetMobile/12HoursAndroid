package com.tajchert.hours.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tajchert.hours.CalendarObject;
import com.tajchert.hours.R;
import com.tajchert.hours.WidgetInstance;

import java.util.ArrayList;


public class CalendarListAdapter extends BaseAdapter {
	public ArrayList<CalendarObject> data = new ArrayList<CalendarObject>();
	public WidgetInstance widget;
	Context c;
	//SimpleDateFormat  format = new SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault());
	public CalendarListAdapter(ArrayList<CalendarObject> data, Context c, WidgetInstance widget) {
		this.data = data;
		this.c = c;
		if(widget!= null){
			this.widget= widget;
		}
	}
	public CalendarListAdapter(ArrayList<CalendarObject> data, Context c) {
		this.data = data;
		this.c = c;
	}
	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_market_item, null);
		}
		TextView NameText = (TextView) v.findViewById(R.id.NameText);
		TextView textViewOwner = (TextView) v.findViewById(R.id.textViewOwner);
		final CheckBox check = (CheckBox) v.findViewById(R.id.checkBoxSub);
		
		Button buttonTransparent = (Button) v.findViewById(R.id.buttonTransparent);
		
		buttonTransparent.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	if(check.isChecked()){
	        		check.setChecked(false);
	        		data.get(position).isChecked = false;
				} else {
					check.setChecked(true);
					data.get(position).isChecked = true;
				}
	        }
	    });
		
		CalendarObject obj = data.get(position);
		NameText.setText(obj.name);
		textViewOwner.setText(obj.owner);
		if(widget!= null){
			for(String name: widget.calendarNames){
				if(obj.name.equals(name)){
					data.get(position).isChecked = true;
				}
			}
		}
		
		check.setChecked(data.get(position).isChecked);
		return v;
	}
}