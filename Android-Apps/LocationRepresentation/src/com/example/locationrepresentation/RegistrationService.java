/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.example.locationrepresentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.R.integer;
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
	private enum states{IS_RECEIVING, NOT_RECEIVING};
	private states state;
	private HashMap<String, String> newDevices = new HashMap<String, String>();
	private HashMap<String, String> oldDevices = new HashMap<String, String>();
	private HashMap<String, String> allDevices = new HashMap<String, String>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
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
				newDevices.clear();
				oldDevices.clear();
				allDevices.clear();
				state = states.IS_RECEIVING;
				Message message = new Message(serviceId);
				message.addAction("getInfo");
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
			
			// if the app wants to pull the map from the database
			if (command.equals("GETLOCATION")) {
				// query the database and get the locations
				ArrayList<String> homes = sdl.getHomes();
				ArrayList<String> floors = sdl.getFloors();
				ArrayList<String> rooms = sdl.getRooms();
				ArrayList<String> usertag = sdl.getUsertags(); 
				
				// put the information in the intent
				Intent getLocation = new Intent("ASSIGNLOCATION");
				getLocation.putStringArrayListExtra("homes", homes);
				getLocation.putStringArrayListExtra("floors", floors);
				getLocation.putStringArrayListExtra("rooms", rooms);
				getLocation.putStringArrayListExtra("usertag", usertag);
				broadcaster.sendBroadcast(getLocation);
			}


			// Following commands are for entering home map
			// if the app wants to add home to the database
			if (command.equals("ADDHOME")) {
				String home = intent.getStringExtra("HOME");
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
			
			// if the app wants to add user tag to the database
			if (command.equals("ADDUSERTAG")) {
				String usertag = intent.getStringExtra("USERTAG");
				sdl.addUsertag(usertag);
			}
			
			// if the app wants to stop receiving getInfo trigger
			if (command.equals("STOP")) {
				state = states.NOT_RECEIVING;
			}
			
			// if the app wants to set location of a service
			if (command.equals("SETLOCATION")) {
				String dst = intent.getStringExtra("dstServiceid");
				String location = intent.getStringExtra("location");
				Message message = new Message(serviceId);
				message.addServiceId(dst);
		        message.addAction("setLocation",location);
		        sdl.sendMessage(message);
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

		sdl.registerTriggers("getInfoReceived", "getInfo", this.getClass());
	}

	public void getInfoReceived(Object actionInput, Object srcServiceId) {
		// if the registration process is still open for messages
		// update the list
		if (state == states.IS_RECEIVING) {		
			Log.e(TAG, (String)srcServiceId);
			// TODO now we only return all of the devices to the UI
			allDevices.put((String)srcServiceId, (String)actionInput);

			Intent uiIntent = new Intent("FINDDEVICE");
			uiIntent.putExtra("devices", allDevices);
			broadcaster.sendBroadcast(uiIntent);
		}
	}
}

