import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class Doorbell {
	int countNumOfReplies;
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	 public static void main(String[] args) throws Exception{
	        sdl = new ServiceDiscoveryLayer(true);
	        sdl.registerApp(new Doorbell());
	        serviceId = sdl.registerNewService("Doorbell");
	        sdl.addLocation();
	        sdl.registerActions("giveInfo", Doorbell.class);
	        
	    }

	 public void giveInfo(Object actionInput, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
	        String location = sdl.getProperties().get("Location").toString();
	        String trigger = sdl.getActions().toString();
	        
	        Message message = new Message(serviceId);
	        message.addTrigger("onDoorbell", location+":"+trigger);
	        message.addServiceId((String)srcServiceId);
	    	
	        sdl.sendMessage(message);
	        
	    }
	 
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}