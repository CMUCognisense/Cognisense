package servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import multicast.MulticastLayer;
import multicast.MulticastReceive;
import multicast.RecvMessageEvent;


public class ServiceDiscoveryLayer implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	// the serviceid  and the service objects
	Map<String,Service> services;
	//ArrayList<Method> methods = new ArrayList<Method>();
	Application appObject = null;
	boolean DEBUG = false;
	private int idcounter;
	Location location;

	public ServiceDiscoveryLayer(boolean bool) {
		multicastLayer = new MulticastLayer();
		location = new Location();
		multicastLayer.DEBUG = false;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		services = new HashMap<String,Service>(2);
		idcounter=0;
	}

	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * @param service
	 */
	public String registerNewService(String serviceType) {

		Service service = new Service();
		service.setServiceType(serviceType);
		String  serviceId  = getNewServiceId();
		service.setServiceid(serviceId);
		services.put(serviceId,service);
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

		String msg = message.generateMessage();

		if(msg!=null)
			multicastLayer.sendAll(msg);
		else 
			throw new IllegalArgumentException("Message is not valid");
	}

	public void registerApp(Application appObject) {
		this.appObject = appObject;
	}

	public void registerActions(String serviceID,Method method) throws Exception {
		Service service = services.get(serviceID);
		// sets the name and Method of the action
		Action action = new Action(method,method.getName());
		service.addAction(action);
	}

	public void registerTriggers(String serviceID,Method method) throws Exception {
		Service service = services.get(serviceID);
		// sets the name and Method of the action
		Trigger trigger = new Trigger(method,method.getName());
		service.addTrigger(trigger);
	}

	public void addProperty(String serviceId, Property property) {
		Service service = services.get(serviceId);
		if(property.name == null)
			property.name = property.getClass().getName();
		service.addProperties(property);
	}

	public void addLocation(String serviceId, Object actionInput) {
		Service service = services.get(serviceId);
		String newLocation = (String)actionInput;
		String [] locations = newLocation.split("\\+");
		if(locations[0]!=null){
		location.addLocation(locations[0],locations[1],locations[2],locations[3],locations[4]);
		service.addProperties(location);
		}
	}
			
	public String getLocations(){
		String loc = location.provideLocation();
		if(loc!=null){
				return loc;
		}else
			return null;
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

	public void callMethod(Object parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		//methods.get(0).invoke(appObject, parameters);
	}

	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		System.out.println("Message Received: "+e.getMessage());
		Message message = Message.parseMessage(e.getMessage());
		String actionName = message.getAction();
		String triggerName = message.getTriggerName();
		try {
			if(actionName!=null)
			{
				for(Service service: services.values())
				{
					for(Action action:service.getActions())
					{
						//TODO this has to be generic as i do not know what argument to give.
						// at the same time the service requires the message object to get the 
						// service src id and the action and the trigger data. 
						action.getMethod().invoke(appObject, message.getActionInput(),message.getSrcServiceID());
					}
				}
			}
			else if(triggerName!=null)
			{
				for(Service service: services.values())
				{
					for(Trigger trigger:service.getTrigger())
					{
						//TODO this has to be generic as i do not know what argument to give.
						// at the same time the service requires the message object to get the 
						// service src id and the action and the trigger data. 
						trigger.getMethod().invoke(appObject, message.getTriggerData(), message.getSrcServiceID());
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
