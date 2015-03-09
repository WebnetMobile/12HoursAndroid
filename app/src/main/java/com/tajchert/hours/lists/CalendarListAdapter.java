package com.tajchert.hours.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.tajchert.hours.R;
import com.tajchert.hours.calendar.CalendarObject;
import com.tajchert.hours.widgets.WidgetInstance;

import java.util.ArrayList;


public class CalendarListAdapter extends BaseAdapter {
    private static final int HIGHLIGHT_COLOR = 0x999be6ff;
    private TextDrawable.IBuilder mDrawableBuilder;
    public ArrayList<CalendarObject> data = new ArrayList<CalendarObject>();
    public WidgetInstance widget;
    Context c;
	public CalendarListAdapter(ArrayList<CalendarObject> data, Context c, WidgetInstance widget) {
		this.data = data;
		this.c = c;
		if(widget!= null){
			this.widget= widget;
		}
        mDrawableBuilder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig().round();
	}
	public CalendarListAdapter(ArrayList<CalendarObject> data, Context c) {
		this.data = data;
		this.c = c;
        mDrawableBuilder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig().round();
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
			v = vi.inflate(R.layout.list_calendar_item, null);
		}
		TextView NameText = (TextView) v.findViewById(R.id.NameText);
		TextView textViewOwner = (TextView) v.findViewById(R.id.textViewOwner);
        final ImageView calendarIcon = (ImageView) v.findViewById(R.id.calendarIcon);
        final ImageView check_icon = (ImageView) v.findViewById(R.id.check_icon);
		Button buttonTransparent = (Button) v.findViewById(R.id.buttonTransparent);
		
		buttonTransparent.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	if(data.get(position).isChecked ){
	        		data.get(position).isChecked = false;
				} else {
					data.get(position).isChecked = true;
				}
                setCheckIcon(calendarIcon, check_icon, data.get(position).isChecked, data.get(position).name.substring(0,1),data.get(position).color);
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
        if(obj.name != null && obj.name.length() >= 1){
            TextDrawable drawable1 =  mDrawableBuilder.build(obj.name.substring(0, 1), obj.color);
            calendarIcon.setImageDrawable(drawable1);
            setCheckIcon(calendarIcon, check_icon, data.get(position).isChecked, data.get(position).name.substring(0,1),data.get(position).color);
        }
		//check.setChecked(data.get(position).isChecked);
		return v;
	}

    private void setCheckIcon(ImageView viewText, ImageView checkMark, boolean isChecked, String character, int color) {
        if(isChecked){
            viewText.setImageDrawable(mDrawableBuilder.build(" ", 0xff616161));
            checkMark.setVisibility(View.VISIBLE);
        } else {
            TextDrawable drawable = mDrawableBuilder.build(character, color);
            viewText.setImageDrawable(drawable);
            checkMark.setVisibility(View.GONE);
        }

    }
}