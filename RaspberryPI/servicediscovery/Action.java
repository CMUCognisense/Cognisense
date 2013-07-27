package servicediscovery;
import java.lang.reflect.Method;

/**
 * Defines an Action. An action is a method along with an actionTag. 
 * The actionTag is a string by which the method is known to other services.
 * The method is the function that can be called later when needed. 
 * The action stores a method and a name of the method by which it can be referred to.  
 * @author parth
 *
 */
public class Action {

	private Method method;
	private String actionTag;
	
	/**
	 * Constructor of the action. The name of the action ie the actionTag does not have to be same as the name of the method. 
	 * The method can be named whatever but the actionTag is the one that is important when referring to this 
	 * action from another service. 
	 * @param method this is the method of any class that can be stored here. This if of type Method from javas reflection api
	 * @param actionTag this is the name by which this method is referred to by other services. 
	 */
	public Action(Method method, String actionTag) {
		this.method = method;
		this.actionTag = actionTag;
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
	 * getter method for actionTag which is a name of the method. 
	 * @return the action tag which is a string
	 */
	public String getActionTag() {
		return actionTag;
	}
	
	/**
	 * sets the action tag
	 * @param actionTag the action tag to set.
	 */
	public void setActionTag(String actionTag) {
		this.actionTag = actionTag;
	}
	
}
