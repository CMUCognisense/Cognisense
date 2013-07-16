
import java.io.IOException;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class Speaker {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	static int playing = 0;
	static String playerId;

	public static void main(String[] args) throws Exception{

		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new Speaker());
		serviceId = sdl.registerNewService("Speaker");
		sdl.registerActions(serviceId, "setLocation","setLocation", Speaker.class);
		sdl.registerActions(serviceId, "startPlaying","play", Speaker.class);
		sdl.registerActions(serviceId, "stopPlaying","stop", Speaker.class);
		sdl.registerActions(serviceId, "getInfo", "getInfo", Speaker.class);
		sdl.registerActions(serviceId, "notify", "notify", Speaker.class);
		sdl.addProperty(serviceId, new State("Stop"));		
		sdl.addLocationProperty(serviceId);

		//		Message message = new Message(serviceId);
		//		message.addServiceType("Speaker");
		//        message.addAction("setLocation","home+one+bedroom+Top+onfloor");
		//        sdl.sendMessage(message);
		//		

		//		message = new Message(serviceId);
		//        message.addAction("getInfo");
		//        message.addServiceType("Speaker");
		//        message.addProperty("Location");
		//        message.addPropertyValue("Location", "HOME", "home");
		//        message.addPropertyValue("Location", "FLOOR", "one");
		//        message.addPropertyValue("Location", "ROOM", "bedroom");
		//        message.addPropertyValue("Location", "INROOM", "Top");
		//        message.addPropertyValue("Location", "USERTAG", "onfloor");
		//        message.addProperty("State");
		//        message.addPropertyValue("State", "VALUE", "Stop");
		//        sdl.sendMessage(message);
		//
		//        message = new Message(serviceId);
		//        message.addAction("getInfo");
		//        message.addServiceType("Speaker");
		//        message.addProperty("State");
		//        message.addPropertyValue("State", "VALUE", "Stop");
		//        sdl.sendMessage(message);

	}

	public void setLocation(Object actioninput, Object srcServiceId) {
		System.out.println("In Set Location "+(String)actioninput);
		String[] location = ((String)actioninput).split("\\+");

		if(location.length!=5)
		{
			System.out.println("Location input improper not setting location");
			return;
		}

		sdl.addLocationValue(serviceId, location[0], location[1], location[2], location[3], location[4]);
	}


	public void notify(Object actioninput, Object srcServiceId) {
		if(playing == 0) {			
			String[] commands = new String[] {"python","gstScript/doorbell.py"};
			System.out.println("Notifying now ");
			try {
				Process p = Runtime.getRuntime().exec(commands);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startPlaying(Object actioninput, Object srcServiceId) {
		if(playing==0)
		{
			playing = 1;
			playerId = (String)srcServiceId;
			String[] server = ((String)actioninput).split(":");
			// gives the ip and port number seperated by a : sign
			String[] commands = new String[] {"python","gstScript/run.py", server[0],server[1]};
			System.out.println("Playing now ");
			try {
				Process p = Runtime.getRuntime().exec(commands);
			} catch (IOException e) {
				e.printStackTrace();
			}

			sdl.addProperty(serviceId, new State("Playing"));
		}
	}

	public void stopPlaying(Object actioninput, Object srcServiceId) {
		String[] commands = new String[] {"python","gstScript/kill.py"};
		System.out.println("Killing now ");
		if(playerId.equals((String)srcServiceId))
		{
			try {
				Process p = Runtime.getRuntime().exec(commands);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sdl.addProperty(serviceId, new State("Stop"));
			playing = 0;
		}
	}

	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + 
				sdl.getInfo(serviceId));

		Message message = new Message(serviceId);
		message.addTrigger("getInfo", sdl.getInfo(serviceId));
		message.addServiceId((String)srcServiceId);
		sdl.sendMessage(message);
	}


}
