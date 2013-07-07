
import java.io.ObjectInputStream.GetField;

import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class DoorbellApp {
	int countNumOfReplies;
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	 public static void main(String[] args) throws Exception{
		 	sdl = new ServiceDiscoveryLayer(false,false);
		 	sdl.registerApp(new DoorbellApp());
		 	serviceId = sdl.registerNewService("DoorbellApp");
	        sdl.addLocationProperty(serviceId);
	        sdl.registerTriggers(serviceId, "doorbellTriggerMethod","onDoorbell", DoorbellApp.class);
	     	sdl.registerTriggers(serviceId, "doorbellRecvMethod","recLoc",DoorbellApp.class);
	        test();
	        /*Message message = new Message(serviceId);
	        message.addAction("giveInfo");
	        message.addServiceType("Doorbell");
	        sdl.sendMessage(message);
			*/
	    }

	 public void doorbellTriggerMethod(Object triggerData, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
		 	System.out.println("****************in the door bell triggermethod*************");
	    	System.out.println("The properties of the doorbell are :" + (String)triggerData +" SrcServiceID:"+ (String)srcServiceId);
	    	
	        
	    }	
	 
	 public void doorbellRecvMethod(Object triggerData, Object srcServiceId){		 
		 System.out.println(" The doorBell location is " + (String)triggerData); 
		 String location = (String)triggerData;
		 if("notset".equals(location)){
		 System.out.println("****************Location is not set****************");
		 Message message = new Message(serviceId);
		 message.addAction("setLocation","chidduhome+floor1+kitchen+notset+notset");
		 message.addServiceType("Doorbell");
		 sdl.sendMessage(message);
		 }else
			 System.out.println("*************Location is set to "+location+"***************");
		 
		 Message message = new Message(serviceId);
			message.addAction("getLocation");
	        message.addServiceType("Doorbell");
	        sdl.sendMessage(message);
	 }
	 
	 public static void test(){
		  Message message = new Message(serviceId);
	        message.addAction("getLocation");
	        message.addServiceType("Doorbell");
	       	/*String location = "chidduHome+notset+kitchen+myroom+notset";
	        String[] newLoc = location.split("\\+");
	        System.out.println("home = " + newLoc[0]);
	        System.out.println("floor = " + newLoc[1]);			
	       if("notset".equals(newLoc[1])){
	    	   newLoc[1]= null;
	        	System.out.println("not set validated "+ newLoc[1]+"******");
	       }*/
	        sdl.sendMessage(message);      
	 }
	 
	 public static void test1(){
		Message message = new Message(serviceId);
		message.addAction("getLocation");
        message.addServiceType("Doorbell");
        sdl.sendMessage(message);
	 }
}
