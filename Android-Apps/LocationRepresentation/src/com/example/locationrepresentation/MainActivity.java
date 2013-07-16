package com.example.locationrepresentation;

import com.androidcommproc.CommService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This is the main activity of the registration application in the android phone
 * it will start the android communication process which is the process which facilitate
 * the message sending and receiving between the phone and other devices. Also, this is 
 * the application where user can do the basic things like entering home map, registering
 * other devices to the home and change the location of the user.
 * 
 * @author pengcheng
 *
 */

public class MainActivity extends Activity implements OnClickListener {

	Button map,assign;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Start the android android communication process here
		Intent serviceIntent = new Intent(MainActivity.this, CommService.class);
		startService(serviceIntent);

		// initialize the views
		init();
	}

	// Set the onclick listeners for all of the buttons
	public void init() {
		map = (Button) findViewById(R.id.map);
		assign = (Button) findViewById(R.id.assign);

		map.setOnClickListener(this);
		assign.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final Context context = this;
		switch(v.getId()){
		// bring up the house map activity
		case R.id.map : 
			Intent enterMap = new Intent(context, EnterMap.class);
			startActivity(enterMap);
			break;
		// bring up the assign location activity(registering new device)
		case R.id.assign: 
			Intent searchDevice = new Intent(context, FindDevices.class);
			startActivity(searchDevice);
			break;
		}
	}
}