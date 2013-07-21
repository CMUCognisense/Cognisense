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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * This is the activity where user assign the location to a service 
 * he/she has chosen for location assignment. The user can assign any 
 * location in the home map. The location consists of home, floor, room,
 * inroom and usertag. The home is the only field that cannot be empty. 
 * 
 * @author Pengcheng
 *
 */

public class LocationAssignment extends Activity{
	private Button done;
	private Spinner homes, floors, rooms, inrooms;
	private TextView detail;
	private EditText usertags;
	private String serviceid;
	private String info;
	private BroadcastReceiver receiver;
	private ArrayList<String> homeList;
	private ArrayList<String> floorList;
	private ArrayList<String> roomList;
	private ArrayList<String> inRoomList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_location);

		info = getIntent().getStringExtra("Device");
		serviceid = info.split(" ")[1];

		// do initialization of the view
		init();
		
		detail.setText(info);

		// send get info action to all the devices
		Intent findDevice = new Intent(LocationAssignment.this, RegistrationService.class);
		findDevice.putExtra("command", "GETLOCATION");
		startService(findDevice);
	}

	/**
	 * Do initialization of the views
	 */
	private void init() {
		done = (Button) findViewById(R.id.setlocationbutton);
		homes = (Spinner) findViewById(R.id.spinner1);
		floors = (Spinner) findViewById(R.id.spinner2);
		rooms = (Spinner) findViewById(R.id.spinner3);
		inrooms = (Spinner) findViewById(R.id.spinner4);
		detail = (TextView) findViewById(R.id.serviceid);
		usertags = (EditText) findViewById(R.id.usertag);
		
		inRoomList.add("Empty");
		inRoomList.add("Top");
		inRoomList.add("Bottom");
		inRoomList.add("Right");
		inRoomList.add("Left");
		inRoomList.add("Front");
		inRoomList.add("Back");


		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// first get what the user has given
				String home = homes.getSelectedItem().toString();
				String floor = floors.getSelectedItem().toString();
				// if the floor field is empty
				if (floor.trim().equals("Empty")) {
				    floor = "notset";
				}

				String room = rooms.getSelectedItem().toString();
				// if the room field is empty
				if (room.trim().equals("Empty")) {
				    room = "notset";
				}
				
				String inroom = inrooms.getSelectedItem().toString();
				// if the inroom field is empty
				if (inroom.trim().equals("Empty")) {
				    inroom = "notset";
				}

				String usertag = usertags.getText().toString();
				// if the usertag field is empty
				if (usertag.trim().equals("")) {
				    usertag = "notset";
				}
				
				// send setLocation to the selected device
				Intent setLocation = new Intent(LocationAssignment.this, RegistrationService.class);
				setLocation.putExtra("command", "SETLOCATION");
				setLocation.putExtra("dstServiceid", serviceid);
				setLocation.putExtra("location", home+"+"+floor+"+"+room+"+"+inroom+"+"+usertag);
				startService(setLocation);
				
				// store the user tag into the database
				Intent addUserTag = new Intent(LocationAssignment.this, RegistrationService.class);
				addUserTag.putExtra("command", "ADDUSERTAG");
				addUserTag.putExtra("USERTAG", usertag);
				startService(addUserTag);
				finish();
			}
		});

		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				homeList = intent.getStringArrayListExtra("homes");
				floorList = intent.getStringArrayListExtra("floors");
				roomList = intent.getStringArrayListExtra("rooms");

				// add empty choice to the spinner
				floorList.add(0, "Empty");
				roomList.add(0, "Empty");

				// populate the spinner with the home map pulled from database
				populateHome();
				populateFloors();
				populateRooms();
				populateInroom();
			}
		};
	}

	/**
	 * Populate the home spinner
	 */
	private void populateHome() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, homeList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		homes.setAdapter(dataAdapter);
	}

	/**
	 * Populate the room spinner
	 */
	private void populateRooms() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, roomList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rooms.setAdapter(dataAdapter);
	}

	/**
	 * Populate the floor spinner
	 */
	private void populateFloors() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, floorList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		floors.setAdapter(dataAdapter);
	}	

	/**
	 * Populate the inroom spinner
	 */
	private void populateInroom() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, inRoomList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		inrooms.setAdapter(dataAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("ASSIGNLOCATION"));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}

}
