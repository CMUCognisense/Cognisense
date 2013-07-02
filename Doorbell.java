import java.util.HashMap;
import java.util.Map;

import servicediscovery.Application;
import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class Doorbell extends Application{
	int countNumOfReplies;
	String serviceId;
	
	 public static void main(String[] args) throws Exception{
	        Doorbell app = new Doorbell();
	        ServiceDiscoveryLayer sdl = app.getSdl();
	        app.serviceId = sdl.registerNewService("Doorbell");
	        app.registerActionstoServices(app.serviceId, "giveInfo", Doorbell.class);
	        app.registerActionstoServices(app.serviceId, "provideLocation",Doorbell.class);
	        app.registerActionstoServices(app.serviceId, "setLocation",Doorbell.class);
	    }

	 public void giveInfo(Object actionInput, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
	        ServiceDiscoveryLayer sdl = getSdl();
	        String location = sdl.getProperties(serviceId).get("Location").toString();
	        String trigger = sdl.getActions(serviceId).toString();       
	        Message message = new Message(serviceId);
	        message.addTrigger("onDoorbell", location+":"+trigger);
	        message.addServiceId((String)srcServiceId);
	    	
	        sdl.sendMessage(message);   
	    }
	 
	 public void provideLocation(Object actionInput, Object srcServiceId){
		 
		 ServiceDiscoveryLayer sdl = getSdl();
		 String location = sdl.getProperties(serviceId).get("Location").toString();
		 if(location==null){
			 System.out.println("****************location is not set in doorbell************");
			 location = "notset";
		 }
		 Message message = new Message(serviceId);
	     message.addTrigger("checkLocation",location);
	     message.addServiceId((String)srcServiceId);
	     System.out.println("**************sending message to set the location for this doorbell**********");
	     sdl.sendMessage(message);   
	 }
	 
	 public void setLocation(Object actionInput,Object srcServiceId){
			if(serviceId!=null){
				System.out.println("***************received messge to set the location as per ***********" + (String)actionInput);
				ServiceDiscoveryLayer sdl = getSdl();
				sdl.addLocation(serviceId,actionInput);	
			}
		}
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}