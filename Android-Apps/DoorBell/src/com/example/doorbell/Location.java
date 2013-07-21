package com.example.doorbell;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Location extends Activity implements OnClickListener{
	Spinner homes,floors,rooms,inRoom,favorite;
	Button done,dontSet;
	List<String> homeList,floorList,roomList,inRoomList,favoriteList;
	String TAG = "DEBUG";
	TextView t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

		Log.d(TAG, "Started the location UI");
		Intent serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		startService(serviceIntent);

		Log.d(TAG, "Asking for homes");
		serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "getHomes");
		startService(serviceIntent);

		Log.d(TAG, "Asking for floors");
		serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "getFloors");
		startService(serviceIntent);

		Log.d(TAG, "Asking for rooms");
		serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "getRooms");
		startService(serviceIntent);

		Log.d(TAG, "Asking for usertags");
		serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "getUserTags");
		startService(serviceIntent);	
		populateInRoom();
		addListenerOnButton();
		t = (TextView)findViewById(R.id.previousLocation);
		t.setText("PREVIOUS LOCATION SETTING :"+"Home : "+p.getString("home","")+" "+"Floor : "+ p.getString("floor","")+" "+
				"Room : "+p.getString("room","")+" "+"In room : "+p.getString("inRoom","")+" "+ "Favorite : "+ 
				p.getString("favorite",""));

	}

	public void addListenerOnButton() {
		done= (Button) findViewById(R.id.locationDone);
		done.setOnClickListener(this);
		dontSet = (Button) findViewById(R.id.locationDontSet);
		dontSet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		switch(v.getId()){
		case R.id.locationDone: Intent serviceIntent = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent.putExtra("configuration", "LOCATION");
		serviceIntent.putExtra("set",true);
		if(!homes.getSelectedItem().toString().equals("select home")){
			p.edit().putString("home",homes.getSelectedItem().toString()).commit();
			Log.v(TAG,"Home : "+homes.getSelectedItem().toString());
			serviceIntent.putExtra("home", homes.getSelectedItem().toString());
		}
		else{
			serviceIntent.putExtra("home","notset");
			Log.v(TAG,"Home : "+homes.getSelectedItem().toString());
			p.edit().putString("home","notset").commit();
		}

		if(!floors.getSelectedItem().toString().equals("select floor")){
			serviceIntent.putExtra("floor",floors.getSelectedItem().toString());
			Log.v(TAG,"Floors : "+floors.getSelectedItem().toString());
			p.edit().putString("floor",floors.getSelectedItem().toString()).commit();
		}
		else{
			serviceIntent.putExtra("floor","notset");
			Log.v(TAG,"Floors : "+floors.getSelectedItem().toString());
			p.edit().putString("floor","notset").commit();
		}

		if(!rooms.getSelectedItem().toString().equals("select room")){
			serviceIntent.putExtra("room", rooms.getSelectedItem().toString());
			p.edit().putString("room",rooms.getSelectedItem().toString()).commit();
		}
		else{
			serviceIntent.putExtra("room","notset");
			p.edit().putString("room","notset").commit();
		}

		if(!inRoom.getSelectedItem().toString().equals("select in room")){
			serviceIntent.putExtra("inRoom", inRoom.getSelectedItem().toString());
			p.edit().putString("inRoom",inRoom.getSelectedItem().toString()).commit();
		}
		else{
			serviceIntent.putExtra("inRoom","notset");
			p.edit().putString("inRoom","notset").commit();
		}

		if(!favorite.getSelectedItem().toString().equals("select user tag")){
			serviceIntent.putExtra("favorite", favorite.getSelectedItem().toString());
			p.edit().putString("favorite",favorite.getSelectedItem().toString()).commit();
		}
		else{
			serviceIntent.putExtra("favorite","notset");
			p.edit().putString("favorite","notset").commit();
		}
		startService(serviceIntent);
		break;
		
		case R.id.locationDontSet :	Intent serviceIntent1 = new Intent(Location.this, DoorBellConfigurationService.class);
		serviceIntent1.putExtra("configuration", "LOCATION");
		serviceIntent1.putExtra("set",false);
		startService(serviceIntent1);
		}
		Intent intent1 = new Intent(context, MainActivity.class);
		startActivity(intent1);
	}


	private void populateFavorite() {
		favorite= (Spinner) findViewById(R.id.Favorite);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, favoriteList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		favorite.setAdapter(dataAdapter);

	}	


	private void populateHome() {

		homes = (Spinner) findViewById(R.id.HomeSpinner);


		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, homeList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		homes.setAdapter(dataAdapter);

	}

	private void populateInRoom() {

		inRoom= (Spinner) findViewById(R.id.InRoomSpinner);
		inRoomList = new ArrayList<String>();
		inRoomList.add("select in room");
		inRoomList.add("left");
		inRoomList.add("front");
		inRoomList.add("back");
		inRoomList.add("right");
		inRoomList.add("top");
		inRoomList.add("down");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,inRoomList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inRoom.setAdapter(dataAdapter);

	}

	private void populateRooms() {

		rooms = (Spinner) findViewById(R.id.RoomSpinner);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, roomList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rooms.setAdapter(dataAdapter);

	}
	private void populateFloors() {

		floors = (Spinner) findViewById(R.id.FloorSpinner);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, floorList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		floors.setAdapter(dataAdapter);

	}	
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
			Log.d(TAG," location : Intent from service" + intent.getStringExtra("Location"));
			if(intent.getStringExtra("Location").equals("Home"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				homeList = new ArrayList<String>();
				homeList.add("select home");
				for(String value:values)
					homeList.add(value);
				populateHome();
			}
			else if(intent.getStringExtra("Location").equals("Floor"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				floorList = new ArrayList<String>();
				floorList.add("select floor");
				for(String value:values)
					floorList.add(value);
				populateFloors();
			}
			else if(intent.getStringExtra("Location").equals("Room"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				roomList = new ArrayList<String>();
				roomList.add("select room");
				for(String value:values)
					roomList.add(value);
				populateRooms();
			}
			else if(intent.getStringExtra("Location").equals("UserTag"))
			{
				ArrayList<String> values = intent.getStringArrayListExtra("Values");
				favoriteList = new ArrayList<String>();
				favoriteList.add("select user tag");
				for(String value:values)
					favoriteList.add(value);
				populateFavorite();
			}
		}
	};




}
