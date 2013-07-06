import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;

public class LEDConfigurationService {
	static String serviceId;
	static ServiceDiscoveryLayer sdl;

	/**
	 * @param args
	 */

	public static void main(String[] args) throws Exception {
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new LEDConfigurationService());
		serviceId = sdl.registerNewService("LEDConfigurationService");
		sdl.addLocationProperty(serviceId);

		Message message = new Message(serviceId);
		message.addAction("TURNON");
		message.addServiceType("LED");
		sdl.sendMessage(message);
	}

}
