/**
 * This is the Android Communication Process that utilize the 
 * multicast layer function calls to facilitate communication
 * between applications and other nodes in the cognisense env
 */
package com.example.doorbell;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.servicediscovery.Message;
import com.servicediscovery.ServiceDiscoveryLayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DoorBellConfigurationService extends android.app.Service{
	private static final String TAG = "DEBUG";
	private LocalBroadcastManager broadcaster;
	private ServiceDiscoveryLayer sdl = null;

	private String serviceId = null;
	private boolean isRegistered = false;


	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "In the onCreate method");
		super.onCreate();
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
		/* neeed to get user input for the configuration 
		 * and also need to take care of the location of the user
		 */

		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

		String configuration = intent.getStringExtra("configuration");
		if(configuration != null){

			if (configuration.equals("USER")) {

				String userName = intent.getStringExtra("user").toLowerCase();
				p.edit().putString("user",userName).commit();
			}

			if (configuration.equals("DEVICE")) {
				String serviceType = intent.getStringExtra("device");
				p.edit().putString("service",serviceType).commit();
				Log.v(TAG,"Device Received"+serviceType);
			}

			if (configuration.equals("NOTIFICATION")) {
				boolean notify = intent.getBooleanExtra("notify",false);
				p.edit().putBoolean("notify", notify).commit();
				Log.v(TAG,"Notification expectation for user "+ notify);
			}

			if (configuration.equals("TIME")) {
				int startHour,startMin,endHour,endMin;
				boolean set = intent.getBooleanExtra("set",false);
				if(set==true){
					startHour = intent.getIntExtra("starthour",0);
					startMin = intent.getIntExtra("startmin",0);
					endHour = intent.getIntExtra("endhour",0);
					endMin = intent.getIntExtra("endmin",0);
					p.edit().putBoolean("timeset", true).commit();
					p.edit().putInt("starthour", startHour).commit();
					p.edit().putInt("startmin", startMin).commit();
					p.edit().putInt("endhour", endHour).commit();
					p.edit().putInt("endmin", endMin).commit();
					Log.v(TAG,"Time Recieved "+ "Start Hour"+startHour +"start minute"+startMin+
							"end Hour"+endHour + "end Minute"+ endMin);
				}else{					
					p.edit().putBoolean("timeset", false).commit();
					p.edit().putInt("starthour", -1).commit();
					p.edit().putInt("startmin", -1).commit();
					p.edit().putInt("endhour", -1).commit();
					p.edit().putInt("endmin", -1).commit();					
				}
			}

			if(configuration.equals("LOCATION")){
				String home,floor,room,inRoom,favorite,serviceType;
				boolean set = intent.getBooleanExtra("set",false);
				if(set==true){
					home = intent.getStringExtra("home").toLowerCase();
					floor = intent.getStringExtra("floor").toLowerCase();		
					room = intent.getStringExtra("room").toLowerCase();			
					inRoom = intent.getStringExtra("inRoom").toLowerCase();			
					favorite = intent.getStringExtra("favorite").toLowerCase();
					p.edit().putBoolean("locationset", true).commit();
					p.edit().putString("home", home).commit();
					p.edit().putString("floor", floor).commit();
					p.edit().putString("room", room).commit();
					p.edit().putString("inRoom", inRoom).commit();
					p.edit().putString("favorite", favorite).commit();
					Log.v(TAG,"Location Recieved"+home+floor+room+inRoom+favorite);
				}else{
					p.edit().putBoolean("locationset",false).commit();
					p.edit().putString("home","").commit();
					p.edit().putString("floor","").commit();
					p.edit().putString("room", "").commit();
					p.edit().putString("inRoom", "").commit();
					p.edit().putString("favorite", "").commit();
				}

			}
			if(configuration.equals("getHomes")) {
				Intent intent1 = new Intent("UIINTENT");
				intent1.putExtra("Location","Home");
				intent1.putStringArrayListExtra("Values", sdl.getHomes());
				broadcaster.sendBroadcast(intent1);		

			}
			if(configuration.equals("getFloors")) {
				Intent intent1 = new Intent("UIINTENT");
				intent1.putExtra("Location","Floor");
				intent1.putStringArrayListExtra("Values", sdl.getFloors());
				broadcaster.sendBroadcast(intent1);		

			}

			if(configuration.equals("getRooms")) {
				Intent intent1 = new Intent("UIINTENT");
				intent1.putExtra("Location","Room");
				intent1.putStringArrayListExtra("Values", sdl.getRooms());
				broadcaster.sendBroadcast(intent1);		

			}

			if(configuration.equals("getUserTags")) {
				Intent intent1 = new Intent("UIINTENT");
				intent1.putExtra("Location","UserTag");
				intent1.putStringArrayListExtra("Values", sdl.getUsertags());
				broadcaster.sendBroadcast(intent1);		

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
		// on registation, get a service discovery layer object
		// register the service and triggers/actions
		// these information will be registered to android communication process 
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(this, "DoorbellConfiguration", getApplicationContext());
		// register the service "DoorbellApp" whose intent filter name is "DoorbellApp"
		serviceId = sdl.registerNewService("DoorbellConfiguration");
		Log.e(TAG, serviceId);
		//TODO location setting is not done yet
		//sdl.addLocation();
		sdl.registerTriggers("doorbellTriggerMethod","onDoorbell", this.getClass());
		//sdl.registerTriggers("getInfoReceived", "getInfo", this.getClass());
		sdl.registerTriggers("locationReceived", "getLocation", this.getClass());	
	}

	
	public void doorbellTriggerMethod(Object actionInput, Object srcServiceId) {

		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		Log.v(TAG,"Asking for user location"+p.getString("user",""));
		Message message = new Message(serviceId);
		message.addAction("sendLocation",p.getString("user",""));
		sdl.sendMessage(message);
	}


	public void configurationMatch(){
		
		Log.v(TAG, "Entered Configuration Match");
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		boolean notify = p.getBoolean("notify", false);
		boolean isTimeSet = p.getBoolean("timeset", false);
		boolean isLocationSet = p.getBoolean("locationset",false);

		boolean timeResult = false;
		boolean result = false;		

		if(isTimeSet==true){
			timeResult = timeMatch();
			Log.v(TAG, "Time Match Result: "+timeResult);
		}
		if(isLocationSet==true){
			result = locationMatch();
			Log.v(TAG, "Location Result: "+result);
		}
		if(notify==false){		
			Log.v(TAG, "C");
			if(isTimeSet==true && isLocationSet==true){
				Log.v(TAG, "D");
				if(timeResult ==false && result == false){
					Log.v(TAG, "E");
					sendNotifyMessage();
				}
				if(timeResult==false && result==true){
					sendNotifyMessage();
				}
				if(timeResult==true && result==false){
					sendNotifyMessage();
				}

			}
			if(isTimeSet==false && isLocationSet==true){
				Log.v(TAG, "F");
				if(result==false){
					Log.v(TAG, "G");
					sendNotifyMessage();
				}
			}
			if(isTimeSet==true && isLocationSet==false){
				Log.v(TAG, "H");
				if(timeResult==false){
					Log.v(TAG, "I");
					sendNotifyMessage();
				}
			}

		}
		else{		
			if(isTimeSet==true && isLocationSet==true){
				Log.v(TAG, "J");
				if(timeResult == true && result == true){
					Log.v(TAG, "K");
					sendNotifyMessage();
				}	
			}

			if(isTimeSet == true && isLocationSet == false){
				Log.v(TAG, "L");
				if(timeResult==true){
					Log.v(TAG, "M");
					sendNotifyMessage();
				}
			}
			if(isTimeSet == false && isLocationSet == true){
				Log.v(TAG, "N");
				if(result==true){
					Log.v(TAG, "O");
					sendNotifyMessage();
				}
			}
			if(isTimeSet== false && isLocationSet== false){
				Log.v(TAG, "P");
				sendNotifyMessage();	
			}
		}



	}

	private boolean timeMatch(){

		int startHour,startMin,endHour,endMin;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		startHour = p.getInt("starthour",0);
		startMin = p.getInt("startmin", 0);
		endHour = p.getInt("endhour", 0);
		endMin = p.getInt("endmin",0);
		Calendar cal = Calendar.getInstance();
		int pHour = cal.get(Calendar.HOUR_OF_DAY);
		int pMin = cal.get(Calendar.MINUTE);

		if(startHour<endHour){

			if(pHour==startHour){
				if(pMin<startMin)
					return false;
				else
					return true;
			}

			else if(pHour > startHour){
				if(pHour<endHour){
					return true;
				}
				else if(pHour==endHour){
					if(pMin > endMin){
						return false;
					}
					else
						return true;
				}
				else
					return false;
			}

			else
				return false;			
		}

		else
		{
			if(pHour > startHour || pHour < endHour ){
				return true;
			}
			else
				return false;			
		}
	}

	private boolean locationMatch(){

		Log.v(TAG, "In Location Match");
		String home,floor,room,inRoom,favorite;
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);		
		home = p.getString("home","");
		floor = p.getString("floor","");
		room = p.getString("room","");
		inRoom = p.getString("inRoom","");
		favorite = p.getString("favorite","");
		Log.v(TAG,"Home " +home +"floor" + floor + "room" +inRoom +"favorite"+favorite);
		if(home.equals("notset")){
			return false;
		}

		if(home.equalsIgnoreCase(p.getString("uhome",""))){
			Log.v(TAG, "Matched Home"+ p.getString("uhome",""));
			if(!(floor.equals("notset"))){
				if(p.getString("ufloor","").equals("notset"))
					return false;
				if((!p.getString("ufloor","").equals("notset")) && (!floor.equalsIgnoreCase(p.getString("ufloor","")))){
					return false;
				}
				Log.v(TAG, "Matched Floor"+ p.getString("ufloor",""));
			}

			if(!room.equals("notset")){
				if(p.getString("uroom","").equals("notset"))
					return false;
				if(!p.getString("uroom","").equals("notset") && !room.equalsIgnoreCase(p.getString("uroom","")))
					return false;
				Log.v(TAG, "Matched room"+ p.getString("uroom",""));
			}


			if(!inRoom.equals("notset")){
				if(p.getString("uinRoom","").equals("notset")){
					return false;
				}
				if(!p.getString("uinRoom","").equals("notset") && !inRoom.equalsIgnoreCase(p.getString("uinRoom",""))){
					return false;
				}
				Log.v(TAG, "Matched iRroom"+ p.getString("uinRoom",""));
			}


			if(!favorite.equals("notset")){
				if(p.getString("ufavorite","").equals("notset"))
					return false;
				if(!p.getString("ufavorite","").equals("notset") && !favorite.equalsIgnoreCase(p.getString("ufavorite","")))
					return false;
				Log.v(TAG, "Matched room"+ p.getString("ufavorite",""));
			}
			Log.v(TAG, "match done");
			return true;
		}else
			return false;

	}


	private void sendNotifyMessage() {
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);	
		Log.d(TAG," Sending message to get locations of notifications services " );
		if(p.getString("service","").equals("LED")){			
			Message message1 = new Message(serviceId);
			message1.addAction("notify");
			message1.addServiceType("LED");
			message1.addProperty("Location");
			message1.addPropertyValue("Location", "HOME",p.getString("uhome",""));
			message1.addPropertyValue("Location", "FLOOR", p.getString("ufloor",""));
			message1.addPropertyValue("Location", "ROOM", p.getString("uroom",""));
			message1.addPropertyValue("Location", "INROOM", p.getString("uinRoom",""));
			message1.addPropertyValue("Location", "USERTAG", p.getString("ufavorite",""));
			Log.d(TAG," Sending message to get locations of LED services with location"+ " "+p.getString("uhome","")+
					p.getString("ufloor","")+p.getString("uroom","")+p.getString("uinRoom","")+p.getString("ufavorite",""));
			sdl.sendMessage(message1);		
		}

		else if(p.getString("service","").equals("Speaker")){
			Message message = new Message(serviceId);
			message.addAction("notify");
			message.addServiceType("Speaker");
			message.addProperty("Location");
			message.addPropertyValue("Location", "HOME", p.getString("uhome",""));
			message.addPropertyValue("Location", "FLOOR", p.getString("ufloor",""));
			message.addPropertyValue("Location", "ROOM", p.getString("uroom",""));
			message.addPropertyValue("Location", "INROOM", p.getString("uinRoom",""));
			message.addPropertyValue("Location", "USERTAG", p.getString("ufavorite",""));
			Log.d(TAG," Sending message to get locations of Speaker services with location"+ " "+p.getString("uhome","")+
					p.getString("ufloor","")+p.getString("uroom","")+p.getString("uinRoom","")+p.getString("ufavorite",""));
			sdl.sendMessage(message);
		}
		else{			
			Log.d(TAG," Sending message to get locations of Speaker and LED services with location"+ " "+p.getString("uhome","")+
					p.getString("ufloor","")+p.getString("uroom","")+p.getString("uinRoom","")+p.getString("ufavorite",""));
			Message message = new Message(serviceId);
			message.addAction("notify");
			message.addServiceType("Speaker");
			message.addProperty("Location");
			message.addPropertyValue("Location", "HOME", p.getString("uhome",""));
			message.addPropertyValue("Location", "FLOOR", p.getString("ufloor",""));
			message.addPropertyValue("Location", "ROOM", p.getString("uroom",""));
			message.addPropertyValue("Location", "INROOM", p.getString("uinRoom",""));
			message.addPropertyValue("Location", "USERTAG", p.getString("ufavorite",""));
			sdl.sendMessage(message);
			Message message1 = new Message(serviceId);
			message1.addAction("notify");
			message1.addServiceType("LED");
			message1.addProperty("Location");
			message1.addPropertyValue("Location", "HOME", p.getString("uhome",""));
			message1.addPropertyValue("Location", "FLOOR", p.getString("ufloor",""));
			message1.addPropertyValue("Location", "ROOM", p.getString("uroom",""));
			message1.addPropertyValue("Location", "INROOM", p.getString("uinRoom",""));
			message1.addPropertyValue("Location", "USERTAG", p.getString("ufavorite",""));
			sdl.sendMessage(message1);
		}
	}

	public void locationReceived(Object triggerData, Object srcServiceId) {

		String[] location;
		Log.d(TAG,"Location that is received " + (String)triggerData);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);	
		location = ((String)triggerData).split("\\+");
		p.edit().putString("uhome", location[0]).commit();
		p.edit().putString("ufloor", location[1]).commit();
		p.edit().putString("uroom", location[2]).commit();
		p.edit().putString("uinRoom", location[3]).commit();
		p.edit().putString("ufavorite", location[4]).commit();
		configurationMatch();
	}

}