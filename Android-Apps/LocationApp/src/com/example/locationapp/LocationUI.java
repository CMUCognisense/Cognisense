package com.example.locationapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocationUI extends Activity {

	private Button setLocation; 
	private String TAG = "LocationService";
	private EditText userNameip = null;
	private EditText locationIp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_ui);
		
		userNameip = (EditText)findViewById(R.id.editText1);
		locationIp = (EditText)findViewById(R.id.editText2);
		
		setLocation = (Button)findViewById(R.id.button1);
		setLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Log.d(TAG,"Send Intent to service");
				Intent serviceIntent = new Intent(LocationUI.this,LocationService.class);
				serviceIntent.putExtra("command", "SetLocation");
				serviceIntent.putExtra("user", userNameip.getText().toString());
				serviceIntent.putExtra("location", locationIp.getText().toString());
				startService(serviceIntent);
				
			}
		});
		
		Log.d(TAG, "Started the UI Activity staring service");
		Intent serviceIntent = new Intent(LocationUI.this, LocationService.class);
		startService(serviceIntent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_ui, menu);
		return true;
	}
}
