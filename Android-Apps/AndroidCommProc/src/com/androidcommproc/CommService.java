/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.androidcommproc;

import com.servicediscovery.SdlCommon;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CommService extends android.app.Service{
	private static final String TAG = "CommService";
	SdlCommon myServiceDiscovery = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "ServiceDemo onCreate");
		super.onCreate();
		// get an instance of the service discovery layer and
		// the receiver thread will start
		if(this.myServiceDiscovery == null){
			myServiceDiscovery = new SdlCommon(this, true,false);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "ServiceDemo onStartCommand");
		// get info from intent
		if(intent == null) 
		{
			Log.v(TAG, "NUll intent");
			return 0;
		}
		
		String command = intent.getStringExtra("command");
		if(command != null){
			// if the app/service want to send message
			if (command.equals("SENDALL")) {
				Log.e(TAG, "SendAll Command");
				String msg = (String)intent.getSerializableExtra("message");
				myServiceDiscovery.sendMessage(msg);
			}
			// if the application wants to register for the service
			if (command.equals("REGISTER")) {
				Log.e(TAG, "Register Command");
				//TODO get the intentfilter and the service object from the intent
				String intentfilter = intent.getStringExtra("intentfilter");
				myServiceDiscovery.registerNewService(intentfilter);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
}

