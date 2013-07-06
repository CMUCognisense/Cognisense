import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class Doorbell {
	int countNumOfReplies;
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	 public static void main(String[] args) throws Exception{
	        sdl = new ServiceDiscoveryLayer(false,false);
	        sdl.registerApp(new Doorbell());
	        serviceId = sdl.registerNewService("Doorbell");
	        sdl.addLocationProperty(serviceId);
	        sdl.registerActions(serviceId, "infoMethod","giveInfo", Doorbell.class);
	        sdl.registerActions(serviceId, "getLocMethod","getLocation",Doorbell.class);
	       	sdl.registerActions(serviceId, "setLocMethod","setLocation",Doorbell.class);
	    }

	 public void infoMethod(Object actionInput, Object srcServiceId) {
	        // this will print out the message sent by the doorbell.
		 	System.out.println("*********in the info method************");
	        String location = sdl.getProperties(serviceId).get("Location").toString();
	        String trigger = sdl.getActions(serviceId).toString();
	        
	        Message message = new Message(serviceId);
	        message.addTrigger("onDoorbell", location+":"+trigger);
	        message.addServiceId((String)srcServiceId);
	    	System.out.println("****************before the send message to the application**************"+"location = "+ location +"Trigger"+trigger);
	        sdl.sendMessage(message);
	        
	    }
	 
	 public void getLocMethod(Object actionInput, Object srcServiceId){
		 
		 System.out.println("**********entered get Location*********");
		 String location = sdl.getProperties(serviceId).get("Location").printProperty();
		 System.out.println("*********Present Location of doorbell:"+location+"****************");
		 Message message = new Message(serviceId);
		 	if(location.contains("HOME")){
		 		message.addTrigger("recLoc",location);
		 	}else{
		 		message.addTrigger("recLoc","notset");
		 	}	 	
		 	message.addServiceId((String)srcServiceId);
		 	sdl.sendMessage(message);
	 }
	 
	 public void setLocMethod(Object actionInput, Object srcServiceId){
		 System.out.println("*************In set Location************" + (String)actionInput);
		 String loc = (String)actionInput;
		 String []location = loc.split("\\+");
		 String home,floor,room,inRoom,usertag;
		 
		 if("notset".equals(location[0]))
			 home = null;
		 else
			 home = location[0];
		 
		 if("notset".equals(location[1]))
			 floor = null;
		 else
			 floor = location[1];
		 
		 if("notset".equals(location[2]))
			 room = null;
		 else
			 room = location[2];
		 
		 if("notset".equals(location[3]))
			 inRoom = null;
		 else
			 inRoom = location[3];
		 
		 if("notset".equals(location[4]))
			 usertag = null;
		 else
			 usertag = location[4];
		 
		 System.out.println("******************location that is set **************"+home+floor+room+inRoom+usertag);
		 sdl.addLocationValue(serviceId,home,floor,room,inRoom,usertag);
	 }
	 
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}
