package com.multicast;
import java.util.EventObject;


public class RecvMessageEvent extends EventObject {
	//here's the constructor
	String message;
	public RecvMessageEvent(Object source,String message) {
		super(source);
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}
