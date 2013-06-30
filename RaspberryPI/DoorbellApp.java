
import java.io.ObjectInputStream.GetField;

import servicediscovery.Application;
import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class DoorbellApp extends Application{
	int countNumOfReplies;
	String serviceId;
	
	 public static void main(String[] args) throws Exception{
	        DoorbellApp app = new DoorbellApp();
	        ServiceDiscoveryLayer sdl = app.getSdl();
	        app.serviceId = sdl.registerNewService("DoorbellApp");
	        sdl.addLocation(app.serviceId);
	        app.registerTriggerstoServices(app.serviceId, "onDoorbell",DoorbellApp.class);
	        app.registerActionstoServices(app.serviceId, "onLocationFound",DoorbellApp.class);
	        
	        Message message = new Message(app.serviceId);
	        message.addAction("giveInfo");
	        message.addServiceType("Doorbell");
	        sdl.sendMessage(message);
	    }

	 public void onDoorbell(Object triggerData, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
	    	System.out.println("The properties of the doorbell are :" + (String)triggerData +" SrcServiceID:"+ (String)srcServiceId);
	    	
	        
	    }
	 
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}
