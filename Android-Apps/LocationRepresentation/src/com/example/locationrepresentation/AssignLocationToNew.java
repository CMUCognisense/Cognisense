package com.example.locationrepresentation;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AssignLocationToNew extends Activity implements OnClickListener{

	Button search,assign;
	private Spinner newDevices;
	private BroadcastReceiver receiver;
	private String Tag = "DEBUG";
	List<String> list = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchnewdevices);
		// initialize the views
		init();
		
		// send get info action to all the devices
		Intent findDevice = new Intent(AssignLocationToNew.this, RegistrationService.class);
		findDevice.putExtra("command", "FINDDEVICE");
		startService(findDevice);
	}

	public void init() {
		search = (Button) findViewById(R.id.searchNew);
		assign = (Button) findViewById(R.id.searchAssign);
		newDevices = (Spinner) findViewById(R.id.searchSpinner);

		search.setOnClickListener(this);
		assign.setOnClickListener(this);

		receiver = new BroadcastReceiver() {
			// once this activity receives 
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(Tag,"The intent is received from UI value is" + intent.getStringArrayListExtra("Values"));
				
				list = intent.getStringArrayListExtra("Values");
				populateSearchSpinner();
			}
		};
	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.searchNew :
			Intent serviceIntent = new Intent(AssignLocationToNew.this, RegistrationService.class);
			serviceIntent.putExtra("command", "FINDDEVICE");
			Log.d("DEBUG","Sent to service");
			startService(serviceIntent);
			break;
		case R.id.searchAssign:		
			Intent intent1 = new Intent(context,LocationAssignment.class);
			intent1.putExtra("Device",String.valueOf(newDevices.getSelectedItem()));
			startActivity(intent1);
		}
	}

	private void populateSearchSpinner() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		newDevices.setAdapter(dataAdapter);
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
