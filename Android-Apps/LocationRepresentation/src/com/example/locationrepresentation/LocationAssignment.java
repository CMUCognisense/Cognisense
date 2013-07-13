package com.example.locationrepresentation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class LocationAssignment extends Activity{
	Button button;
	private Spinner homes, floors,rooms;
	String device;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			device = extras.getString("Device");
		}
		Log.d("DEBUG","got DEvice name" + device);
		populateHome();
		populateFloors();
		populateRooms();
	}

	private void populateHome() {

		homes = (Spinner) findViewById(R.id.homeSpinner);
		List<String> list = new ArrayList<String>();
		list.add("Select Home");
		list.add("Chiddu Home");
		list.add("Parth Home");
		list.add("PC Home");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		homes.setAdapter(dataAdapter);

	}

	private void populateRooms() {

		rooms = (Spinner) findViewById(R.id.roomSpinner);
		List<String> list = new ArrayList<String>();
		list.add("Select room");
		list.add("Kitchen");
		list.add("Garage");
		list.add("BedRoom");
		list.add("Living Room");
		list.add("Wash Room");
		list.add("Hall Way");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rooms.setAdapter(dataAdapter);

	}
	private void populateFloors() {

		floors = (Spinner) findViewById(R.id.floorSpinner);
		List<String> list = new ArrayList<String>();
		list.add("Select floor");
		list.add("Basement");
		list.add("First Floor");
		list.add("Second Floor");
		list.add("Third Floor");
		list.add("Terrace");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		floors.setAdapter(dataAdapter);

	}	

}
