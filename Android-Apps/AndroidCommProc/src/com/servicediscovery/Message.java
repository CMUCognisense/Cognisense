package com.servicediscovery;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;


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

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> serviceID;
	private List<String> serviceTypes;
	private Map<String,Map<String,String>> properties;
	private String action;
	private String actionInput;
	private String triggerName;
	private String triggerData;
	private String srcServiceID;
	
	
	public Message(String serviceId) {
		serviceTypes = new LinkedList<String>();
		properties = new HashMap<String, Map<String,String>>();
		serviceID = new LinkedList<String>();
		srcServiceID = serviceId;
	}
	
	public Message() {
		serviceID = new LinkedList<String>();
		serviceTypes = new LinkedList<String>();
		properties = new HashMap<String, Map<String,String>>();
	}
	
	/**
	 * this will return a set of all the property names of 
	 * the properties in the particular message
	 * @return
	 */
	public Set<String> getProperties() {
		return properties.keySet();
	}
	
	/**
	 * this return the property attributes that directly go as input to the match function for 
	 * a particular property
	 * @param propertyName
	 * @return
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
	 * @param propertyName
	 * @param propertyField
	 * @param propertyValue
	 */
	public void addPropertyValue(String propertyName, String propertyField, String propertyValue) {
		
		HashMap<String,String> propertyValues = (HashMap<String, String>) properties.get(propertyName);
		if(propertyValues == null)
			throw new IllegalArgumentException("No such property");
		
		propertyValues.put(propertyField, propertyValue);
		
	}
	
	public void addServiceType(String serviceType) {
		serviceTypes.add(serviceType);
	}
	
	public void addAction(String action) {
		this.action = action;
	}
	
	public void addAction(String action, String actionInput) {
		this.action = action;
		this.actionInput = actionInput;
	}
	
	public void addTrigger(String triggerName) {
		this.triggerName = triggerName;
	}
	
	public void addTrigger(String triggerName, String triggerData) {
		this.triggerName = triggerName;
		this.triggerData = triggerData;
	}

	public List<String> getServiceIds() {
		return serviceID;
	}

	public void addServiceId(String serviceID) {
		this.serviceID.add(serviceID);
	}

	public List<String> getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(List<String> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public String getSrcServiceID() {
		return srcServiceID;
	}

	public void setSrcServiceID(String srcServiceID) {
		this.srcServiceID = srcServiceID;
	}

	public String getAction() {
		return action;
	}

	public String getActionInput() {
		return actionInput;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public String getTriggerData() {
		return triggerData;
	}
	
	/**
	 * This method parses the message object and generates the message to be put on the wire
	 * This method also checks if the message is valid for generation or not
	 * it will throw an exception is the message is not valid i.e. does not have a trigger 
	 * or an action or has both. 
	 * @return
	 */
	public String generateMessage() {
		if(action==null && triggerName==null || action !=null && triggerName!=null)
			return null;

		if(srcServiceID==null)
			return null;
		
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}
	/**
	 *  This method is used to parse a given message from the reliable multicast layer and 
	 * convert it to a Message object it is a complement of the generateMessage mesage which 
	 * does the exact opposite
	 * @param json
	 * @return
	 */
	public static Message parseMessage(String json) {
		Gson gson = new Gson();
		Message message = gson.fromJson(json,Message.class);
		return message;
	}
	

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

