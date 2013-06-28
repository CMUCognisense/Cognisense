package servicediscovery;
import java.lang.reflect.Method;


public class Action {

	Method method;
	Object parameters;
	String actionTag;
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object getParameters() {
		return parameters;
	}
	public void setParameters(Object parameters) {
		this.parameters = parameters;
	}
	public String getActionTag() {
		return actionTag;
	}
	public void setActionTag(String actionTag) {
		this.actionTag = actionTag;
	}
	
}
