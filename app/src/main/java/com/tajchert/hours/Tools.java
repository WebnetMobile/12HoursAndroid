package com.tajchert.hours;

import android.graphics.Color;

public class Tools {
	
	public final static String AWESOME_TAG = "24Hours";
	public final static int [] clock_layouts = {R.drawable.hand_dial, R.drawable.hand_nodigits_dial, R.drawable.kit_kat_hand_dial, R.drawable.hand_whitefull_dial};
	public final static float [][] clock_layouts_dimensions_small = new float [] [] {{52.5f, 112.5f, 111f, 11f, 250f}, {52.5f, 112.5f, 111f, 11f, 250f}, {55.75f, 115.5f, 113f, 6.75f, 250f}, {55.75f, 115.5f, 113f, 6.75f, 250f}};
	public final static float [][] clock_layouts_dimensions_medium = new float [] [] {{73.5f, 158.2f, 155.4f, 15.4f, 350f}, {73.5f, 158.2f, 155.4f, 15.4f, 350f}, {78.05f, 161.7f, 158.2f, 9.45f, 350f}, {78.05f, 161.7f, 158.2f, 9.45f, 350f}};
	public final static float [][] clock_layouts_dimensions_big = new float [] [] {{105f, 226f, 222f, 22f, 500f}, {105f, 226f, 222f, 22f, 500f}, {111.5f, 231f, 226f, 13.5f, 500f}, {111.5f, 231f, 226f, 13.5f, 500f}};
	
	public final static int [] colors_mild = {Color.parseColor("#48e7bc"), Color.parseColor("#ed9aff"), 
		Color.parseColor("#bae515"), Color.parseColor("#23e3ff"), Color.parseColor("#3490ef"), Color.parseColor("#ff7853")};
	public final static int [] colors_aggressive  = {Color.parseColor("#fa16bc"), Color.parseColor("#feda12"), 
		Color.parseColor("#6ff939"), Color.parseColor("#02ffcc"), Color.parseColor("#00a2ff"), Color.parseColor("#e94028")};
	public final static int [] colors_blind = {Color.parseColor("#ff0000"), Color.parseColor("#ffff00"), 
		Color.parseColor("#00ee00"), Color.parseColor("#00eeee"), Color.parseColor("#0000ff"), Color.parseColor("#900090")};

	public final static String COLOR_LIST_TYPE = "com.tajchert.hours.colorslisttype";

	public static String TIME_WIDGET_ALL_UPDATES = "com.tajchert.hours.timewidgetupdate";
	public static String TRANSPARENCY_INNER = "com.tajchert.hours.innertransparency";
	public static String TRANSPARENCY_OUTER = "com.tajchert.hours.outtransparency";
	public static String TRANSPARENCY_CENTER = "com.tajchert.hours.centertransparency";
	
	public static String WIDGET_CALENDAR_LIST = "com.tajchert.hours.widgetslist";
	public static String COLOR_LIST = "com.tajchert.hours.colorlist";
	
	public static String WIDGET_FIRSTADD = "com.tajchert.hours.firstadd";
    public static String WIDGET_FIRSTRUN = "com.tajchert.hours.firstrun";
	
	public static String RESULUTION_GOT = "com.tajchert.hours.resolutiongot";

	
	//DEFAULT VALUES
	public final static int innerTransparency = 60;
	public final static int gradientTransparency = 120;
	public final static int outerTransparency = 210;


}
