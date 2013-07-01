import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class TestScheme {
	static String serviceId;
	static String serviceId2;
	static String serviceId3;
	static ServiceDiscoveryLayer sdl;
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(new TestScheme());
		serviceId = sdl.registerNewService("Test");
		serviceId2 = sdl.registerNewService("Test1");
		serviceId3 = sdl.registerNewService("Test3");
		sdl.addLocation(serviceId);
		sdl.registerActions(serviceId, "giveInfo", TestScheme.class);
		sdl.registerActions(serviceId, "getInfo", TestScheme.class);
		sdl.registerActions(serviceId2, "giveInfo", TestScheme.class);
		sdl.registerActions(serviceId2, "getInfo", TestScheme.class);
		sdl.registerActions(serviceId3, "giveInfo", TestScheme.class);
		sdl.registerActions(serviceId3, "getInfo", TestScheme.class);
		
		
        Message message = new Message(serviceId);
        message.addAction("giveInfo","input");
        message.addServiceType("Test");
		
		sdl.testOnReceiveMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo","input");
        message.addServiceType("Test");
		
		sdl.testOnReceiveMessage(message);

		
	}

	public void giveInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId );
	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo is called " + (String)actionInput + (String)srcServiceId);
	}
	
}
