package com.tajchert.hours.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tajchert.hours.R;


public class ColorListAdapter extends BaseAdapter {
	int [] data;
	Context c;
	private boolean clickable;

	public ColorListAdapter(int[] data, Context c, boolean clickable) {
		if(clickable){
			this.data = new int[(data.length+1)];
			System.arraycopy( data, 0, this.data, 0, data.length);
		}else{
			this.data = data;
		}
		this.c = c;
		this.clickable = clickable;
	}
	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return data[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_colors_item, null);
		}
		RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.colorLayout);
		TextView nameText = (TextView) v.findViewById(R.id.NameText);

		if(!clickable){
			nameText.setText("#"+data[position]);
			rl.setBackgroundColor(data[position]);
		}else{
			if(position<(data.length-1)){
				nameText.setText("#"+data[position]);
				rl.setBackgroundColor(data[position]);
			}else{
				nameText.setText(c.getResources().getString(R.string.color_add_addbutton));
			}
		}
		return v;
	}
}