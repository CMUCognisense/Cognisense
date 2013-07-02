
import java.io.ObjectInputStream.GetField;

import servicediscovery.Application;
import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class DoorbellApp extends Application{
	int countNumOfReplies;
	String serviceId;
	static ServiceDiscoveryLayer sdl;
	static boolean debug = true;

	public DoorbellApp(){

	}

	public static void main(String[] args) throws Exception{

		//sdl.addLocation(app.serviceId);
		DoorbellApp app = null;
		app = new DoorbellApp();
		sdl = app.getSdl();
		app.serviceId = sdl.registerNewService("DoorbellApp");
		app.registerTriggerstoServices(app.serviceId, "onDoorbell",DoorbellApp.class);
		app.registerTriggerstoServices(app.serviceId, "checkLocation",DoorbellApp.class); 
		/*Message message = new Message(app.serviceId);
		message.addAction("giveInfo");
		message.addServiceType("Doorbell");
		sdl.sendMessage(message);*/
		testLocation(app.serviceId,sdl);
		testLocation1(app.serviceId,sdl);
	}

	public  void checkLocation(Object triggerData, Object srcServiceId) { 
		if(triggerData.equals("notset")){
			if(debug)
				System.out.println("***************** location is not at set ***********");
			Message message = new Message(serviceId);
			/* this is basicaly were we have to show up a UO for the user for getting the 
			 * location
			 */
			/* also the location which are not set by the user has to be included
			 * in the string with a value of "notset". Because sending null with  a 
			 * delimiter and then to parse that string doesnt make sense
			 * eg : "chidduhome+null+null+window" here it is
			 * hard to differentiate for which sublocation there is a null value
			 */
			if(debug)
				System.out.println("***************** Setting the location from DoorBellAPP ***********");
			message.addAction("setLocation","chidduhome+floor1+bedroom+bottom+nearwindow");
			message.addServiceType("Doorbell");
			sdl.sendMessage(message);			
		}
		System.out.println("The present DoorBell Location is "+ (String)triggerData);
	}

	public void onDoorbell(Object triggerData, Object srcServiceId) {
		// this will print out the message sent by the doorbell.
		System.out.println("The properties of the doorbell are :" + (String)triggerData +" SrcServiceID:"+ (String)srcServiceId);
	}	 


	public static void testLocation(String serviceId, ServiceDiscoveryLayer sdl) {
		Message message = new Message(serviceId);
		message.addAction("provideLocation");
		message.addServiceType("Doorbell");
		sdl.sendMessage(message);
		if(debug)
			System.out.println("*************Asking for provide location*************");
	}
	
	public static void testLocation1(String serviceId, ServiceDiscoveryLayer sdl) {
		Message message = new Message(serviceId);
		message.addAction("provideLocation");
		message.addServiceType("Doorbell");
		sdl.sendMessage(message);
		if(debug)
			System.out.println("*************Asking for provide location 2nd test*************");
	}
}
