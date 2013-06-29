package servicediscovery;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import multicast.MulticastLayer;
import multicast.MulticastReceive;
import multicast.RecvMessageEvent;

public class ServiceDiscoveryLayer implements MulticastReceive {

	MulticastLayer multicastLayer = null;
	Set<Service> services;
	ArrayList<Method> methods = new ArrayList<Method>();
	Application appObject = null;
	boolean DEBUG = false;

	// ArrayList<Integer> list = new ArrayList<Integer>();
	// int randomUniqueSNIndex = 0;

	// int uniqueSequenceNum = 0;

	// public void setDeviceNumRange(int lowerLimit, int upperLimit) {
	// for (int i = lowerLimit; i < upperLimit; i++) {
	// list.add(i + 1);
	// }
	//
	// }
	//
	// public void randomSequenceNum() {
	//
	// Collections.shuffle(list);
	//
	// }

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

	public void callMethod(Object parameters) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		methods.get(0).invoke(appObject, parameters);
	}

	@Override
	public void onReceiveMessage(RecvMessageEvent e) {
		System.out.println("Message Received: " + e.getMessage());
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

	public String assignUniqueSequenceNum(RecvMessageEvent e) {
		UUID a = UUID.randomUUID();
		String message = e.getMessage();
		message = "REP:" + a + message;
		return message;
	}
}

// public String assignUniqueSequenceNum(RecvMessageEvent e) {
//
// if (list.isEmpty()) {
// setDeviceNumRange(1000000,10000000);
// randomSequenceNum();
// } else {
// String message = e.getMessage();
// message = "REP:" + list.get(randomUniqueSNIndex++) + message;
// return message;
// }
// return null;
//
// }
// }
// } else {
//
// int SN = sequenceNum++;
// String message = e.getMessage();
// message = "" + SN + message;
// return message;
//
// }

