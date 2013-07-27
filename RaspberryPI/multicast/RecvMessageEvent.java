package multicast;
import java.util.EventObject;

/**
 * This class defines the event that is generated when a new message is received by the multicast layer. 
 * @author parth
 *
 */
public class RecvMessageEvent extends EventObject {
     
	private String message;
	/**
	 * This is the constructor of the event. 
	 * @param source This is the source of the event that class that generates this event
	 * @param message this is the message that is to be sent along with the event. 
	 */
             public RecvMessageEvent(Object source,String message) {
                 super(source);
                 this.setMessage(message);
             }
             /**
              * get the message in the event. 
              * @return this is the message associated with the event.  
              */
			public 	String getMessage() {
				return message;
			}
			/**
			 * set the message with the event. This is used to associate a message with the event. 
			 * @param message the string that contains the message to be associated. 
			 */
			public void setMessage(
			String message) {
				this.message = message;
			}
}
