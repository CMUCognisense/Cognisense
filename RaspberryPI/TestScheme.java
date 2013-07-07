
import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class TestScheme {
	static String serviceId;
	static String serviceId2;
	static String serviceId3;
	static ServiceDiscoveryLayer sdl;
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new TestScheme());
		serviceId = sdl.registerNewService("Test");
		serviceId2 = sdl.registerNewService("Test2");
		serviceId3 = sdl.registerNewService("Test3");
		sdl.addLocationProperty(serviceId);
		sdl.addLocationValue(serviceId, "MyHome", "one", "Bedroom", null, "nearWindow");
		sdl.registerActions(serviceId, "giveInfo","giveInfo", TestScheme.class);
		sdl.registerActions(serviceId, "getInfo","getInfo", TestScheme.class);
		sdl.registerActions(serviceId2, "giveInfo2","giveInfo", TestScheme.class);
		sdl.registerActions(serviceId2, "getInfo2","getInfo", TestScheme.class);
		sdl.registerActions(serviceId3, "giveInfo3","giveInfo", TestScheme.class);
		sdl.registerActions(serviceId3, "getInfo3","getInfo", TestScheme.class);
		
		sdl.addProperty(serviceId, new Brightness(10));
		sdl.addProperty(serviceId2, new Brightness(20));
		sdl.addProperty(serviceId3, new Brightness(30));
		
        Message message = new Message(serviceId);
        message.addAction("giveInfo","input1 ");
        message.addServiceType("Test");
        message.addServiceType("Test2");
        message.addServiceType("Test3");
        message.addServiceId(serviceId3);
        sdl.sendMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo","input2 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
		message.addProperty("Brightness");
		message.addPropertyValue("Brightness", "VALUE", "10");
        sdl.sendMessage(message);


		message = new Message(serviceId);
        message.addAction("getInfo","input3 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
        sdl.sendMessage(message);


		message = new Message(serviceId);
        message.addAction("giveInfo","input4 ");
        message.addServiceType("Test");
        message.addServiceType("Test2");
        message.addServiceType("Test3");
        message.addServiceId(serviceId3);
        message.addProperty("Brightness");
        sdl.sendMessage(message);

        message = new Message(serviceId);
        message.addAction("giveInfo","input5 ");
        message.addServiceType("Test");
        message.addServiceType("Test2");
        message.addServiceType("Test3");
        message.addServiceId(serviceId3);
        message.addProperty("Brightness");
		message.addPropertyValue("Brightness", "VALUE", "10");
		message.addProperty("Location");
		message.addPropertyValue("Location", "HOME", "MyHome");
		message.addPropertyValue("Location", "FLOOR", "one");
		message.addPropertyValue("Location", "ROOM", "Bedroom");
        sdl.sendMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo","input6 ");
        sdl.sendMessage(message);

		message = new Message(serviceId);
        message.addAction("giveInfo","input7 ");
        message.addServiceType("Test66");
        message.addServiceType("Test77");
        message.addServiceType("Test5");
        sdl.sendMessage(message);
        
	}

	public void giveInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + sdl.getInfo(serviceId));
		
	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo is called " + (String)actionInput + (String)srcServiceId);
	}
	
	public void giveInfo2(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo2 is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + sdl.getInfo(serviceId2));
		
	}
	
	public void getInfo2(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo2 is called " + (String)actionInput + (String)srcServiceId);
	}
	
	public void giveInfo3(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo3 is called " + (String)actionInput + (String)srcServiceId +" Info is\n" + sdl.getInfo(serviceId3) );
		
	}
	
	public void getInfo3(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo3 is called " + (String)actionInput + (String)srcServiceId);
	}
	
}
