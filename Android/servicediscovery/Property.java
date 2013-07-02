package servicediscovery;
import java.io.Serializable;
import java.util.Map;


public abstract class Property implements Serializable{
	private static final long serialVersionUID = 1L;
	public String name;
	
	public abstract boolean match(Map<String,String> properties);
	
}
