/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.example.locationrepresentation;
import java.util.ArrayList;

import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class RegistrationService extends android.app.Service{
	private static final String TAG = "RegistrationService";
	private LocalBroadcastManager broadcaster;
	private ServiceDiscoveryLayer sdl = null;
	private String serviceId = null;
	private boolean isRegistered = false;
	private enum states{IS_RECEIVING_ALL, NOT_RECEIVING, IS_RECEIVING_NEW};
	private states state;
	private ArrayList<String> devices;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		super.onCreate();
	    broadcaster = LocalBroadcastManager.getInstance(this);

		// register if not registered yet
		if (!isRegistered) {
			try {
				register();
			} catch (Exception e) {
				Log.e(TAG, "error on registration");
				e.printStackTrace();
			}
			isRegistered = true;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		// get info from intent
		if(intent == null) 
		{
			return 0;
		}
		
		String command = intent.getStringExtra("command");
		// if the intent does not have a command string
		// then this message is send from android communication service
		if(command == null){
			sdl.callMethod(intent);
		} else {
			// find all of the devices
			if (command.equals("FINDDEVICE")) {
				state = states.IS_RECEIVING_NEW;
				Message message = new Message(serviceId);
				message.addAction("giveInfo");
				sdl.sendMessage(message);
			}

			// if the app wants to pull the map from the database
			if (command.equals("GETMAP")) {
				// query the database and get the locations
				ArrayList<String> homes = sdl.getHomes();
				ArrayList<String> floors = sdl.getFloors();
				ArrayList<String> rooms = sdl.getRooms();

				// put the information in the intent
				Intent getMap = new Intent("GETMAP");
				getMap.putStringArrayListExtra("homes", homes);
				getMap.putStringArrayListExtra("floors", floors);
				getMap.putStringArrayListExtra("rooms", rooms);
				broadcaster.sendBroadcast(getMap);
			}

			// Following commands are for entering home map
			// if the app wants to add home to the database
			if (command.equals("ADDHOME")) {
				String home = intent.getStringExtra("HOME");
				Log.d(TAG, home);
				sdl.addHome(home);
			}

			// if the app wants to add floor to the database
			if (command.equals("ADDFLOOR")) {
				String floor = intent.getStringExtra("FLOOR");
				sdl.addFloor(floor);
			}

			// if the app wants to add room to the database
			if (command.equals("ADDROOM")) {
				String room = intent.getStringExtra("ROOM");
				sdl.addRoom(room);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void register() throws Exception{
		// on registration, get a service discovery layer object
		// register the service and triggers/actions
		// these information will be registered to android communication process 
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(this, "RegistrationService", getApplicationContext());

		// register the service "RegistrationService" whose intent filter name is "RegistrationService"
		serviceId = sdl.registerNewService("RegistrationService");

		//TODO location setting is not done yet
		sdl.registerTriggers("getInfoReceived", "getInfo", this.getClass());
	}

	public void getInfoReceived(Object actionInput, Object srcServiceId) {
		// if the registration process is still open for messages
		// update the list
		if (state == states.IS_RECEIVING_NEW) {
			
			// if the action input is null, then update the list
			
		}
		if(selectToPlay == 1)
		{
			Log.d(TAG,"Speaker is chosen"+(String)srcServiceId+" now get the songs from the server");
			chosenSpeaker = new String((String)srcServiceId);
			selectToPlay = 0;
			Message msg = new Message(serviceId);
			msg.addAction("getSongs", null);
			sdl.sendMessage(msg);
		}
		else if (locChange == 1)
		{
			locChange = 0;
			Log.d(TAG,"Speaker is chosen"+(String)srcServiceId+" now play song again");
			chosenSpeaker = new String((String)srcServiceId);
			Message msg = new Message(serviceId);
			msg.addServiceId(chosenSpeaker);
			msg.addAction("play", serverIp+":"+serverPort);
			sdl.sendMessage(msg);

		}
	}

}

