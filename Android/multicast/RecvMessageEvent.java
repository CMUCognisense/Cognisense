package multicast;
import java.util.EventObject;


public class RecvMessageEvent extends EventObject {
     /**
	 * 
	 */
			private //here's the constructor
			String message;
             public RecvMessageEvent(Object source,String message) {
                 super(source);
                 this.setMessage(message);
             }
			public //here's the constructor
			String getMessage() {
				return message;
			}
			public void setMessage(//here's the constructor
			String message) {
				this.message = message;
			}
}
