/**
 * This is a brightness property
 * it expects an input of the form 
 * PROPERTYNAME Brightness
 * status		<xyz>
 * @author Jiahan Wang
 *
 */

import java.util.Map;

import servicediscovery.Property;

public class LEDStatus extends Property {

	String status = "";

	public LEDStatus(String status) {
		this.status = status;
		// setName("LEDStatus");
	}

	@Override
	public boolean match(Map<String, String> properties) {
		String propertyName = properties.get("PROPERTYNAME");
		if (propertyName == null)
			return false;
		if (!propertyName.equals("LEDStatus"))
			return false;
		if (properties.get("LEDSTATUS") == null)
			return false;
		if (properties.get("LEDSTATUS").equals(status))
			return true;
		else
			return false;
	}

	@Override
	// public String printProperty() {
	// return "PROPERTYNAME-" + getName() + ",status-" + status + ",";
	// }
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
