import servicediscovery.Message;
import servicediscovery.ServiceDiscoveryLayer;

public class Doorbell {
	int countNumOfReplies;
	static String serviceId;
	static ServiceDiscoveryLayer sdl;


	public static void main(String[] args) throws Exception {
		sdl = new ServiceDiscoveryLayer(true, false);
		sdl.registerApp(new Doorbell());
		serviceId = sdl.registerNewService("Doorbell");
		sdl.addLocationProperty(serviceId);
		sdl.registerActions(serviceId, "infoMethod", "giveInfo", Doorbell.class);

	}

	public void infoMethod(Object actionInput, Object srcServiceId) {
		// this will print out the message sent by the doorbell.
		String location = sdl.getProperties(serviceId).get("Location")
				.toString();
		String trigger = sdl.getActions(serviceId).toString();

		Message message = new Message(serviceId);
		message.addTrigger("onDoorbell", location + ":" + trigger);
		message.addServiceId((String) srcServiceId);

		sdl.sendMessage(message);

	}

	public void onLocationFound(Object parameters) {
		countNumOfReplies++;
	}

}

