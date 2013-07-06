/**
 * This is a brightness property
 * it expects an input of the form 
 * PROPERTYNAME Brightness
 * VALUE		<xyz>
 * @author parth
 *
 */



import java.util.Map;

import servicediscovery.Property;


public class State extends Property {

	String value;
	
	public State(String value) {
		this.value = new String(value);
		name = "State";
	}
	
	@Override
	public boolean match(Map<String, String> properties) {
		String propertyName = properties.get("PROPERTYNAME");
		if( propertyName == null)
			return false;
		if(!propertyName.equals("Brightness"))
			return false;
		if(properties.get("VALUE")==null)
			return false;
		if(properties.get("VALUE").equals(value))
			return true;
		else 
			return false;
	}

	@Override
	public String printProperty() {
		return "PROPERTYNAME-"+name+",VALUE-"+value+",";
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
