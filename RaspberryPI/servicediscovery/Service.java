package servicediscovery;

import java.util.Set;


public class Service {
		private String serviceid;
		private String serviceType;
		private Set<Action> actions;
		private Set<Trigger> trigger;
		private Property[] properties;
		public String getServiceid() {
			return serviceid;
		}
		public void setServiceid(String serviceid) {
			this.serviceid = serviceid;
		}
		
		
		
	}