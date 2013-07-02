/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.androidcommproc;

import com.servicediscovery.Message;
import com.servicediscovery.SdlCommon;
import com.servicediscovery.Service;

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
			myServiceDiscovery = new SdlCommon(this, true);
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
				Message msg = (Message)intent.getSerializableExtra("message");
				myServiceDiscovery.sendMessage(msg);
			}
			// if the application wants to register for the service
			if (command.equals("REGISTER")) {
				Log.e(TAG, "Register Command");
				//TODO get the intentfilter and the service object from the intent
				String intentfilter = intent.getStringExtra("intentfilter");
				Service service = (Service)intent.getSerializableExtra("service");
				myServiceDiscovery.registerNewService(intentfilter, service);
			}
			// if the service wants to do some update
			if (command.equals("UPDATE")) {
				Log.e(TAG, "Update Command");
				Service service = (Service)intent.getSerializableExtra("service");
				myServiceDiscovery.updateMap(service);
			}		
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
//	/**
//	 * This method is called when messages are received from the multicast
//	 * layer of android device. Then it send intent to the application with
//	 * the message it received
//	 */
//	@Override
//	public void onReceiveMessage(RecvMessageEvent e) {
//		// TODO Auto-generated method stub
//		//print out the mapping
//		for (String service: registerMap.keySet()){
//			String key = service.toString();
//			String value = registerMap.get(service).toString();  
//			System.out.println(key + " " + value);  
//		} 
//				
//		String msg = e.getMessage();
//		//TODO for testing purpose, msg (headers[2]) is just the action name
//		// in the intent filter, so we don't need to do any process for now
//		String intentfilter = registerMap.get(msg);
//		Intent intent = new Intent(intentfilter);
//		intent.putExtra("received_msg", e.getMessage());
//		Log.e("CommService", "FIRE INTENT " + e.getMessage() + "\nintentfilter: " + intentfilter);
//		sendBroadcast(intent);
//	}
}

