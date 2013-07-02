
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
		sdl.testOnReceiveMessage(message);

		message = new Message(serviceId);
        message.addAction("getInfo","input2 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
		message.addProperty(Brightness.name);
		message.addPropertyValue(Brightness.name, "VALUE", "20");
		//System.out.println(message.generateMessage());
		sdl.testOnReceiveMessage(message);


		message = new Message(serviceId);
        message.addAction("getInfo","input3 ");
        message.addServiceType("Test");
        message.addServiceId(serviceId3);
		
		sdl.testOnReceiveMessage(message);


		message = new Message(serviceId);
        message.addAction("giveInfo","input1 ");
        message.addServiceType("Test");
        message.addServiceType("Test2");
        message.addServiceType("Test3");
        message.addServiceId(serviceId3);
        message.addProperty(Brightness.name);
		message.addPropertyValue(Brightness.name, "VALUE", "30");
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
