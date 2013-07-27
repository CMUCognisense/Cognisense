package servicediscovery;
import java.util.Map;

/**
 * The abstract class for properties. Each property in a service has to extend this class. 
 * This is a general class for properties. 
 * each property has a name, a match function and a print property function
 * @author parth
 *
 */
public abstract class Property {

	private String name;
	/**
	 * Every property has to implement a match function. 
	 * This function will take a map of string-string as input. 
	 * The key in this map will be the fields of properties
	 * the values in this map will be the values of the fields in the properties.
	 * the function tells you whether the given input is a match for the property object or not.  
	 * @param properties the map to be given as input
	 * @return the result of the match of the property with the input. 
	 */
	public abstract boolean match(Map<String,String> properties);
	
	/**
	 * this is a function that every property has to implement. This will return a string that 
	 * contains information about the property. The return value will have description of the 
	 * information and the value that is stored in the property object. 
	 * @return the string containing information about the property.
	 */
	public abstract String printProperty();

	/**
	 * getter for the name of the property
	 * @return the property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter for the property name
	 * @param name the name of the property
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
