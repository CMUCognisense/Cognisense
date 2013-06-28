package servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import multicast.MulticastLayer;
import multicast.MulticastReceive;
import multicast.RecvMessageEvent;


public class ServiceDiscoveryLayer implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	Set<Service> services;
	ArrayList<Method> methods = new ArrayList<Method>();
	Application appObject = null;
	boolean DEBUG = false;
	
	public ServiceDiscoveryLayer(boolean bool) {
		multicastLayer = new MulticastLayer();
		multicastLayer.DEBUG = bool;
		DEBUG = bool;
		System.out.println("Started the Multicast layer");
		multicastLayer.addEventListener(this);
		services = new HashSet<Service>(2);
	}
	
	/**
	 * this method will add a service to the service set of the SDL
	 * It will perform all the checks for a valid service that need to be done. 
	 * @param service
	 */
	public void registerService(Service service) {
		
	}
	
	/**
	 * this method generates a string from the message object and then passes it to the
	 * This method also checks if the message is valid for generation or not
	 * it will throw an excaption is the message is not valid i.e. does not have a trigger 
	 * or an action or has both. 
	 * reliable multicast layer. 
	 * @param message
	 */
	public void sendMessage(Message message) {
		
		String msg = generateMessage(message);
		
		if(msg!=null)
			multicastLayer.sendAll(msg);
		else 
			throw new IllegalArgumentException("Message is not valid");
	}
	
	/**
	 * This method parses the message object and generates the message to be put on the wire
	 * @param message
	 * @return
	 */
	private String generateMessage(Message message) {

		return null;
	}
	
	/**
	 * This method is used to parse a given message from the reliable multicast layer and 
	 * convert it to a Message object it is a complement of the generateMessage mesage which 
	 * does the exact opposite
	 * @param message
	 * @return
	 */
	private Message parseMessage(String message) {
		
		return null;
	}
	
	public void registerApp(Application appObject) {
		this.appObject = appObject;
	}
	
	public void registerActions(Method method) throws Exception {
        methods.add(method);
    }
	
	public void callMethod(Object parameters) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        methods.get(0).invoke(appObject, parameters);
	}
	
	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		System.out.println("Message Received: "+e.getMessage());
		String[] strary = new String[1];
        strary[0] = e.getMessage();
		try {
			callMethod(strary);
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
