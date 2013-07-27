package servicediscovery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;


/**
 * This is the message class. This is used as a wrapper for sending and receiving messages. The message format 
 * need not be known to the user of this class. This abstracts out the exact format of the string that is 
 * sent in the packet by giving a generate message and parse message api.
 * Messages are exchanged between services.  
 * This class has all the following attributes 
 * 1. A list of serviceID's to whom the message is addressed to. It is the unique identification of a particular service that this message is addressed to. This can be empty or null
 * 2. A list of serviceTypes to whom the message is addressed to. this is to identify the type of service this message is addressed to. This can be null or empty
 * 3. Properties that need to matched with the serviceID or serviceTypes that this message is addressed to
 * 4. An action or a trigger.
 * Action is the particular action desired from the service being addressed. This cannot be null in case the message does not have a trigger. 
 * Trigger This is the trigger that a service is sending. This cannot be null unless the actions field is filled. 
 * 5. A source service id to identify who is sending the message. 
 * @author parth
 *
 */

public class Message {
	private List<String> serviceID;
	private List<String> serviceTypes;
	private Map<String,Map<String,String>> properties;
	private String action;
	private String actionInput;
	private String triggerName;
	private String triggerData;
	private String srcServiceID;
	
/**
 * Initialize an empty message with source service ID that is passed in the argument. 
 * @param serviceId this is the source service of the service sending the message. 
 */
	public Message(String serviceId) {
		serviceTypes = new LinkedList<String>();
		properties = new HashMap<String, Map<String,String>>();
		serviceID = new LinkedList<String>();
		srcServiceID = serviceId;
	}
	
	/**
	 * initialize an empty message without specifying the source service ID
	 */
	public Message() {
		serviceID = new LinkedList<String>();
		serviceTypes = new LinkedList<String>();
		properties = new HashMap<String, Map<String,String>>();
	}
	
	/**
	 * this will return a set of all the property names of 
	 * the properties in the particular message
	 * @return the set of property names
	 */
	public Set<String> getProperties() {
		return properties.keySet();
	}
	
	/**
	 * this return the property attributes that directly go as input to the match function for 
	 * a particular property
	 * @param propertyName
	 * @return the map of the property field and value
	 */
	public Map<String,String> getPropertyAttributes(String propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * add a property to the propertyName
	 * @param propertyName
	 */
	public void addProperty(String propertyName) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("PROPERTYNAME", propertyName);
		properties.put(propertyName, map);
	}
	
	/**
	 * adds the property value for the specified the propertyName
	 * @param propertyName The name of the property given in property name eg. 'Location'
	 * @param propertyField the field og the property. eg. 'HOME'
	 * @param propertyValue the value of the field of the property eg. 'parthshome'
	 */
	public void addPropertyValue(String propertyName, String propertyField, String propertyValue) {
		
		HashMap<String,String> propertyValues = (HashMap<String, String>) properties.get(propertyName);
		if(propertyValues == null)
			throw new IllegalArgumentException("No such property");
		
		propertyValues.put(propertyField, propertyValue);
		
	}
	
	/**
	 * add service type to the message
	 * @param serviceType service type to add in the message
	 */
	public void addServiceType(String serviceType) {
		serviceTypes.add(serviceType);
	}
	
	/**
	 * add action to the message specifying the action tag. Action is what the action is identified by.
	 * @param action the action tag of the action
	 */
	public void addAction(String action) {
		this.action = action;
	}
	
	/**
	 * add the action and action input to the message. 
	 * @param action the action tag
	 * @param actionInput the action input to the action. 
	 */
	public void addAction(String action, String actionInput) {
		this.action = action;
		this.actionInput = actionInput;
	}
	
	/**
	 * the trigger to add to the message. This is what the trigger is identified by.
	 * @param triggerName this is the trigger name. 
	 */
	public void addTrigger(String triggerName) {
		this.triggerName = triggerName;
	}
	
	/**
	 * the trigger as well as trigger data to add to the message.
	 * @param triggerName the trigger name
	 * @param triggerData the data that is sent with the trigger. 
	 */
	public void addTrigger(String triggerName, String triggerData) {
		this.triggerName = triggerName;
		this.triggerData = triggerData;
	}

	/**
	 * returns the list of serviceids that are in the message. 
	 * @return the list of serviceids. 
	 */
	public List<String> getServiceIds() {
		return serviceID;
	}

	/**
	 * add a serviceid to the message
	 * @param serviceID the service id to add to the message
	 */
	public void addServiceId(String serviceID) {
		this.serviceID.add(serviceID);
	}

	public List<String> getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(List<String> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	/**
	 * get the source service id
	 * @return source service id 
	 */
	public String getSrcServiceID() {
		return srcServiceID;
	}

	/**
	 * set the source service id
	 * @param srcServiceID the source service id to set 
	 */
	public void setSrcServiceID(String srcServiceID) {
		this.srcServiceID = srcServiceID;
	}

	/**
	 * gets the action name
	 * @return the action name
	 */
	public String getAction() {
		return action;
	}

	/**
	 * returns the action input in the message
	 * @return the action input in the message
	 */
	public String getActionInput() {
		return actionInput;
	}

	/**
	 * returns the trigger name in the message. 
	 * @return trigger name
	 */
	public String getTriggerName() {
		return triggerName;
	}

	/**
	 * return the trigger data in the message
	 * @return trigger data
	 */
	public String getTriggerData() {
		return triggerData;
	}
	
	/**
	 * This method parses the message object and generates the message to be put on the wire
	 * This method also checks if the message is valid for generation or not
	 * it will throw an exception is the message is not valid i.e. does not have a trigger 
	 * or an action or has both. 
	 * @return the generated message
	 */
	public String generateMessage() {
		if(action==null && triggerName==null || action !=null && triggerName!=null)
			return null;

		if(srcServiceID==null)
			return null;
		
		if(serviceID.size() == 0 && serviceTypes.size() == 0 && properties.size() > 0)
			return null;
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}
	/**
	 *  This static method is used to parse a given message from the reliable multicast layer and 
	 * convert it to a Message object it is a complement of the generateMessage mesage which 
	 * does the exact opposite. The message from the multicast layer is in the json format. 
	 * @param json the json string to be passed 
	 * @return it returns an object of the Message class. 
	 */
	public static Message parseMessage(String json) {
		Gson gson = new Gson();
		Message message = gson.fromJson(json,Message.class);
		return message;
	}
	
/**
 * main method for unit testing
 * @param args
 */
	public static void main(String[] args) {
		Message message = new Message("123");
		message.addServiceId("12-32-32-12-asd");
		message.addServiceType("Speaker");
		message.addAction("startlistening");
		
		message.addProperty("Volume");
		message.addPropertyValue("Volume", "level", "120");
		
		message.addProperty("Location");
		message.addPropertyValue("Location", "room", "bedroom");
		message.addPropertyValue("Location", "usertag", "nearwindow");
		
		System.out.println(message.generateMessage());
			
		
	}
	
}

