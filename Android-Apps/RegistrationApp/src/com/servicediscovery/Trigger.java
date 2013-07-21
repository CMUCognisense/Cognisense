package com.servicediscovery;
import java.lang.reflect.Method;


public class Trigger {

	Method method;
	String triggerTag;
	
	public Trigger(Method method, String triggerTag) {
		this.method = method;
		this.triggerTag = triggerTag;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	
	public String getTriggerTag() {
		return triggerTag;
	}
	public void setTriggerTag(String actionTag) {
		this.triggerTag = actionTag;
	}
}
