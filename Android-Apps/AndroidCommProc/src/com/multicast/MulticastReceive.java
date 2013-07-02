package com.multicast;

import java.util.EventObject;


public interface MulticastReceive {
	public void onReceiveMessage(RecvMessageEvent e);
}
