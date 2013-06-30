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
	        sdl.addLocation(app.serviceId);
	        app.registerActionstoServices(app.serviceId, "giveInfo", Doorbell.class);
	        
	    }

	 public void giveInfo(Object actionInput, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
	        ServiceDiscoveryLayer sdl = getSdl();
	        String location = sdl.getProperties(serviceId).get("Location").toString();
	        String trigger = sdl.getActions(serviceId).toString();
	        
	        Message message = new Message(serviceId);
	        message.addTrigger("onDoorbell", location+":"+trigger);
	        message.addServiceType((String)srcServiceId);
	    	
	        sdl.sendMessage(message);
	        
	    }
	 
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}