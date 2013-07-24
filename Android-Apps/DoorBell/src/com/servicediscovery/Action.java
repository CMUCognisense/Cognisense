package com.servicediscovery;
import java.io.Serializable;
import java.lang.reflect.Method;


public class Action implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Method method;
	private String actionTag;
	
	public Action(Method method, String actionTag) {
		this.method = method;
		this.actionTag = actionTag;
	}
	
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getActionTag() {
		return actionTag;
	}
	public void setActionTag(String actionTag) {
		this.actionTag = actionTag;
	}
	
}
