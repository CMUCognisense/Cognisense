/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.example.locationapp;

import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocationService extends android.app.Service{
	private boolean isRegistered = false;
	private ServiceDiscoveryLayer sdl = null;
	private String serviceId = null;
	private String TAG = "LocationService";



	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "Location Service onCreate");
		super.onCreate();

		// register if not registered yet
		if (!isRegistered) {
			try {
				register();
			} catch (Exception e) {
				Log.e(TAG, "error on registration" + e);
				e.printStackTrace();
			}
			isRegistered = true;
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Location Service onStartCommand");
		// get info from intent
		if(intent == null) 
		{
			Log.v(TAG, "NUll intent");
			return 0;
		}

		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		String mylocation  = p.getString("mylocation","" );
		String command = intent.getStringExtra("command");
		if(command != null){
			// if the app/service want to send message
			if (command.equals("SetLocation")) {
				
				Log.v(TAG,"Set the location previous is" + mylocation);
				Log.v(TAG,"Start the process of selecting songs and locating the user");
				String userName = intent.getStringExtra("user").toLowerCase();
				mylocation = intent.getStringExtra("location");
				p.edit().putString("mylocation", mylocation).commit();
				p.edit().putString("user",userName).commit();
				Message message = new Message(serviceId);
				message.addTrigger("LocationChanged",userName+":"+mylocation);
				sdl.sendMessage(message);
			}			
		}
		else
		{	// this means this is an intent from the ServiceDscoveryLayer 
			// rather than the one in the UI
			Log.v(TAG,"Calling the processing command");

			Log.v(TAG,"My location is " + mylocation);
			sdl.callMethod(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void register() throws Exception{
		// on registration, get a service discovery layer object
		// register the service and triggers/actions
		// these information will be registered to android communication process 
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(this, "LocationService", getApplicationContext());
		// register the service "DoorbellApp" whose intent filter name is "DoorbellApp"
		serviceId = sdl.registerNewService("LocationService");
		sdl.registerActions("sendLocation", "sendLocation", LocationService.class);
		Log.v(TAG, "Registered service " + serviceId);
	}

	// actoin for the location service
	public void sendLocation(Object actionInput, Object srcServiceId) {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		String username = p.getString("user", "");
		if(((String)actionInput).equalsIgnoreCase(username))
		{
			Message msg = new Message(serviceId);
			String mylocation  = p.getString("mylocation","" );
			msg.addTrigger("getLocation", mylocation);
			System.out.println("The src service id is " + (String)srcServiceId);
			msg.addServiceId((String)srcServiceId);
			sdl.sendMessage(msg);
		}
	}

}

