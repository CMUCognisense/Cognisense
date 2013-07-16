package com.example.locationrepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class FindDevices extends Activity implements OnClickListener{

	private Button assign, refresh;
	private TextView details;
	private Spinner devices;
	private BroadcastReceiver receiver;
	private HashMap<String, String> deviceMap; 
	private boolean isStarted = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_new_devices);
		// initialize the views
		init();

		// send get info action to all the devices
		Intent findDevice = new Intent(FindDevices.this, RegistrationService.class);
		findDevice.putExtra("command", "FINDDEVICE");
		isStarted = true;
		startService(findDevice);
	}

	public void init() {
		assign = (Button) findViewById(R.id.searchAssign);
		refresh = (Button) findViewById(R.id.searchRefresh);
		devices = (Spinner) findViewById(R.id.searchSpinner);
		details = (TextView) findViewById(R.id.searchdetail);
		
		assign.setOnClickListener(this);
		refresh.setOnClickListener(this);

		// register receiver for the intent sent from the registration service
		receiver = new BroadcastReceiver() {
			// once this activity receives 
			@SuppressWarnings("unchecked")
			@Override
			public void onReceive(Context context, Intent intent) {
				ArrayList<String> list = new ArrayList<String>();
				StringBuilder entry = new StringBuilder();
				
				// once it gets result from the registration service
				// it populates the spinner with new arraylist
				deviceMap = (HashMap<String, String>)intent.getSerializableExtra("devices");
				
				// The entry will look like this "servicetype serviceid"
				for (String id : deviceMap.keySet()) {
					entry.setLength(0);
					Log.e("RE", id);
					Log.e("entry", entry.toString());

					String infos[] = deviceMap.get(id).split(",");
					// get the service type
					for (int i = 0; i < infos.length; i++) {
						if (infos[i].startsWith("SERVICETYPE-")) {
							String[] type = infos[i].split("-");
							entry.append(type[1]);
							break;
						}
					}
					entry.append(" ");
					entry.append(id);
					list.add(entry.toString());
				}
				// populate the spinner
				populateSearchSpinner(list);
			}
		};

		// if one of the entry is selected
		devices.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				String entry[] = parent.getItemAtPosition(pos).toString().split(" ");
				String serviceid = entry[1];
				// Set the text field to the detailed information
				String info = deviceMap.get(serviceid);
				String[] infos = info.split(",");
				StringBuilder text = new StringBuilder();
				for (int i = 0; i < infos.length; i++) {
					text.append(infos[i]);
					text.append("\n");
				}
				details.setText(text.toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.searchAssign:		
			Intent assignLoc = new Intent(context,LocationAssignment.class);
			assignLoc.putExtra("Device", devices.getSelectedItem().toString());
			if (isStarted) {
				// first stop the receiving of getInfo trigger
				Intent stop = new Intent(FindDevices.this, RegistrationService.class);
				stop.putExtra("command", "STOP");
				startService(stop);
				isStarted = false;
			}
			startActivity(assignLoc);
			break;
		case R.id.searchRefresh:
			// clear all the info
			ArrayList<String> empty = new ArrayList<String>();
			populateSearchSpinner(empty);
			details.setText("");
			// send get info action to all the devices
			Intent findDevice = new Intent(FindDevices.this, RegistrationService.class);
			findDevice.putExtra("command", "FINDDEVICE");
			isStarted = true;
			startService(findDevice);
			break;
		}
	}

	/**
	 * Populate the spinner with a given array list
	 * @param list
	 */
	private void populateSearchSpinner(ArrayList<String> list) {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		devices.setAdapter(dataAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("FINDDEVICE"));
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}
}
