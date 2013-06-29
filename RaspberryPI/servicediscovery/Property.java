package servicediscovery;

import java.util.Map;

public abstract class Property {

	public String name;

	public abstract boolean match(Map<String, String> properties);

}
