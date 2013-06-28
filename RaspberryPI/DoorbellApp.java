
import servicediscovery.Application;


public class DoorbellApp extends Application{
	int countNumOfReplies;
	
	 public static void main(String[] args) throws Exception{
	        DoorbellApp app = new DoorbellApp();
	        app.registerActionstoServices("onDoorbell",DoorbellApp.class);
	        app.registerActionstoServices("onLocationFound",DoorbellApp.class);
	        //app.callMethod(strary);
	    }

	 public void onDoorbell(Object parameters) {
	        // this will print out the message sent by the doorbell.
	    	String[] msg = (String[])parameters;
	    	countNumOfReplies++;
	    	System.out.println("The number of replies is " + countNumOfReplies);
	    	System.out.println(msg[0]);
	    	
	        
	    }
	 
	 public void onLocationFound(Object parameters) {
		 countNumOfReplies++;
	 }
	 
}
