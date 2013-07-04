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

public class ServiceDiscoveryLayer implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// the serviceid and the service objects
	Map<String, Service> services;

	Object appObject = null;
	boolean DEBUG = false;

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
	 * 
	 * @param service
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
	 * Gives a new service id everytime
	 * 
	 * @return
	 */
	public String getNewServiceId() {

		UUID a = UUID.randomUUID();
		String message = "" + a;
		return message;
	}

	/**
	 * this method generates a string from the message object and then passes it
	 * to the multicast layer reliable multicast layer.
	 * 
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

	public void registerApp(Object appObject) {
		this.appObject = appObject;
	}

	public void registerActions(String serviceID, String methodName, String actionName, 
			Class appClass) throws Exception {
		Service service = services.get(serviceID);
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the action
		Action action = new Action(appMethod, actionName!=null?actionName:methodName);
		service.addAction(action);
	}

	public void registerTriggers(String serviceID, String methodName, String triggerName,
			Class appClass) throws Exception {
		Service service = services.get(serviceID);
		Method appMethod = appClass.getMethod(methodName, Object.class,
				Object.class);
		// sets the name and Method of the trigger
		Trigger trigger = new Trigger(appMethod, triggerName!=null?triggerName:methodName);
		service.addTrigger(trigger);
	}
	
	public void addTriggerGenerated(String serviceId, String trigger) {
		services.get(serviceId).addTriggerGenerated(trigger);
	}
	
	public List<String> getTriggerGenerated(String serviceId) {
		return services.get(serviceId).getTriggerGenerated(); 
	}

	public void addProperty(String serviceId, Property property) {
		Service service = services.get(serviceId);
		if (property.name == null)
			property.name = property.getClass().getName();
		service.addProperties(property);
	}

	public void addLocationProperty(String serviceId) {
		Service service = services.get(serviceId);
		Location location = new Location();
		service.addProperties(location);
	}
	
	public void addLocationValue(String serviceId, String home, String floor, String room, String inRoom, String userTag) {
		Service service = services.get(serviceId);
		Location location = (Location) service.getProperty("Location");
		location.addLocation(home, floor, room, inRoom, userTag);
		service.setProperty("Location", location);
	}

	

	public Map<String, Property> getProperties(String serviceId) {
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
