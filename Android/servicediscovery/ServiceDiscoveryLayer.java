package servicediscovery;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ServiceDiscoveryLayer{
	// the serviceid  and the service objects
	Service service;
	Object appObject = null;
	boolean DEBUG = false;
	private int idcounter;
	private String TAG = "SDL";
	
	public ServiceDiscoveryLayer(boolean bool) {
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		service = new Service();
		idcounter=0;
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * @param service
	 */
	public String registerNewService(String serviceType) {
		service.setServiceType(serviceType);
		String  serviceId  = getNewServiceId();
		service.setServiceid(serviceId);
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
	public void sendMessage(Context context, Message message) {
		//TODO send intents here
		String msg = message.generateMessage();

		// send intent to the android communication process to send the msg
		Intent i = new Intent("commservice");
		i.putExtra("command", "SENDALL");
		i.putExtra("message", msg);
		Log.e(TAG, "Send msg");
		context.sendBroadcast(i);
	}

	public void registerApp(Object appObject) {
		this.appObject = appObject;
	}

	public void registerActions(String methodName, Class appClass) throws Exception {
		Method appMethod = appClass.getMethod(methodName, Object.class, Object.class);
		// sets the name and Method of the action
		Action action = new Action(appMethod,appMethod.getName());
		service.addAction(action);
	}

	public void registerTriggers(String methodName, Class appClass) throws Exception {
		Method appMethod = appClass.getMethod(methodName, Object.class, Object.class);
		// sets the name and Method of the trigger
		Trigger trigger = new Trigger(appMethod,appMethod.getName());
		service.addTrigger(trigger);
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
			for(Action action:service.getActions())
				list.add(action.getActionTag());
		}
		return list;
	}
	
	//get action object using action name
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

	// get the trigger object using trigger name
	public Trigger getTrigger(String triggerName){
		for(Trigger trigger : service.getTrigger()){
			if(trigger.getTriggerTag().equals(triggerName)){
				return trigger;
			}
		}
		return null;
	}
	
	//TODO  this will have an intent receiver which will call methods that are registerd. like the on receive 
	// of the sdlcommon
	
}
