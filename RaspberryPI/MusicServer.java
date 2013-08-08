
import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class MusicServer {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	static HashMap<String,Process> connections;
	static int currentPort;
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new MusicServer());
		connections = new HashMap<String, Process>();
		currentPort = 8554;
		serviceId = sdl.registerNewService("MusicServer");
		// and two triggers one for listSongs other for serverStarted
		sdl.registerActions(serviceId, "getSongs","getSongs", MusicServer.class);
		sdl.registerActions(serviceId, "startServer","startServer", MusicServer.class);
		// takes port number as input
		sdl.registerActions(serviceId, "stopServer","stopServer", MusicServer.class);
		// gives general info
		sdl.registerActions(serviceId, "getInfo", "getInfo", MusicServer.class);
		
		
	}

	
	
	public void getSongs(Object actioninput, Object srcServiceId) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("songs"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder songList = new StringBuilder();
		String sCurrentLine; 
		try {
			while ((sCurrentLine = br.readLine()) != null) {
				songList.append(sCurrentLine);
				songList.append("&");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message msg = new Message(serviceId);
		msg.addServiceId((String)srcServiceId);
		msg.addTrigger("listSongs", songList.toString());
		sdl.sendMessage(msg);
		
	}
	
	public void startServer(Object actionInput, Object srcServiceId) {
		
		String newPortNum = Integer.toString(currentPort++);
		String[] commands = new String[] {"python","vlcScript/start.py", (String)actionInput,newPortNum};
        System.out.println("Playing now on "+newPortNum);
        try {
			Process p = Runtime.getRuntime().exec(commands);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Message msg = new Message(serviceId);
        msg.addServiceId((String)srcServiceId);
        msg.addTrigger("serverStarted", sdl.getIpAddress()+":" + newPortNum);
        sdl.sendMessage(msg);
        
	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + 
				sdl.getInfo(serviceId));
		
		Message message = new Message(serviceId);
        message.addTrigger("getInfo", sdl.getInfo(serviceId));
        message.addServiceId((String)srcServiceId);
        sdl.sendMessage(message);
		
	}
	
	public void stopServer(Object actionInput, Object srcServiceId) {
		System.out.println("Killing server on port "+(String)actionInput);
		if(actionInput != null) {
			String[] commands = new String[] {"python","vlcScript/kill.py", (String)actionInput};
			try {
				Process p = Runtime.getRuntime().exec(commands);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	
}
