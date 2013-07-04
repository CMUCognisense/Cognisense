package com.servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.text.style.UpdateAppearance;
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
		System.out.println("Started the Multicast layer");
		service = new Service();
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * Register the service to the service discovery layer on the android
	 * communication side at the same time.
	 * @param serviceType
	 */
	public String registerNewService(String serviceType) {
		service.setServiceType(serviceType);
		String  serviceId  = getNewServiceId();
		service.setServiceid(serviceId);
		
		// TODO send an intent to the sdlcommon and register the service
		// register the service to the android communication process 
		Intent registerService = new Intent("commservice");
		registerService.putExtra("command", "REGISTER");
		registerService.putExtra("intentfilter", intentfilter);
		registerService.putExtra("service", service);
		appContext.sendBroadcast(registerService);
		
		return serviceId;
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

		//TODO send intent to the android communication process to update the action for service
		updateOnCommProc();
	}

	public void registerTriggers(String methodName, String triggerName,
			Class appClass) throws Exception {
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the trigger
		Trigger trigger = new Trigger(appMethod, triggerName!=null?triggerName:methodName);
		service.addTrigger(trigger);
		
		//TODO send intent to the android communication process to update the trigger for service
		updateOnCommProc();
	}

	public void addProperty(Property property) {
		if(property.name == null)
			property.name = property.getClass().getName();
		service.addProperties(property);

		//TODO send intent to the android communication process to update the property for service
		updateOnCommProc();
	}

	public void addLocation() {
		Location location = new Location();
		location.addLocation("MyHome", "one", "Bedroom", "Top", "onDoor");
		service.addProperties(location);
		
		//TODO send intent to the android communication process to update the location for service
		updateOnCommProc();
	}

	public Map<String,Property> getProperties() {
		return service.getProperties();
	}

	public List<String> getActions() {
		List<String> list = new LinkedList<String>();

		if(service.getActions()!=null)
		{
			for(Action action:service.getActions())
				list.add(action.getActionTag());
		}
		return list;
	}
	
	/**
	 * Get action object by the action name
	 * @param actionName
	 * @return
	 */
	public Action getAction(String actionName){
		for(Action action : service.getActions()){
			if (action.getActionTag().equals(actionName)) {
				return action;
			}
		}
		return null;
	}

	public List<String> getTriggers(String serviceId) {
		List<String> list = new LinkedList<String>();

		if(service.getTrigger()!=null)
		{
			for(Trigger trig:service.getTrigger())
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
		for(Trigger trigger : service.getTrigger()){
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
			Message message = (Message) intent.getSerializableExtra("message");
			if(message != null){
				Log.e(TAG, "Receive a message from android comm proc");
				// get the action and trigger to see what action to perform
				String actionName = message.getAction();
				String triggerName = message.getTriggerName();
				// this has to be an intent to the particular service. 
				try{
					if(actionName!=null)
					{
						// if the message specify an action, perform the action
						Action action = getAction(actionName);
						action.getMethod().invoke(appObject, message.getActionInput(),message.getSrcServiceID());
					}
					else if(triggerName!=null)
					{
						// if the message specify a trigger, call registered trigger
						Trigger trigger = getTrigger(triggerName);
						trigger.getMethod().invoke(appObject, message.getTriggerData(), message.getSrcServiceID());
					}
				} catch (IllegalArgumentException e1) {
					Log.e(TAG, "error in invoking");
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					Log.e(TAG, "error in invoking");
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					Log.e(TAG, "error in invoking");
					e1.printStackTrace();
				}
			}
		}
	}	
	
	/**
	 * Send intent to the android communication process and update the service
	 * on the service discovery layer of android communication process
	 */
	private void updateOnCommProc() {
		Intent update = new Intent("commservice");
		update.putExtra("command", "UPDATE");
		update.putExtra("service", service);
		appContext.sendBroadcast(update);
	}

	public String getInfo(String serviceId) {
		StringBuilder builder = new StringBuilder();
			
		if(services.get(serviceId) != null)
		{
			builder.append("SERVICEID-");
			builder.append(serviceId);
			builder.append(",");
			Service service = services.get(serviceId);
			builder.append("SERVICETYPE-");
			builder.append(service.getServiceType());
			builder.append(",");
			for(String action: getActions(serviceId))
			{
				builder.append("ACTION-");
				builder.append(action);
				builder.append(",");
			}
			for(String gentrigger: getTriggerGenerated(serviceId))
			{
				builder.append("TRIGGERSGEN-");
				builder.append(gentrigger);
				builder.append(",");
			}
			for(String trigger: getTriggers(serviceId))
			{
				builder.append("TRIGGERS-");
				builder.append(trigger);
				builder.append(",");
			}
			for( Property property: getProperties(serviceId).values())
				builder.append(property.printProperty());
			
		}
		
		return builder.toString();
	}

}
