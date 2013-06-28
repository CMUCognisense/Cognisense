package servicediscovery;
import java.lang.reflect.Method;


public class Trigger {

	Method method;
	Object parameters;
	String triggerTag;
	
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
		return triggerTag;
	}
	public void setActionTag(String actionTag) {
		this.triggerTag = actionTag;
	}
}
