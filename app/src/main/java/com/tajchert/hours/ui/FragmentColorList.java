package com.tajchert.hours.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tajchert.hours.ColorManager;
import com.tajchert.hours.R;
import com.tajchert.hours.Tools;
import com.tajchert.hours.lists.ColorListAdapter;


public class FragmentColorList extends Fragment {

	private ListView colorListView;
	public static String ARG_PARAM1 ="colorNumber";
	private int colorNumber = 0;
	private SharedPreferences prefs;
	private ColorListAdapter adapter = null;

	public FragmentColorList() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView title;

		View v = inflater.inflate(R.layout.fragment_color_list, container, false);
		prefs = getActivity().getSharedPreferences("com.tajchert.hours", Context.MODE_PRIVATE);
		
		if (getArguments() != null) {
			colorNumber = Integer.parseInt(getArguments().getInt(ARG_PARAM1)+"");
		}
		title = (TextView) v.findViewById(R.id.textViewTitle);
		colorListView = (ListView) v.findViewById(R.id.listViewColors);
		
		
		switch(colorNumber){
		case 0:
			title.setText(getString(R.string.tab_style_color_mild));
			adapter = new ColorListAdapter(Tools.colors_mild, getActivity(), false);
			break;
		case 1:
			title.setText(getString(R.string.tab_style_color_aggressive));
			adapter = new ColorListAdapter(Tools.colors_aggressive, getActivity(), false);
			break;
		case 2:
			title.setText(getString(R.string.tab_style_color_blind));
			adapter = new ColorListAdapter(Tools.colors_blind, getActivity(), false);
			break;
		case 3:
			title.setText(getString(R.string.tab_style_color_custom));
			adapter = new ColorListAdapter(ColorManager.getArray(prefs), getActivity(), true);
			
			break;
		default:
			break;
		}
		if(adapter != null){
			colorListView.setAdapter(adapter);
			colorListView.setDividerHeight(0);
			if(colorNumber==3){
				colorListView.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view, final int position,long id) {
		            	if(position==9){
		            		Toast.makeText(getActivity(), "Too many colors...", Toast.LENGTH_SHORT).show();
		            	}else if(position>=ColorManager.getArray(prefs).length){
		                	Log.d(Tools.AWESOME_TAG,"ADD COLOR");
		                	Intent intent = new Intent(getActivity(), ActivityAddColors.class);
		                    startActivity(intent);
		                }else{
		                	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		                	    @Override
		                	    public void onClick(DialogInterface dialog, int which) {
		                	        switch (which){
		                	        case DialogInterface.BUTTON_POSITIVE:
		                	            ColorManager.removeColor(prefs, position);
		                	            adapter = new ColorListAdapter(ColorManager.getArray(prefs), getActivity(), true);
		                		    	adapter.notifyDataSetChanged();
		                		    	colorListView.setAdapter(adapter);
		                		    	 colorListView.setDividerHeight(0);
		                	            break;

		                	        case DialogInterface.BUTTON_NEGATIVE:
		                	            //No button clicked
		                	            break;
		                	        }
		                	    }
		                	};
		                	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		                	builder.setMessage(getResources().getString(R.string.color_delete_question)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
		                	    .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
		                }
		                
		            }
		        });
			}
		}
		return v;
	}
	
	@Override
	  public void onResume() {
	     super.onResume();
	     Log.d(Tools.AWESOME_TAG,"onResume");
	     if(adapter!=null && colorNumber == 3){
	    	 Log.d(Tools.AWESOME_TAG,"adapter size:"+adapter.getCount());
	    	 adapter = new ColorListAdapter(ColorManager.getArray(prefs), getActivity(), true);
	    	 adapter.notifyDataSetChanged();
	    	 colorListView.setAdapter(adapter);
	    	 colorListView.setDividerHeight(0);
	    	 Log.d(Tools.AWESOME_TAG,"adapter size:"+adapter.getCount());
	     }
	  }

}
