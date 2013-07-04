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


public class Brightness extends Property {

	int value = 0;
	
	public Brightness(int value) {
		this.value = value;
		name = "Brightness";
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
		if(properties.get("VALUE").equals(Integer.toString(value)))
			return true;
		else 
			return false;
	}

	@Override
	public String printProperty() {
		return "PROPERTYNAME-"+name+",VALUE-"+value+",";
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
