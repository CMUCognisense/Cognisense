package servicediscovery;

import java.util.LinkedList;
import java.util.List;

/**
 * Message Format of service Discovery Layer:
<ServiceID> <ServiceType> <Property> <Actions>OR<Trigger> <Source ServiceID>

<ServiceID> is the unique identification of a particular service that this message is addressed to. This can be empty or null

<ServiceType> this is to identify the type of service this message is addressed to. This can be null or empty

<Property> This is the property to match for the service to which this is addressed to. This can be null or empty

<Actions> This is the particular action desired from the service being addressed. This cannot be null in case the message does not have a trigger. 

<Trigger> This is the trigger that a service is sending. This cannot be null unless the actions field is filled. 

<Source ServiceID> this is the source service id of the service that generated this message and can be used to address the service at a later time. 
 * @author parth
 *
 */

public class Message {
	private String serviceID;
	private List<String> serviceType;
	private List<String> property;
	private String action;
	private String trigger;
	private String srcServiceID;
	
	
	public Message(Service service) {
		serviceType = new LinkedList<String>();
		property = new LinkedList<String>();
		srcServiceID = service.getServiceid();
	}
	
	/**
	 * this method generated a message from the given message object that is to be sent
	 * by the service discovery layer. The service discovery object calls this method
	 * @return
	 */
	public String generateMessage() {
		
		
		
		return null;
	}
	
}

