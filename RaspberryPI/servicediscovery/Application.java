package servicediscovery;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Application {
	
	ServiceDiscoveryLayer sdl = null;
	
    public static void main(String[] args) throws Exception{
        Application app = new Application();
        String[] strary = new String[1];
        strary[0] = "Message";
        app.callMethod(strary);
    }

    public void callMethod(String[] string) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	sdl.callMethod(string);
	}

	public Application() {
    	sdl = new ServiceDiscoveryLayer(true);
    	sdl.registerApp(this);
    }
    
    public void registerActionstoServices(String serviceId, String methodName, Class class1) throws Exception {
        Method method1 = class1.getMethod(methodName, Object.class);
    	sdl.registerActions(serviceId, method1);
    }

    public void registerTriggerstoServices(String serviceId, String methodName, Class class1) throws Exception {
        Method method1 = class1.getMethod(methodName, Object.class);
    	sdl.registerTriggers(serviceId, method1);
    }
    
	public ServiceDiscoveryLayer getSdl() {
		return sdl;
	}
    
    

}

