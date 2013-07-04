package servicediscovery;
import java.util.Map;


public abstract class Property {

	public static String name;
	
	public abstract boolean match(Map<String,String> properties);
	
	public abstract String printProperty();
	
}
