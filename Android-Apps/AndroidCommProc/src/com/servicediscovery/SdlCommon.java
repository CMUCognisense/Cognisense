package com.servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.multicast.MulticastLayer;
import com.multicast.MulticastReceive;
import com.multicast.RecvMessageEvent;


public class SdlCommon implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// mapping between the service id and intent filter name
	HashSet<String> intentFilter;
	Context androidContext;
	Object appObject = null;
	boolean DEBUG = false;
	String TAG = "SDLCommon";

	public SdlCommon(Context context, boolean bool, boolean multicast) {
		multicastLayer = new MulticastLayer(context, multicast);
		androidContext = context;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		intentFilter = new HashSet<String>();
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * @param service
	 */
	public void registerNewService(String intentfilter) {
		// add the intent filter to the hash set
		intentFilter.add(intentfilter);
		Log.d(TAG, "Register " + intentfilter);
	}
	
	/**
	 * this method generates a string from the message object and then passes it to the multicast layer
	 * reliable multicast layer. 
	 * @param message
	 */
	public void sendMessage(String msg) {
		// the message is already converted to string by the application
		if(DEBUG) System.out.println("Sending message "  +msg);
		if (msg != null)
		{
			multicastLayer.sendAll(msg);
			DispatchMessage(msg);
		}
		else
			throw new IllegalArgumentException("Message is not valid");
	}
	
	/**
	 * This method will dispatch the message the android communication process
	 * received to each and every service that is registered to it
	 * @param msg
	 */
	public void DispatchMessage(String msg) {
		if(DEBUG) System.out.println("Dispatching message");
		for (String intentfilter : intentFilter) 
		{
			// send intent to the services in the set
			Intent intent = new Intent(intentfilter);
			intent.putExtra("message", msg);
			Log.e(TAG, "dispatch action intent");
			androidContext.sendBroadcast(intent);
		}
	}

	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		if(DEBUG)System.out.println("Message Received: " + e.getMessage());
		DispatchMessage(e.getMessage());
	}
}
