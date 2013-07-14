package com.example.locationapp;


import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class LocationUI extends Activity {

	private Button setLocation; 
	private String TAG = "LocationService";
	private EditText userNameip = null;
	private AutoCompleteTextView homeName = null;
	private ArrayAdapter<String> homeAdapter;
	private AutoCompleteTextView floorName = null;
	private ArrayAdapter<String> floorAdapter;
	private AutoCompleteTextView roomName = null;
	private ArrayAdapter<String> roomAdapter;
	private AutoCompleteTextView inRoomName = null;
	private ArrayAdapter<String> inRoomAdapter;
	private AutoCompleteTextView userTagName = null;
	private ArrayAdapter<String> userTagAdapter;
	
	String inRoom[]={
			"Top", "Bottom", "Right", "Left",
			"Front", "Back"
	};
	
	
	private String notSetCheck(String str) {
		if(str.length()>0)
			return str;
		else 
			return "notset";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_ui);

		userNameip = (EditText)findViewById(R.id.editText1);

		setLocation = (Button)findViewById(R.id.button1);
		setLocation.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				Log.d(TAG,"build location string");
				
				String home = homeName.getText().toString();
				home = notSetCheck(home);
				String floor = floorName.getText().toString();
				floor = notSetCheck(floor);
				String room = roomName.getText().toString();
				room = notSetCheck(room);
				String inRoom = inRoomName.getText().toString();
				inRoom = notSetCheck(inRoom);
				String userTag = userTagName.getText().toString();
				userTag = notSetCheck(userTag);
				
				Log.d(TAG,"Send Intent to service");
				Intent serviceIntent = new Intent(LocationUI.this,LocationService.class);
				serviceIntent.putExtra("command", "SetLocation");
				serviceIntent.putExtra("user", userNameip.getText().toString());
				serviceIntent.putExtra("location", home+"+"+floor+"+"+room+"+"+inRoom+"+"+userTag);
				Log.d(TAG,"Send Intent to service "+home+"+"+floor+"+"+room+"+"+inRoom+"+"+userTag);
				startService(serviceIntent);

			}
		});

		homeName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
		homeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		homeName.setAdapter(homeAdapter);
		homeName.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				homeName.showDropDown();
				return false;
			}

		});

		floorName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView2);
		floorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		floorName.setAdapter(floorAdapter);
		floorName.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				floorName.showDropDown();
				return false;
			}

		});

		
		roomName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView3);
		roomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		roomName.setAdapter(roomAdapter);
		roomName.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				roomName.showDropDown();
				return false;
			}

		});

		inRoomName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView4);
		inRoomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,inRoom);
		inRoomName.setAdapter(inRoomAdapter);
		inRoomName.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				inRoomName.showDropDown();
				return false;
			}

		});
		
		userTagName = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView5);
		userTagAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		userTagName.setAdapter(userTagAdapter);
		userTagName.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event){
				userTagName.showDropDown();
				return false;
			}

		});
		
		
		
		Log.d(TAG, "Started the UI Activity staring service");
		Intent serviceIntent = new Intent(LocationUI.this, LocationService.class);
		startService(serviceIntent);

		serviceIntent = new Intent(LocationUI.this, LocationService.class);
		serviceIntent.putExtra("command", "getHomes");
		startService(serviceIntent);
		
		serviceIntent = new Intent(LocationUI.this, LocationService.class);
		serviceIntent.putExtra("command", "getFloors");
		startService(serviceIntent);
		
		serviceIntent = new Intent(LocationUI.this, LocationService.class);
		serviceIntent.putExtra("command", "getRooms");
		startService(serviceIntent);
		
		serviceIntent = new Intent(LocationUI.this, LocationService.class);
		serviceIntent.putExtra("command", "getUserTags");
		startService(serviceIntent);
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_ui, menu);
		return true;
	}
	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("UIINTENT"));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG,"Intent from service" + intent.getStringExtra("Location"));
			if(intent.getStringExtra("Location").equals("Home"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				for(String value:values)
					homeAdapter.add(value);
			}
			else if(intent.getStringExtra("Location").equals("Floor"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				for(String value:values)
					floorAdapter.add(value);
			}
			else if(intent.getStringExtra("Location").equals("Room"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				for(String value:values)
					roomAdapter.add(value);
			}
			else if(intent.getStringExtra("Location").equals("UserTag"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				for(String value:values)
					userTagAdapter.add(value);
			}
		}
	};

}
