package com.example.locationrepresentation;

import java.util.ArrayList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ViewMap extends Activity implements OnClickListener{

	private Button back,done;
	private Spinner homeSpinner, floorSpinner, roomSpinner;
	private BroadcastReceiver receiver;
	private ArrayList<String> homeList;
	private ArrayList<String> floorList;
	private ArrayList<String> roomList;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_map);
		init();
		// first send intent to the registration service to
		// get the locations user has entered
		Intent getMap = new Intent(ViewMap.this, RegistrationService.class);
		getMap.putExtra("command", "GETMAP");
		startService(getMap);

		// register broadcast receiver for the activity
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				homeList = intent.getStringArrayListExtra("homes");
				floorList = intent.getStringArrayListExtra("floors");
				roomList = intent.getStringArrayListExtra("rooms");
				populateHome(homeList);
				populateFloors(floorList);
				populateRooms(roomList);
			}
		};
	}

	/**
	 * Do some initialization of the views
	 */
	public void init() {
		homeSpinner = (Spinner) findViewById(R.id.spinner_home);
		roomSpinner = (Spinner) findViewById(R.id.spinner_room);
		floorSpinner = (Spinner) findViewById(R.id.spinner_floor);

		back = (Button) findViewById(R.id.back);
		done = (Button) findViewById(R.id.done);
		back.setOnClickListener(this);
		done.setOnClickListener(this);
	}	

	/**
	 * set on click listeners for the button
	 */
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.back: 
			Intent houseMap = new Intent(context, EnterMap.class);
			startActivity(houseMap);
			break;
		case R.id.done: 
			Intent main = new Intent(context,MainActivity.class);
			startActivity(main);
			break;				 
		}	
	}

	/**
	 * populate the home spinner with information pulled from the database
	 * @param list
	 */
	private void populateHome(ArrayList<String> list) {

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		homeSpinner.setAdapter(dataAdapter);
	}

	/**
	 * populate the room spinner with information pulled from the database
	 * @param list
	 */
	private void populateRooms(ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		roomSpinner.setAdapter(dataAdapter);
	}
	
	/**
	 * populate the floor spinner with information pulled from the database
	 * @param list
	 */
	private void populateFloors(ArrayList<String> list) {

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		floorSpinner.setAdapter(dataAdapter);
	}	

	@Override
	protected void onStart() {
	    super.onStart();
	    LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("GETMAP"));
	}

	@Override
	protected void onStop() {
	    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
	    super.onStop();
	}	
}
