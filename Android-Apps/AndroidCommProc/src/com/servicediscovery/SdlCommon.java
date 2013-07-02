package com.servicediscovery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.multicast.MulticastLayer;
import com.multicast.MulticastReceive;
import com.multicast.RecvMessageEvent;


public class SdlCommon implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// mapping between the service id  and the service objects
	Map<String,Service> services;
	// mapping between the service id and intent filter name
	Map<String, String> intentFilter;
	// mapping between the intent filter name and service id
	Map<String, String> serviceID; 
	Object appObject = null;
	boolean DEBUG = false;

	public SdlCommon(Context context, boolean bool) {
		multicastLayer = new MulticastLayer(context);
		multicastLayer.DEBUG = bool;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		services = new HashMap<String,Service>(2);
		intentFilter = new HashMap<String, String>();
		serviceID = new HashMap<String, String>();
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * @param service
	 */
	public void registerNewService(String intentfilter, Service service) {
		// update three mappings
		serviceID.put(intentfilter, service.getServiceid());
		intentFilter.put(service.getServiceid(), intentfilter);
		services.put(service.getServiceid(), service);
	}

	/**
	 * This method is called when the android communication process receive
	 * intent from any application or service to update the service. This method
	 * will update the hash map it is currently maintaining 
	 * @param service
	 */
	public void updateMap(Service service){
		services.put(service.getServiceid(), service);
	}
	
	/**
	 * this method generates a string from the message object and then passes it to the multicast layer
	 * reliable multicast layer. 
	 * @param message
	 */
	public void sendMessage(Message message) {

		String msg = message.generateMessage();

		if (msg != null)
			multicastLayer.sendAll(msg);
		else
			throw new IllegalArgumentException("Message is not valid");
	}

	public Map<String,Property> getProperties(String serviceId) {
		return services.get(serviceId).getProperties();
	}

	public List<String> getActions(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if(service.getActions()!=null)
		{
			for(Action action:service.getActions())
				list.add(action.getActionTag());
		}
		return list;
	}

	public List<String> getTriggers(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if(service.getTrigger()!=null)
		{
			for(Trigger trig:service.getTrigger())
				list.add(trig.getTriggerTag());
		}
		return list;
	}


	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		System.out.println("Message Received: " + e.getMessage());
		Message message = Message.parseMessage(e.getMessage());
				String actionName = message.getAction();
		String triggerName = message.getTriggerName();
		List<String> idList = message.getServiceIds();
		List<String> typeList = message.getServiceTypes();
		try {
			if (actionName != null) {
				Set<Service> set = new HashSet<Service>();
				boolean notNull = false;
				for (Service service : services.values()) {
					for (String incomingSId : idList) {
						if (service.getServiceid().equals(incomingSId)) {
							set.add(service);
							notNull = true;
						}
					}
				}
				for (Service service : services.values()) {
					for (String type : typeList) {
						if (service.getServiceType().equals(type)) {
							set.add(service);
							notNull = true;
						}
					}
				}
				if (notNull) {
					for (Service service : set) {
						for (Action action : service.getActions()) {
							if (action.getActionTag().equals(actionName)) {
								action.getMethod().invoke(appObject,
										message.getActionInput(),
										message.getSrcServiceID());
							}

						}
					}
				} else {
					for (Service service : services.values()) {
						for (Action action : service.getActions()) {
							if (action.getActionTag().equals(actionName)) {
								action.getMethod().invoke(appObject,
										message.getActionInput(),
										message.getSrcServiceID());
							}
						}
					}

				}
			} else if (triggerName != null) {
				Set<Service> set = new HashSet<Service>();
				boolean notNull = false;
				for (Service service : services.values()) {
					for (String incomingSId : idList) {
						if (service.getServiceid().equals(incomingSId)) {
							set.add(service);
							notNull = true;
						}
					}
				}
				for (Service service : services.values()) {
					for (String type : typeList) {
						if (service.getServiceType().equals(type)) {
							set.add(service);
							notNull = true;
						}
					}
				}
				if (notNull) {
					for (Service service : set) {
						for (Trigger trigger : service.getTrigger()) {
							if (trigger.getTriggerTag().equals(actionName)) {

								trigger.getMethod().invoke(appObject,
										message.getActionInput(),
										message.getSrcServiceID());
							}

						}
					}
				} else {
					for (Service service : services.values()) {
						for (Trigger trigger : service.getTrigger()) {
							if (trigger.getTriggerTag().equals(actionName)) {
								trigger.getMethod().invoke(appObject,
										message.getActionInput(),
										message.getSrcServiceID());
							}
						}
					}

				}
			}
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
