package sdlcommon;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import servicediscovery.Action;
import servicediscovery.Message;
import servicediscovery.Service;
import servicediscovery.Trigger;

import android.content.Context;

import multicast.MulticastLayer;
import multicast.MulticastReceive;
import multicast.RecvMessageEvent;
import servicediscovery.Property;


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
	private int idcounter;

	public SdlCommon(Context context, boolean bool) {
		multicastLayer = new MulticastLayer(context);
		multicastLayer.DEBUG = bool;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		services = new HashMap<String,Service>(2);
		intentFilter = new HashMap<String, String>();
		serviceID = new HashMap<String, String>();
		idcounter=0;
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

		if(msg!=null)
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
		//TODO new logic goes here
		
	}

}
