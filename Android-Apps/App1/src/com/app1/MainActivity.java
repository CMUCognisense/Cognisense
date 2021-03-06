/**
 * @commservice is the action name of a intent filter in the broadcast
 * receiver of comm process.
 * 
 * @UDP is the action name with which the comm process will send intent
 * 
 * @received_msg is the key that mapped to the UDP message comm process received
 * 
 * @command is the key that mapped to the function names the app wants to call
 *
 * @message is the key that mapped to the message(String) the app wants to send
 */
package com.app1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.app1.R;
import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	private boolean isRegistered = false;
	private ServiceDiscoveryLayer sdl = null;
	private String serviceId = null;
	private String TAG = "DoorbellApp";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// register the services to the service discovery layer
		if (!isRegistered) {
			try {
				register();
			} catch (Exception e) {
				Log.e(TAG, "error on registration");
				e.printStackTrace();
			}
			isRegistered = true;
		}

		Intent intent = getIntent();

		// this method get the intent from broadcast receiver and parse
		// message and call corresponding actions or triggers
		sdl.callMethod(intent);

		// set the on click on getinfo button
		Button send = (Button)findViewById(R.id.button1);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//generate the message
				Message message = new Message(serviceId);
				message.addAction("giveInfo");
				message.addServiceType("Doorbell");
				sdl.sendMessage(message);
			}
		});
		
		Button test = (Button)findViewById(R.id.button2);
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sdl.addHome("pc");
				sdl.addHome("parth");
				sdl.addFloor("1");
				sdl.addFloor("2");
				sdl.addFloor("3");
				sdl.addFloor("4");
				sdl.addRoom("bed");
				sdl.addRoom("living");
				sdl.addUsertag("window");
				sdl.addUsertag("table");
			}
		});
		
		Button show = (Button)findViewById(R.id.button3);
		show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> homes = sdl.getHomes();
				ArrayList<String> floors = sdl.getFloors();
				ArrayList<String> rooms = sdl.getRooms();
				ArrayList<String> tags = sdl.getUsertags();
				Log.e(TAG, homes.toString());
				Log.e(TAG, floors.toString());
				Log.e(TAG, rooms.toString());
				Log.e(TAG, tags.toString());
			}
		});
		
	}

	private void register() throws Exception{
		// on registration, get a service discovery layer object
		// register the service and triggers/actions
		// these information will be registered to android communication process 
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(this, "DoorbellApp", getApplicationContext());
		// register the service "DoorbellApp" whose intent filter name is "DoorbellApp"
		serviceId = sdl.registerNewService("DoorbellApp");
		Log.e(TAG, serviceId);

		//TODO location setting is not done yet
		sdl.addLocation();
		sdl.registerTriggers("onDoorbell", "onDoorbell", this.getClass());
	}

	// triggers
	public void onDoorbell(Object triggerData, Object srcServiceId) {
		// this will print out the message sent by the doorbell.
		Log.e(TAG, "The properties of the doorbell are :" + (String)triggerData +" SrcServiceID:"+ (String)srcServiceId); 
	}	 
}
