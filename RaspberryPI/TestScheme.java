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
		serviceId2 = sdl.registerNewService("Test2");
		serviceId3 = sdl.registerNewService("Test3");
		sdl.addLocation(serviceId);
		sdl.registerActions(serviceId, "giveInfo", TestScheme.class);
		sdl.registerActions(serviceId, "getInfo", TestScheme.class);
		sdl.registerActions(serviceId2, "giveInfo2", TestScheme.class);
		sdl.registerActions(serviceId2, "getInfo2", TestScheme.class);
		sdl.registerActions(serviceId3, "giveInfo3", TestScheme.class);
		sdl.registerActions(serviceId3, "getInfo3", TestScheme.class);
		
		
        Message message = new Message(serviceId);
        message.addAction("giveInfo2","input1 ");
        message.addServiceType("Test");
        message.addServiceType("Test2");
        message.addServiceType("Test3");
        message.addServiceId(serviceId3);
		sdl.testOnReceiveMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo3","input2 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
		
		sdl.testOnReceiveMessage(message);


		message = new Message(serviceId);
        message.addAction("getInfo","input2 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
		
		sdl.testOnReceiveMessage(message);

	}

	public void giveInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo is called " + (String)actionInput + (String)srcServiceId );
		
	}
	
	public void getInfo(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo is called " + (String)actionInput + (String)srcServiceId);
	}
	
	public void giveInfo2(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo2 is called " + (String)actionInput + (String)srcServiceId );
		
	}
	
	public void getInfo2(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo2 is called " + (String)actionInput + (String)srcServiceId);
	}
	
	public void giveInfo3(Object actionInput, Object srcServiceId) {
		System.out.println("GiveInfo3 is called " + (String)actionInput + (String)srcServiceId );
		
	}
	
	public void getInfo3(Object actionInput, Object srcServiceId) {
		System.out.println("GetInfo3 is called " + (String)actionInput + (String)srcServiceId);
	}
	
}
