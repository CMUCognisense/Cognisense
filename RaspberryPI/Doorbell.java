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
		app.registerActionstoServices(app.serviceId, "setLocation",Doorbell.class);

	}

	public void giveInfo(Object parameter) {
		ServiceDiscoveryLayer sdl = getSdl();
		String location = sdl.getProperties(serviceId).get("Location").toString();
		String actionnames = sdl.getActions(serviceId).toString();
		String triggerName = "onDoorbell";
		Message message = new Message(serviceId);
		message.addTrigger(triggerName, location+":"+actionnames);
		message.addServiceType((String)parameter);
		sdl.sendMessage(message);
	}

	public void setLocation(){
		if(serviceId!=null){
			Map<String,String> location= new HashMap<String,String>();
			ServiceDiscoveryLayer sdl = getSdl();
			sdl.getProperties(serviceId);
			Message message = new Message(serviceId);
			location  = message.getPropertyAttributes("Location");
			sdl.addLocation(serviceId,location);
			
		}
	}

	public void onLocationFound(Object parameters) {
		countNumOfReplies++;
	}

}