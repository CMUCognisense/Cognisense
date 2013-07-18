
import java.io.Console;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class SpeakerController {
	static String serviceId;
	static String serviceId2;
	static ServiceDiscoveryLayer sdl;
	static int selectToPlay = 0;
	static String chosenSpeaker;
	static String serverIp;
	static String serverPort;
	static List<String> SpeakersFound;
	static int findSpeakers = 0;
	static String mylocation;
	
	public static void main(String[] args) throws Exception{
		SpeakersFound = new LinkedList<String>();
		
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new SpeakerController());
		serviceId = sdl.registerNewService("ConfigService");
		sdl.registerTriggers(serviceId, "printInfo", "getInfo", SpeakerController.class);
		sdl.registerTriggers(serviceId, "selectSong", "listSongs", SpeakerController.class);
		sdl.registerTriggers(serviceId, "serverStarted", "serverStarted", SpeakerController.class);
		sdl.registerTriggers(serviceId, "locationReceived", "getLocation", SpeakerController.class);
		sdl.registerTriggers(serviceId, "changeOfLocation", "LocationChanged", SpeakerController.class);

		// acton of the location service
		serviceId2 = sdl.registerNewService("LocationService");
		sdl.registerActions(serviceId2, "sendLocation", "sendLocation", SpeakerController.class);
		// it also generates one trigger on location changed
		
		
		
		Message message;
		// set the location of the speaker
//		message = new Message(serviceId);
//		message.addServiceType("LED");
//		message.addServiceType("Speaker");
//        message.addAction("getInfo");
//        sdl.sendMessage(message);
//        findSpeakers = 1;
//        Thread.sleep(2000);
//        findSpeakers = 0;
//        
//        
//        Console console = System.console();
//		String speakerId = console.readLine("Select a speaker to set location :" + SpeakersFound.toString());
//		String location = console.readLine("Enter location:");
//		message = new Message(serviceId);
//		message.addServiceId(speakerId);
//        message.addAction("setLocation",location);
//        sdl.sendMessage(message);
//        
//        speakerId = console.readLine("Select a speaker to set location :" + SpeakersFound.toString());
//		location = console.readLine("Enter location:");
//
//		
//		message = new Message(serviceId);
//		message.addServiceId(speakerId);
//        message.addAction("setLocation",location);
//        sdl.sendMessage(message);
        
        message = new Message(serviceId);
        message.addServiceType("LED");
        message.addServiceType("Speaker");
        message.addProperty("Location");
        message.addPropertyValue("Location", "HOME", "home");
        message.addPropertyValue("Location", "USERTAG","window");
        message.addAction("notify");
        sdl.sendMessage(message);
        
		//mylocation = console.readLine("Set your location:");
        
		//playOrStop();
	}	

	public static void playOrStop() {
		Console console = System.console();
		String input = console.readLine("play or stop or chLoc:");
		if(input.equals("play"))
		{
			Message message = new Message(serviceId);
			message.addAction("sendLocation","Parth");
			sdl.sendMessage(message);
		}
		else if(input.equals("stop"))
		{
			Message message = new Message(serviceId);
			message.addServiceType("Speaker");
			message.addAction("stop");
			sdl.sendMessage(message);
			
			message = new Message(serviceId);
			message.addServiceType("MusicServer");
			message.addAction("stopServer", serverPort);
			sdl.sendMessage(message);
			
			playOrStop();
		}
		else if(input.equals("chLoc"))
		{
			// This message s being sent by the location service
			mylocation = console.readLine("Enter New Location:");
			Message message = new Message(serviceId2);
			message.addTrigger("LocationChanged", "parth:"+mylocation);
			sdl.sendMessage(message);
		}
	}
	// trigger listner for the server config 
	public void printInfo(Object actionInput, Object srcServiceId) {
		
		if(selectToPlay == 1)
		{
			System.out.println("Speaker is chosen"+(String)srcServiceId+" now choose the song");
			chosenSpeaker = new String((String)srcServiceId);
			selectToPlay = 0;
			Message msg = new Message(serviceId);
			msg.addAction("getSongs", null);
			sdl.sendMessage(msg);
		}
		if(findSpeakers==1)
		{
			System.out.println("Found Speaker!");
			SpeakersFound.add((String)srcServiceId);
		}
		
	}

	// trigger listner for location changed for parth
	public void changeOfLocation(Object triggerData, Object srcServiceId) {
		System.out.println("Location of parth has changed now");
		String[] data = ((String)triggerData).split(":");
		if(data[0].compareToIgnoreCase("parth") == 0)
		{
			String[] location = data[1].split("\\+");
			Message message = new Message(serviceId);
			message.addServiceId(chosenSpeaker);
			message.addAction("stop");
			sdl.sendMessage(message);
			System.out.println("Stopping Previous Speaker");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Playing at New Speaker");
			message = new Message(serviceId);
			message.addAction("play",serverIp+":"+serverPort);
			message.addServiceType("Speaker");
			message.addProperty("Location");
			message.addPropertyValue("Location", "HOME", location[0]);
			message.addPropertyValue("Location", "FLOOR", location[1]);
			message.addPropertyValue("Location", "ROOM", location[2]);
			message.addPropertyValue("Location", "INROOM", location[3]);
			message.addPropertyValue("Location", "USERTAG", location[4]);
			sdl.sendMessage(message);
		}
	}

	
	// trigger listner for the song selection
	public void selectSong(Object triggerData, Object srcServiceId) {
		
		String[] list = ((String)triggerData).split("&");
		Console console = System.console();
		String input = console.readLine("Select a song from the list\n"+Arrays.toString(list)+"\n>");
		
		Message msg = new Message(serviceId);
		msg.addAction("startServer", input);
		sdl.sendMessage(msg);
		playOrStop();
		
	}
	
	//	trigger listner for server started
	public void serverStarted(Object actionInput, Object srcServiceId) {
		System.out.println("The server was started starting speaker at "+(String)actionInput);
		serverIp = ((String)actionInput).split(":")[0];
		serverPort = ((String)actionInput).split(":")[1];
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
        selectToPlay = 1;
		
	}
	
	// actoin for the location service
	public void sendLocation(Object actionInput, Object srcServiceId) {
		
		if(((String)actionInput).equals("Parth"))
		{
			Message msg = new Message(serviceId2);
			msg.addTrigger("getLocation", mylocation);
			System.out.println("The src service id is " + (String)srcServiceId);
			msg.addServiceId((String)srcServiceId);
			sdl.sendMessage(msg);
		}
	}

}
