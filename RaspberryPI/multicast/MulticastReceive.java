package multicast;

import java.util.EventObject;

/**
 * This interface defines the method that should be called when a message is received. 
 * @author parth
 *
 */
public interface MulticastReceive {
	/**
	 * This method is to be called when a new message is received. This interface is defined by the multicast
	 * layer and implemented by the service discovery layer. The class implementing this interface needs
	 * to be added to the listner list using the addEventListner function of the multicast layer.  
	 * @param e This is an object of RecvMessageEvent class that contains the message received when this event occurred. 
	 */
	public void onReceiveMessage(RecvMessageEvent e);
}
