
import java.io.Console;
import java.util.Arrays;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class SpeakerController {
	static String serviceId;
	static String serviceId2;
	static ServiceDiscoveryLayer sdl;
	static int getInfo = 0;
	static String chosenSpeaker;
	
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new SpeakerController());
		serviceId = sdl.registerNewService("ConfigService");
		sdl.registerTriggers(serviceId, "printInfo", "getInfo", SpeakerController.class);
		sdl.registerTriggers(serviceId, "selectSong", "listSongs", SpeakerController.class);
		sdl.registerTriggers(serviceId, "serverStarted", "serverStarted", SpeakerController.class);
		sdl.registerTriggers(serviceId, "locationReceived", "getLocation", SpeakerController.class);
		
		// acton of the location service
		serviceId2 = sdl.registerNewService("LocationService");
		sdl.registerActions(serviceId2, "sendLocation", "sendLocation", SpeakerController.class);
		// it also generates one trigger on location changed
		
		Message message;
		
		// set the location of the speaker
		message = new Message(serviceId);
		message.addServiceType("Speaker");
        message.addAction("setLocation","home+one+bedroom+Top+onfloor");
        sdl.sendMessage(message);
        
		Console console = System.console();
		String input = console.readLine("Play or stop:");
			
		message = new Message(serviceId);
        message.addAction("sendLocation","Parth");
        sdl.sendMessage(message);
        
	}	

	// trigger listner for the server config 
	public void printInfo(Object actionInput, Object srcServiceId) {
		System.out.println("Speaker is chosen now choose the song");
		if(getInfo == 0)
		{
			chosenSpeaker = new String((String)srcServiceId);
			getInfo++;
			Message msg = new Message(serviceId);
			msg.addAction("getSongs", null);
			sdl.sendMessage(msg);
		}
		
	}
	
	// trigger listner for the speaker config
	public void selectSong(Object triggerData, Object srcServiceId) {
		
		String[] list = ((String)triggerData).split("&");
		Console console = System.console();
		String input = console.readLine("Select a song from the list\n"+Arrays.toString(list)+"\n>");
		
		Message msg = new Message(serviceId);
		msg.addAction("startServer", "Roobaroo_RANG_DE_BASANTI.mp3");
		sdl.sendMessage(msg);
	}
	
	//	trigger listner for speaker config
	public void serverStarted(Object actionInput, Object srcServiceId) {
		System.out.println("The server was started starting speaker at "+(String)actionInput);
		Message message = new Message(serviceId);
		message.addServiceId(chosenSpeaker);
        message.addAction("play",(String)actionInput);
        sdl.sendMessage(message);
		
	}
	
	public void locationReceived(Object actionInput, Object srcServiceId) {
		System.out.println("Location received");
		String[] location = ((String)actionInput).split("\\+");
		
		Message message = new Message(serviceId);
        message.addAction("getInfo");
        message.addServiceType("Speaker");
        message.addProperty("Location");
        message.addPropertyValue("Location", "HOME", location[0]);
        message.addPropertyValue("Location", "FLOOR", location[1]);
        message.addPropertyValue("Location", "ROOM", location[2]);
        message.addPropertyValue("Location", "INROOM", location[3]);
        message.addPropertyValue("Location", "USERTAG", location[4]);
        sdl.sendMessage(message);
        getInfo = 0;
		
	}
	
	// actoin for the location service
	public void sendLocation(Object actionInput, Object srcServiceId) {
		
		if(((String)actionInput).equals("Parth"))
		{
			Message msg = new Message(serviceId2);
			msg.addTrigger("getLocation", "home+one+bedroom+Top+onfloor");
			System.out.println("The src service id is " + (String)srcServiceId);
			msg.addServiceId((String)srcServiceId);
			sdl.sendMessage(msg);
		}
	}

}
