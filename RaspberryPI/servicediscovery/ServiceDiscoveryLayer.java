package servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import multicast.MulticastLayer;
import multicast.MulticastReceive;
import multicast.RecvMessageEvent;

/**
 * this is the service discovery class. This class is the core of the service discovery layer. 
 * An object of this class is instanticted by each application. This class provides the api that 
 * the application uses. The service discovery uses the multicast layer class.  
 * @author parth
 *
 */
public class ServiceDiscoveryLayer implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// the serviceid and the service objects
	Map<String, Service> services;

	Object appObject = null;
	boolean DEBUG = false;

	/**
	 * the constructor initiaated an objecct of the service disvoery layer. 
	 * @param bool boolean to enable/disable debug messages for this class
	 * @param multicast to enable disable debug messaged for the multicas layer
	 */
	public ServiceDiscoveryLayer(boolean bool, boolean multicast) {
		multicastLayer = new MulticastLayer(multicast);
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		services = new HashMap<String, Service>(2);
	}

	/**
	 * this method will add a service to the service set of the SDL It will
	 * perform all the checks for a valid service that need to be done.
	 * returns a service id of the new service that has just been registered. 
	 * this serviceid is used to refer to this particular service for any subsequent calls to other functions
	 * @param service the service type of the service being registered. 
	 * @return returns a service id of the new service that has just been registered.  
	 */
	public String registerNewService(String serviceType) {

		Service service = new Service();
		service.setServiceType(serviceType);
		String serviceId = getNewServiceId();
		service.setServiceid(serviceId);
		services.put(serviceId, service);
		return serviceId;

	}

	/**
	 * Gives a new service id everytime it is called
	 * @return
	 */
	private String getNewServiceId() {

		UUID a = UUID.randomUUID();
		String message = "" + a;
		return message;
	}

	/**
	 * this method generates a string from the message object and then passes it
	 * to the multicast layer reliable multicast layer.
	 * @param message the message object for the message to be sent
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

	/**
	 * this is used to register the application object. This application object is the object that is used to 
	 * call Methods of the actions and the triggers that are registered by the application. 
	 * @param appObject the object of the application class. 
	 */
	public void registerApp(Object appObject) {
		this.appObject = appObject;
	}

	/**
	 * registers actions to a particular service. 
	 * @param serviceID the service ID to which the action is to be register to. 
	 * @param methodName the name of the method in the application class that is to be 
	 * associatedd with the action. 
	 * @param actionName the name of the action. 
	 * @param appClass the class of the application. 
	 * @throws Exception throws no such method exception. 
	 */
	public void registerActions(String serviceID, String methodName, String actionName, 
			Class appClass) throws Exception {
		Service service = services.get(serviceID);
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the action
		Action action = new Action(appMethod, actionName!=null?actionName:methodName);
		service.addAction(action);
	}

	/**
	 * registers triggers to a particular service. 
	 * @param serviceID the service ID to which the trigger is to be register to. 
	 * @param methodName the name of the method in the application class that is to be 
	 * associatedd with the trigger. 
	 * @param triggerName the name of the trigger. 
	 * @param appClass the class of the application. 
	 * @throws Exception throws no such method exception.
	 */
	public void registerTriggers(String serviceID, String methodName, String triggerName,
			Class appClass) throws Exception {
		Service service = services.get(serviceID);
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the trigger
		Trigger trigger = new Trigger(appMethod, triggerName!=null?triggerName:methodName);
		service.addTrigger(trigger);
	}
	
	/**
	 * add a trigger generated
	 * @param serviceId the service id of the service to which the trigger generated is to be added
	 * @param trigger the name of the trigger generated by the service
	 */
	public void addTriggerGenerated(String serviceId, String trigger) {
		services.get(serviceId).addTriggerGenerated(trigger);
	}
	/**
	 * get a list of generated triggers for a particular service
	 * @param serviceId the service id of the service
	 * @return the list of triggers generated by the service
	 */
	public List<String> getTriggerGenerated(String serviceId) {
		return services.get(serviceId).getTriggerGenerated(); 
	}

	/**
	 * add the property to the particular service
	 * @param serviceId the service id to which th property is to be added
	 * @param property the property object to be added. 
	 */
	public void addProperty(String serviceId, Property property) {
		Service service = services.get(serviceId);
		service.addProperties(property);
	}

	/**
	 * add the location property to the particular service
	 * @param serviceId the service id to which the location property is to be added
	 */
	public void addLocationProperty(String serviceId) {
		Service service = services.get(serviceId);
		Location location = new Location();
		service.addProperties(location);
	}
	
	/**
	 * add the location value to the location property to a particular service specified by the service id
	 * The location property has to be added for that service before a call to this.  
	 * @param serviceId the serviceid to which the location value has to be addded. 
	 * @param home home name cannot be null
	 * @param floor floor name or 'notset'
	 * @param room room name or 'notset'
	 * @param inRoom inRoom location or 'notset'
	 * @param userTag user tag name or 'notset'
	 */
	public void addLocationValue(String serviceId, String home, String floor, String room, String inRoom, String userTag) {
		Service service = services.get(serviceId);
		Location location = (Location) service.getProperty("Location");
		
		if(home.equalsIgnoreCase("notset"))
			home=null;
		if(floor.equalsIgnoreCase("notset"))
			floor=null;
		if(room.equalsIgnoreCase("notset"))
			room=null;
		if(inRoom.equalsIgnoreCase("notset"))
			inRoom=null;
		if(userTag.equalsIgnoreCase("notset"))
			userTag=null;
		
		location.addLocation(home, floor, room, inRoom, userTag);
		service.setProperty("Location", location);		
	}

	
	
	/**
	 * returns all the properties of a particular service
	 * @param serviceId the service id whose properties are to be returned
	 * @return the map whose key is property name and value is property object. 
	 */
	public Map<String, Property> getProperties(String serviceId) {
		return services.get(serviceId).getProperties();
	}

	/**
	 * get a list of actions registered to the service with a given service ID. 
	 * @param serviceId the service id of the service whose actions are needed
	 * @return the list of strings full of action names of a service. 
	 */
	public List<String> getActions(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if (service.getActions() != null) {
			for (Action action : service.getActions().values())
				list.add(action.getActionTag());
		}
		return list;
	}


	/**
	 * get a list of triggers registered to the service with a given service ID. 
	 * @param serviceId the service id of the service whose triggers are needed
	 * @return the list of strings full of trigger names of a service. 
	 */
	public List<String> getTriggers(String serviceId) {
		Service service = services.get(serviceId);
		List<String> list = new LinkedList<String>();

		if (service.getTriggers() != null) {
			for (Trigger trig : service.getTriggers().values())
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
	 * this method process the incoming message that is given by the reliable multicast layer. 
	 * @param message
	 */
	private void processIncomingMessage(String msg) {
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

	/**
	 * this method is called when the multicast layer gets a new message. This is a method that processes
	 * a new message 
	 */
	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		if(DEBUG)System.out.println("Message Received: " + e.getMessage());
		processIncomingMessage(e.getMessage());
	}

	/**
	 * returns a string describing the service whos service id is passed
	 * @param serviceId the service id whose service is needed
	 * @return the string containing information about the service. 
	 */
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
			{
				builder.append(property.printProperty());
				builder.append(",");
			}
				

			
		}
		
		return builder.toString();
	}

	/**
	 * this returns the ipaddress of the interface being used. 
	 * @return the ipaddress in a string format. 
	 */
	public String getIpAddress() {
		return multicastLayer.getCurrentEnvironmentNetworkIp();

	}
}
