import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;


public class TestScheme {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;
	public static void main(String[] args) throws Exception{
		
		sdl = new ServiceDiscoveryLayer(true);
		sdl.registerApp(new TestScheme());
		serviceId = sdl.registerNewService("Test");
		sdl.addLocation(serviceId);
		sdl.registerActions(serviceId, "giveInfo", TestScheme.class);
		sdl.registerActions(serviceId, "getInfo", TestScheme.class);

        Message message = new Message(serviceId);
        message.addAction("giveInfo");
        message.addServiceType("Test");
		
		sdl.testOnReceiveMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo");
        message.addServiceType("Test");
		
		sdl.testOnReceiveMessage(message);

		
	}

	public void giveInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called");
	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo is called");
	}
	
}
