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


public class ChangeLocation extends Activity implements OnClickListener{
	Button search,changeLocation;
	private BroadcastReceiver receiver;
	Spinner Devices;
	List<String> list = new ArrayList<String>();
	String Tag ="DEBUG";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main4);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(Tag,"The intent is received from UI value is" + intent.getStringArrayListExtra("Values"));
				list.add("Select a device");
				list = intent.getStringArrayListExtra("Values");
				populateSearchSpinner();
			}
		};
		addListenerOnButton();
	}
	
	private void populateSearchSpinner() {
		Devices = (Spinner) findViewById(R.id.changeSpinner);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Devices.setAdapter(dataAdapter);
	}
	
	

	public void addListenerOnButton() {
		
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(this);
		changeLocation= (Button) findViewById(R.id.changeLocation);
		changeLocation.setOnClickListener(this);
	}	
	

	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		case R.id.search: 
			Intent serviceIntent = new Intent(ChangeLocation.this, RegistrationService.class);
			serviceIntent.putExtra("command", "FindDevices");
			Log.d("DEBUG","Sent to service");
			startService(serviceIntent);
			break;
		case R.id.changeLocation: Intent intent3 = new Intent(context,LocationAssignment.class);
		   				startActivity(intent3);
		   				break;				 
		}	

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
	
	
	
}
