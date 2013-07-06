
import java.io.IOException;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class Speaker {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new Speaker());
		serviceId = sdl.registerNewService("Speaker");
		sdl.addLocationProperty(serviceId);
		sdl.registerActions(serviceId, "setLocation","setLocation", Speaker.class);
		sdl.registerActions(serviceId, "startPlaying","play", Speaker.class);
		sdl.registerActions(serviceId, "stopPlaying","stop", Speaker.class);
		sdl.registerActions(serviceId, "getInfo", "getInfo", Speaker.class);
		sdl.addProperty(serviceId, new State("Stop"));
		
	}

	
	public void setLocation(Object actioninput, Object srcServiceId) {
		String[] location = ((String)actioninput).split("\\+");
		sdl.addLocationValue(serviceId, location[0], location[1], location[2], location[3], location[4]);
	}
	
	public void startPlaying(Object actioninput, Object srcServiceId) {
		String[] server = ((String)actioninput).split(":");
		// gives the ip and port number seperated by a : sign
		String[] commands = new String[] {"python","gstScript/run.py", server[0],server[1]};
        System.out.println("Playing now ");
        try {
			Process p = Runtime.getRuntime().exec(commands);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void stopPlaying(Object actioninput, Object srcServiceId) {
		String[] commands = new String[] {"python","gstScript/kill.py"};
        System.out.println("Killing now ");
        try {
			Process p = Runtime.getRuntime().exec(commands);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + 
				sdl.getProperties(serviceId).get("Location").printProperty());
		
		Message message = new Message(serviceId);
        message.addTrigger("getInfo", sdl.getProperties(serviceId).get("Location").printProperty());
        message.addServiceId((String)srcServiceId);
        sdl.sendMessage(message);
		
	}
	
	
}
