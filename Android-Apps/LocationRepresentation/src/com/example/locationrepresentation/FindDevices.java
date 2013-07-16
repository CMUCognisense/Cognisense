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

/**
 * This is the activity where user can find all of the devices within the home
 * environment and see their information.
 * 
 * @author Pengcheng
 *
 */
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

	/**
	 * Initialize the views, listeners and receivers
	 */
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

					// add this entry to the list
					list.add(entry.toString());
				}
				
				// populate the spinner with the list we construct
				populateSearchSpinner(list);
			}
		};

		// if one of the entry is selected
		devices.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// first get the service id of the service
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
				// show detailed information in the text view
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
		// once the user click on the assign button, the receiving of getInfo
		// trigger will be stopped and user will assign location to the service
		// he just chose
		case R.id.searchAssign:		
			Intent assignLoc = new Intent(context,LocationAssignment.class);
			String serviceID = devices.getSelectedItem().toString();
			
			//if no device is found
			if (serviceID.trim().equals("") || serviceID == null) {
				return;
			}

			assignLoc.putExtra("Device", serviceID);
			if (isStarted) {
				// stop the receiving of getInfo trigger
				Intent stop = new Intent(FindDevices.this, RegistrationService.class);
				stop.putExtra("command", "STOP");
				startService(stop);
				isStarted = false;
			}
			startActivity(assignLoc);
			break;

		// once the user hit refresh button, the registration service will start
		// a new round of searching for services in the home
		case R.id.searchRefresh:
			// clear all the info in the current UI
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

	/**
	 * register and unregister the broadcast receiver
	 */
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
