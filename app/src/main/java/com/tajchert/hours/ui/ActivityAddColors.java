package com.tajchert.hours.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorSelectedListener;
import com.tajchert.hours.ColorManager;
import com.tajchert.hours.R;

public class ActivityAddColors extends ActionBarActivity implements OnColorSelectedListener{
	private SharedPreferences prefs;
	private int color;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        ColorPicker picker;
        Button addButton;

		super.onCreate(savedInstanceState);
        Dialog dialog = new Dialog(ActivityAddColors.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.activity_add_colors);
		prefs = getSharedPreferences("com.tajchert.hours", MODE_PRIVATE);
		
		addButton = (Button) dialog.findViewById(R.id.add_button);
		picker = (ColorPicker) dialog.findViewById(R.id.picker);
		
		picker.setShowOldCenterColor(false);
		picker.setOnColorSelectedListener(this);
		
		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ColorManager.addColor(prefs, color);
				Intent intent = new Intent(ActivityAddColors.this, ActivityColorManagment.class);
                startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onColorSelected(int colorIn) {
		color = colorIn;
	}
}
