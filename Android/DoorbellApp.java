
import java.io.ObjectInputStream.GetField;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class DoorbellApp {
	int countNumOfReplies;
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	 public static void main(String[] args) throws Exception{
		 	sdl = new ServiceDiscoveryLayer(true);
		 	sdl.registerApp(new DoorbellApp());
		 	serviceId = sdl.registerNewService("DoorbellApp");
	        sdl.addLocation();
	        sdl.registerTriggers("onDoorbell", DoorbellApp.class);
	        
	        Message message = new Message(serviceId);
	        message.addAction("giveInfo");
	        message.addServiceType("Doorbell");
	        sdl.sendMessage(message);
	    }

	 public void onDoorbell(Object triggerData, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
	    	System.out.println("The properties of the doorbell are :" + (String)triggerData +" SrcServiceID:"+ (String)srcServiceId);
	    	
	        
	    }	 
}
