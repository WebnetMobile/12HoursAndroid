package com.tajchert.hours.lists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.widgets.Widget;
import com.tajchert.hours.widgets.WidgetInstance;
import com.tajchert.hours.ui.ActivityWidgetSettings;

import java.util.ArrayList;


public class WidgetsListAdapter extends BaseAdapter {
	public ArrayList<WidgetInstance> data = new ArrayList<WidgetInstance>();
	Context c;

	public WidgetsListAdapter(ArrayList<WidgetInstance> data, Context c) {
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
		final WidgetInstance widget = data.get(position);
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.widget_list_item, null);
		}
		//TextView id = (TextView) v.findViewById(R.id.labelId);
		ImageView viewUnder = (ImageView) v.findViewById(R.id.imageViewClockUnderflow);
		ImageView viewOver = (ImageView) v.findViewById(R.id.imageViewClockOverflow);
		TextView calNum = (TextView) v.findViewById(R.id.labelCalNumber);
		
		Button buttonTransparent = (Button) v.findViewById(R.id.buttonTransparent);
		
		Bitmap clockBackground = null;
		if(widget.style == 0){
			clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.hand_dial), 150, 150, false);
			widget.setDimensions(Tools.clock_layouts_dimensions_small[0]);
		}else if(widget.style == 1){
			clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.hand_nodigits_dial), 150, 150, false);
			widget.setDimensions(Tools.clock_layouts_dimensions_small[1]);
		}else if(widget.style == 2){
			clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.kit_kat_hand_dial), 150, 150, false);
			widget.setDimensions(Tools.clock_layouts_dimensions_small[2]);
		}else if(widget.style == 3){
			clockBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.hand_whitefull_dial), 150, 150, false);
			widget.setDimensions(Tools.clock_layouts_dimensions_small[3]);
		}
		viewOver.setImageBitmap(Widget.addStaticWidget(c, widget));
		viewUnder.setImageBitmap(clockBackground);
		
		buttonTransparent.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	Intent intent = new Intent(c, ActivityWidgetSettings.class);
	        	Bundle b = new Bundle();
	        	b.putString("widgetID", widget.id+""); //Your id
	        	intent.putExtras(b); //Put your id to your next Intent
	        	c.startActivity(intent);
	        }
	    });
		if(widget.calendarNames != null){
			String cNames="";
			for(int i=0; i < widget.calendarNames.length;i++){
				cNames += widget.calendarNames[i] +", ";
			}
			if(cNames != null && cNames.length()>2){
				cNames = cNames.substring(0, cNames.length()-2);
				calNum.setText(cNames +"");
			}else{
				calNum.setText("");
			}
		}else{
			calNum.setText("");
		}
		return v;
	}
}