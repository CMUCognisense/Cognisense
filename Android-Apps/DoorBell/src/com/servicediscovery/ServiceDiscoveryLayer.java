package com.servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.servicediscovery.Action;
import com.servicediscovery.Service;
import com.servicediscovery.Trigger;
import com.servicediscovery.Message;
import com.util.DatabaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


public class ServiceDiscoveryLayer{
	// the service id and the service objects
	Service service;
	Object appObject = null;
	boolean DEBUG = false;
	private String TAG = "SDL";
	private String intentfilter = null;
	private Context appContext = null;

	public ServiceDiscoveryLayer(boolean bool) {
		DEBUG = bool;
		DEBUG = true;
		System.out.println("Started the Multicast layer");
		service = new Service();
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * Meanwhile, this will send the intent filter to the android communication
	 * process to register 
	 * @param serviceType
	 */
	public String registerNewService(String serviceType) {
		String serviceId;
		String returnVal;
		service.setServiceType(serviceType);

		// first query the database to see if the service type exists
		if((returnVal = queryServiceType(serviceType)) != null){
			serviceId = returnVal;
		}
		// if the service type does not exist in the database, generate a new
		// service id and register this service to the database 
		else{
			serviceId  = getNewServiceId();
			ContentResolver resolver = this.appContext.getContentResolver();
	        Uri insertUri = Uri.parse("content://com.commproc.provider/INFO");
			ContentValues values = new ContentValues();
			values.put("SERVICETYPE", serviceType);
			values.put("INTENTFILTER", intentfilter);
			values.put("SERVICEID", serviceId);
	        Uri uri = resolver.insert(insertUri, values);
	        Log.i(TAG, "New service, " + uri.toString());
		}

		service.setServiceid(serviceId);

		return serviceId;
	}

	/**
	 * Query the database to see if the service is already registered
	 * @param serviceType
	 * @return
	 */
	private String queryServiceType(String serviceType) {
        ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.commproc.provider/INFO/" + serviceType);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        // if no row is available for the service type
        if (cursor.getCount() == 0) {
        	cursor.close();
			return null;
		}
        // if the service type already exists in the database
        else{
        	cursor.moveToNext();
        	String id = cursor.getString(cursor.getColumnIndex("SERVICEID"));
        	Log.e(TAG, "Already registered, id: " + id);
        	cursor.close();
        	return id;
        }
	}

	/**
	 * Gives a new service id everytime
	 * @return
	 */	
	public String getNewServiceId() {
		UUID a = UUID.randomUUID();
		String message =  "" + a;
		return message;
	}

	/**
	 * this method generates a string from the message object and then passes it to the multicast layer
	 * reliable multicast layer. 
	 * @param message
	 */
	public void sendMessage(Message message) {
		// send intents here
		String msg = message.generateMessage();

		// send intent to the android communication process to send the msg
		Intent i = new Intent("commservice");
		i.putExtra("command", "SENDALL");
		i.putExtra("message", msg);
		Log.e(TAG, "Send msg");
		appContext.sendBroadcast(i);
	}

	/**
	 * Register the application to the service discovery layer on the application side
	 * and do some initialization
	 * @param appObject
	 * @param intentfilter
	 * @param context
	 */
	public void registerApp(Object appObject, String intentfilter, Context context) {
		this.appObject = appObject;
		this.appContext = context;
		this.intentfilter = intentfilter;
	}

	public void registerActions(String methodName, String actionName, 
			Class appClass) throws Exception {
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the action
		Action action = new Action(appMethod, actionName!=null?actionName:methodName);
		service.addAction(action);
	}

	public void registerTriggers(String methodName, String triggerName,
			Class appClass) throws Exception {
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the trigger
		Trigger trigger = new Trigger(appMethod, triggerName!=null?triggerName:methodName);
		service.addTrigger(trigger);
	}

	/**
	 * Add the string to the home table
	 * @param home
	 */
	public void addHome(String home){
		ContentResolver resolver = this.appContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.HOME_TABLE);
		ContentValues values = new ContentValues();
		values.put("HOME", home);
        Uri uri = resolver.insert(insertUri, values);
        Log.i(TAG, "new home, " + uri.toString());
	}

