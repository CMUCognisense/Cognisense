package servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
