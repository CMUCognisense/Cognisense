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

	public SdlCommon(Context context, boolean bool, boolean multicast) {
		multicastLayer = new MulticastLayer(context, multicast);
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
		if(DEBUG) System.out.println("Sending message "  +msg);
		if (msg != null)
		{
			multicastLayer.sendAll(msg);
			processIncomingMessage(msg);
		}
		else
			throw new IllegalArgumentException("Message is not valid");
	}

	public Map<String,Property> getProperties(String serviceId) {
		return services.get(serviceId).getProperties();
	}

	public List<String> getActions(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if (service.getActions() != null) {
			for (Action action : service.getActions().values())
				list.add(action.getActionTag());
		}
		return list;
	}

	public List<String> getTriggers(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if (service.getTrigger() != null) {
			for (Trigger trig : service.getTrigger().values())
				list.add(trig.getTriggerTag());
		}
		return list;
	}

	
	private void checkServiceIds(List<String> idList, Set<Service> set) {
		for (Service service : services.values()) {
			for (String incomingSId : idList) {
				if (service.getServiceid().equals(incomingSId)) {
					set.add(service);
				}
			}
		}
	}
	
	private void checkServiceTypes(List<String> typeList, Set<Service> set) {

		for (Service service : services.values()) {
			for (String type : typeList) {
				if (service.getServiceType().equals(type)) {
					set.add(service);
				}
			}
		}
	}
	
	private void performActions(Collection<Service>set,Message message ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Service service : set) 
		{
				Action action = service.isActionPresent(message.getAction());
				if(action!=null)action.getMethod().invoke(appObject,message.getActionInput(),message.getSrcServiceID());
		}
	}
	
	private void performTriggers(Collection<Service>set,Message message ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Service service : set) 
		{
				Trigger trigger = service.isTriggerPresent(message.getTriggerName());
				if(trigger!=null)trigger.getMethod().invoke(appObject,message.getTriggerData(),message.getSrcServiceID());
		}
	}

	
	/**
	 * this is a test method for the matching scheme This method
	 * should print out the method names to be called and the services they
	 * should be called on Looking at the message object
	 * 
	 * @param message
	 */
	public void processIncomingMessage(String msg) {
		if(DEBUG) System.out.println("Processing message");
		Message message = Message.parseMessage(msg);
		List<String> idList = message.getServiceIds();
		List<String> typeList = message.getServiceTypes();
		Set<Service> set = new HashSet<Service>(); 
		try {
			
			checkServiceIds(idList, set);
			checkServiceTypes(typeList, set);

			if (set.size()>0 && message.getProperties().size() == 0) 
			{
				if(message.getAction()!=null)
					performActions(set, message);
				else
					performTriggers(set, message);
			}
			else if (set.size()>0 && message.getProperties().size() >0)
			{
				Set<Service> list = new HashSet<Service>();
				for (Service service : set) 
				{
					for(String propertyName : message.getProperties())
					{
						if(service.isPropertyMatching(propertyName, message.getPropertyAttributes(propertyName)))
							list.add(service);
						else
							break;
						
					}
				}
				// not the set only contains services that have matching properties. 
				if(message.getAction()!=null)
					performActions(list, message);
				else
					performTriggers(list, message);
			}
			else if(idList.size() == 0 && typeList.size() == 0)
				if(message.getAction()!=null)
					performActions(services.values(), message);
				else
					performTriggers(services.values(), message);
				
				
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

	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		if(DEBUG)System.out.println("Message Received: " + e.getMessage());
		processIncomingMessage(e.getMessage());
	}

		

}
