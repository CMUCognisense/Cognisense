package servicediscovery;
import java.lang.reflect.Method;

/**
 * Defines a trigger. the trigger is trigger that a service is listening on.  
 * The trigger name is a string by which the trigger is known to other services.
 * The method is the function that can be called when that trigger is received. 
 * the trigger stores a trigger name and a method to be called in case the trigger 
 * with the trigger name specified is received by a service.   
 * @author parth
 *
 */
public class Trigger {

	Method method;
	String triggerTag;

	/**
	 * Constructor of the trigger. The name of the trigger ie the triggerTag does not have to be same as the name of the method. 
	 * The method can be named whatever but the triggerTag is the one that is important when referring to this 
	 * trigger from another service which you are listening on. 
	 * @param method this is the method of any class that can be stored here. This if of type Method from javas reflection api
	 * @param triggerTag this is the name by which other service generate this trigger. 
	 */
	public Trigger(Method method, String triggerTag) {
		this.method = method;
		this.triggerTag = triggerTag;
	}

	/**
	 * getter method
	 * @return the method associated with the object
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * the setter method. 
	 * @param method
	 */
	public void setMethod(Method method) {
		this.method = method;
	}
	
	/**
	 * getter method for triggerTag which is a name of the trigger. 
	 * @return the trigger tag which is a string
	 */
	public String getTriggerTag() {
		return triggerTag;
	}
	
	/**
	 * sets the trigger tag
	 * @param actionTag the trigger name to set.
	 */
	public void setTriggerTag(String actionTag) {
		this.triggerTag = actionTag;
	}
}
