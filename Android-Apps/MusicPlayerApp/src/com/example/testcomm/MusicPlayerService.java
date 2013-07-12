/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.example.testcomm;

import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MusicPlayerService extends android.app.Service{
	private boolean isRegistered = false;
	private ServiceDiscoveryLayer sdl = null;
	private String serviceId = null;
	private String TAG = "MusicPlayer";
	private LocalBroadcastManager broadcaster;
	static int isPlaying = 0;
	static int selectToPlay = 0;
	static int locChange = 0;
	static String chosenSpeaker;
	static String serverIp;
	static String serverPort;
	static String userName = null;



	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "ServiceDemo onCreate");
		super.onCreate();

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



		broadcaster = LocalBroadcastManager.getInstance(this);

		// get an instance of the service discovery layer and
		// the receiver thread will start
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
			if (command.equals("SelectSongs")) {
				Log.v(TAG,"Start the process of selecting songs and locating the user");
				userName = intent.getStringExtra("user").toLowerCase();
				Message message = new Message(serviceId);
				message.addAction("sendLocation",userName);
				// after getting the user name it is important to register a trigger to the 
				// location changed object again. 
				try {
					sdl.registerTriggers("changeOfLocation", "LocationChanged", this.getClass());
				} catch (Exception e) {
					e.printStackTrace();
				}
				sdl.sendMessage(message);
			}
			else if(command.equals("SelectedSong")) {
				Log.v(TAG,"The song is selected now start the server");
				String song = intent.getStringExtra("song");
				Message msg = new Message(serviceId);
				msg.addAction("startServer", song);
				sdl.sendMessage(msg);
			}
			else if(command.equals("Stop")) {
				Message message = new Message(serviceId);
				message.addServiceId(chosenSpeaker);
				message.addAction("stop");
				sdl.sendMessage(message);

				message = new Message(serviceId);
				message.addServiceType("MusicServer");
				message.addAction("stopServer", serverPort);
				sdl.sendMessage(message);

				isPlaying = 0;
			}

		}
		else
		{	// this means this is an intent from the ServiceDscoveryLayer 
			// rather than the one in the UI
			sdl.callMethod(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void register() throws Exception{
		// on registration, get a service discovery layer object
		// register the service and triggers/actions
		// these information will be registered to android communication process 
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(this, "MusicPlayer", getApplicationContext());
		// register the service "DoorbellApp" whose intent filter name is "DoorbellApp"
		serviceId = sdl.registerNewService("MusicPlayer");
		Log.e(TAG, serviceId);

		//TODO location setting is not done yet
		sdl.addLocation();
		sdl.registerTriggers("getInfoReceived", "getInfo", this.getClass());
		sdl.registerTriggers("selectSong", "listSongs", this.getClass());
		sdl.registerTriggers("serverStarted", "serverStarted", this.getClass());
		sdl.registerTriggers("locationReceived", "getLocation", this.getClass());
	}

	// trigger listner for the server config 
	public void getInfoReceived(Object actionInput, Object srcServiceId) {

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

	// trigger listner for location changed for parth
	public void changeOfLocation(Object triggerData, Object srcServiceId) {

		if(isPlaying == 1) {

			Log.d(TAG,"Location of user has changed now");
			String[] data = ((String)triggerData).split(":");
			if(data[0].compareToIgnoreCase(userName) == 0)
			{
				String[] location = data[1].split("\\+");

				Message message = new Message(serviceId);
				message.addServiceId(chosenSpeaker);
				message.addAction("stop");
				sdl.sendMessage(message);
				Log.d(TAG,"Stopping Previous Speaker");
				locChange = 1;
				Log.d(TAG,"Playing at New Speaker");
				message = new Message(serviceId);
				message.addAction("getInfo");
				message.addServiceType("Speaker");
				message.addProperty("Location");
				message.addPropertyValue("Location", "HOME", location[0]);
				message.addPropertyValue("Location", "FLOOR", location[1]);
				message.addPropertyValue("Location", "ROOM", location[2]);
				message.addPropertyValue("Location", "INROOM", location[3]);
				message.addPropertyValue("Location", "USERTAG", location[4]);
				sdl.sendMessage(message);
			}
		}
	}


	// trigger listner for the song selection
	public void selectSong(Object triggerData, Object srcServiceId) {

		//send an intent to the UI to select the songs. When a song is selected then you can start server
		Intent intent1 = new Intent("UIINTENT");
		intent1.putExtra("Values", (String)triggerData);					
		broadcaster.sendBroadcast(intent1);		
	}

	//	trigger listner for server started
	public void serverStarted(Object actionInput, Object srcServiceId) {
		Log.v(TAG,"The server was started starting speaker at "+(String)actionInput);
		serverIp = ((String)actionInput).split(":")[0];
		serverPort = ((String)actionInput).split(":")[1];
		Message message = new Message(serviceId);
		message.addServiceId(chosenSpeaker);
		message.addAction("play",(String)actionInput);
		sdl.sendMessage(message);
		isPlaying = 1;

	}

	public void locationReceived(Object triggerData, Object srcServiceId) {
		Log.d(TAG,"Location of parth received " + (String)triggerData);
		String[] location = ((String)triggerData).split("\\+");
		Log.d(TAG,"Find a speaker that is at this location");
		Message message = new Message(serviceId);
		message.addAction("getInfo");
		message.addServiceType("Speaker");
		message.addProperty("Location");
		message.addPropertyValue("Location", "HOME", location[0]);
		message.addPropertyValue("Location", "FLOOR", location[1]);
		message.addPropertyValue("Location", "ROOM", location[2]);
		message.addPropertyValue("Location", "INROOM", location[3]);
		message.addPropertyValue("Location", "USERTAG", location[4]);
		sdl.sendMessage(message);
		selectToPlay = 1;

	}


}

