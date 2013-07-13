package com.servicediscovery;

import java.util.HashSet;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.multicast.MulticastLayer;
import com.multicast.MulticastReceive;
import com.multicast.RecvMessageEvent;


public class SdlCommon implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// mapping between the service id and intent filter name
	HashSet<String> intentFilter;
	Context appContext;
	Object appObject = null;
	boolean DEBUG = false;
	String TAG = "SDLCommon";

	public SdlCommon(Context context, boolean bool, boolean multicast) {
		multicastLayer = new MulticastLayer(context, multicast);
		appContext = context;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		intentFilter = new HashSet<String>();
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

		ContentResolver contentResolver = appContext.getContentResolver();
		Uri uri = Uri.parse("content://com.commproc.provider/INFO");
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		while(cursor.moveToNext()){
			String intentfilter = cursor.getString(cursor.getColumnIndex("INTENTFILTER"));
			Intent intent = new Intent(intentfilter);
			intent.putExtra("message", msg);
			appContext.sendBroadcast(intent);
			Log.i(TAG, "send intent to:"+ intentfilter);
		}
		cursor.close();
	}

	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		if(DEBUG)System.out.println("Message Received: " + e.getMessage());
		DispatchMessage(e.getMessage());
	}
}
