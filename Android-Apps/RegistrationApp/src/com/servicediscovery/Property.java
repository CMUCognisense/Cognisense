package com.servicediscovery;
import java.util.Map;


public abstract class Property {

	private String name;
	
	public abstract boolean match(Map<String,String> properties);
	
	public abstract String printProperty();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