	/**
	 * Add the string to the floor table
	 * @param floor
	 */
	public void addFloor(String floor){
		ContentResolver resolver = this.appContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.FLOOR_TABLE);
		ContentValues values = new ContentValues();
		values.put("FLOOR", floor);
        Uri uri = resolver.insert(insertUri, values);
        Log.i(TAG, "new floor, " + uri.toString());
	}

	/**
	 * Add the string to the room table
	 * @param room
	 */
	public void addRoom(String room){
		ContentResolver resolver = this.appContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.ROOM_TABLE);
		ContentValues values = new ContentValues();
		values.put("ROOM", room);
        Uri uri = resolver.insert(insertUri, values);
        Log.i(TAG, "New room, " + uri.toString());
	}
	
	/**
	 * Add the string to the user tag table
	 * @param usertag
	 */
	public void addUsertag(String usertag){
		ContentResolver resolver = this.appContext.getContentResolver();
        Uri insertUri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.USERTAG_TABLE);
		ContentValues values = new ContentValues();
		values.put("USERTAG", usertag);
        Uri uri = resolver.insert(insertUri, values);
        Log.i(TAG, "New user tag, " + uri.toString());
	}

	/**
	 * Get the list of home strings from the database
	 * @return
	 */
	public ArrayList<String> getHomes(){
		ArrayList<String> homes = new ArrayList<String>(); 
		ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.HOME_TABLE);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
		while(cursor.moveToNext()){
			String home = cursor.getString(cursor.getColumnIndex("HOME"));
			homes.add(home);
		}
		cursor.close();
		return homes;
	}
	
	/**
	 * Get the list of floor strings from the database
	 * @return
	 */
	public ArrayList<String> getFloors(){
		ArrayList<String> floors = new ArrayList<String>(); 
		ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.FLOOR_TABLE);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
		while(cursor.moveToNext()){
			String floor = cursor.getString(cursor.getColumnIndex("FLOOR"));
			floors.add(floor);
		}
		cursor.close();
		return floors;
	}
	
	/**
	 * Get the list of room strings from the database
	 * @return
	 */
	public ArrayList<String> getRooms(){
		ArrayList<String> rooms = new ArrayList<String>(); 
		ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.ROOM_TABLE);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
		while(cursor.moveToNext()){
			String room = cursor.getString(cursor.getColumnIndex("ROOM"));
			rooms.add(room);
		}
		cursor.close();
		return rooms;
	}

	/**
	 * Get the list of usertag strings from the database
	 * @return
	 */
	public ArrayList<String> getUsertags(){
		ArrayList<String> usertags = new ArrayList<String>(); 
		ContentResolver contentResolver = appContext.getContentResolver();
        Uri uri = Uri.parse("content://com.commproc.provider/" + DatabaseHelper.USERTAG_TABLE);
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
		while(cursor.moveToNext()){
			String usertag = cursor.getString(cursor.getColumnIndex("USERTAG"));
			usertags.add(usertag);
		}
		cursor.close();
		return usertags;
	}

	public void addProperty(Property property) {
		if(property.name == null)
			property.name = property.getClass().getName();
		service.addProperties(property);
	}

	public void addLocation() {
		Location location = new Location();
		location.addLocation("MyHome", "one", "Bedroom", "Top", "onDoor");
		service.addProperties(location);
	}

	public Map<String,Property> getProperties() {
		return service.getProperties();
	}

	public List<String> getActions() {
		List<String> list = new LinkedList<String>();

		if(service.getActions()!=null)
		{
			for(Action action:service.getActions().values())
				list.add(action.getActionTag());
		}
		return list;
	}

	public List<String> getTriggerGenerated() {
		return service.getTriggerGenerated(); 
	}


	/**
	 * Get action object by the action name
	 * @param actionName
	 * @return
	 */
	public Action getAction(String actionName){
		for(Action action : service.getActions().values()){
			if (action.getActionTag().equals(actionName)) {
				return action;
			}
		}
		return null;
	}

	public List<String> getTriggers() {
		List<String> list = new LinkedList<String>();

		if(service.getTriggers()!=null)
		{
			for(Trigger trig:service.getTriggers().values())
				list.add(trig.getTriggerTag());
		}
		return list;
	}

	/**
	 * Get the trigger object with the trigger name
	 * @param triggerName
	 * @return
	 */
	public Trigger getTrigger(String triggerName){
		for(Trigger trigger : service.getTriggers().values()){
			if(trigger.getTriggerTag().equals(triggerName)){
				return trigger;
			}
		}
		return null;
	}

	/**
	 * This will have an intent receiver which will call methods that are registered. 
	 * like the on receive of the sdlcommon
	 * @param intent
	 */
	public void callMethod(Intent intent){
		//check the intent
		if (intent != null) {
			String msg = (String) intent.getSerializableExtra("message");
			Message message = Message.parseMessage(msg);

			if(message != null){
				Log.e(TAG, "Receive a message from android comm proc");
				Log.d(TAG, "Action: " + message.getAction() + " Trigger: " + message.getTriggerName());

				System.out.print(service.getTriggers().toString());
				List<String> idList = message.getServiceIds();
				List<String> typeList = message.getServiceTypes();
				Set<Service> set = new HashSet<Service>(); 

				try{
					Log.e(TAG, idList.toString() + " " + typeList.toString());
					checkServiceIds(idList, set);
					checkServiceTypes(typeList, set);


					// if there is match for id or type and property is not specified
					if (set.size() > 0 && message.getProperties().size() == 0) 
					{
						if(message.getAction()!=null)
							performActions(message);
						else
							performTriggers(message);
					}
					// if there is match for id or type and property is specified
					else if (set.size() > 0 && message.getProperties().size() > 0)
					{
						Set<Service> list = new HashSet<Service>();
						for(String propertyName : message.getProperties())
						{
							if(service.isPropertyMatching(propertyName, message.getPropertyAttributes(propertyName)))
								list.add(service);
							else
								break;
						}
						if (!list.isEmpty()) {
							// not the set only contains services that have matching properties. 
							if(message.getAction()!=null)
								performActions(message);
							else
								performTriggers(message);
						}
					}
					// if type and id are not specified, perform actions or triggers
					else if(idList.size() == 0 && typeList.size() == 0)
						if(message.getAction()!=null)
							performActions(message);
						else
							performTriggers(message);

				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}	


	/**
	 * Perform actions specified in the incoming message
	 * @param message
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void performActions(Message message) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Action action = service.isActionPresent(message.getAction());
		if(action!=null)action.getMethod().invoke(appObject,message.getActionInput(),message.getSrcServiceID());
	}

	/**
	 * Perform triggers specified in the incoming message
	 * @param message
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void performTriggers(Message message) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Log.e(TAG, "Perform Trigger");
		Trigger trigger = service.isTriggerPresent(message.getTriggerName());
		if(trigger!=null)trigger.getMethod().invoke(appObject,message.getTriggerData(),message.getSrcServiceID());
	}

	/**
	 * Check if the service id matches any of the ids specified in the id list
	 * if there is a match, add the service to the set
	 * @param idList
	 * @param set
	 */
	private void checkServiceIds(List<String> idList, Set<Service> set) {
		for (String incomingSId : idList) {
			if (service.getServiceid().equals(incomingSId)) {
				set.add(service);
			}
		}
	}

	/**
	 * Check if the service type matches any of the types specified in the type list
	 * if there is a match, add the service to the set
	 * @param typeList
	 * @param set
	 */
	private void checkServiceTypes(List<String> typeList, Set<Service> set) {
		for (String type : typeList) {
			if (service.getServiceType().equals(type)) {
				set.add(service);
			}
		}
	}

	/**
	 * This method generate a trigger which can provide the information of this 
	 * service.
	 * @return
	 */
	public String getInfo() {
		StringBuilder builder = new StringBuilder();

		if(service != null)
		{
			builder.append("SERVICEID-");
			builder.append(service.getServiceid());
			builder.append(",");
			builder.append("SERVICETYPE-");
			builder.append(service.getServiceType());
			builder.append(",");
			for(String action: getActions())
			{
				builder.append("ACTION-");
				builder.append(action);
				builder.append(",");
			}
			for(String gentrigger: getTriggerGenerated())
			{
				builder.append("TRIGGERSGEN-");
				builder.append(gentrigger);
				builder.append(",");
			}
			for(String trigger: getTriggers())
			{
				builder.append("TRIGGERS-");
				builder.append(trigger);
				builder.append(",");
			}
			for( Property property: getProperties().values())
				builder.append(property.printProperty());

		}

		return builder.toString();
	}

}
