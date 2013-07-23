package com.androidcommproc;

import com.servicediscovery.SdlCommon;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 * 
 * @author Pengcheng
 */

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
		super.onCreate();

		// get an instance of the service discovery layer and
		// the receiver thread will start
		if(this.myServiceDiscovery == null){
			myServiceDiscovery = new SdlCommon(this, false, false);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// get info from intent
		if(intent == null) 
			return 0;

		String command = intent.getStringExtra("command");
		if(command != null){
			// if the app/service want to send message
			if (command.equals("SENDALL")) {
				String msg = (String)intent.getSerializableExtra("message");
				myServiceDiscovery.sendMessage(msg);
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}
}

