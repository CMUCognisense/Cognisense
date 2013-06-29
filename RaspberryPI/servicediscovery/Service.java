package servicediscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Service {
		private String serviceid;
		private String serviceType;
		private Set<Action> actions;
		private Set<Trigger> triggers;
		// property name and the property object
		private Map<String,Property> properties;
		
		
		public Service() {
			actions = new HashSet<Action>();
			triggers = new HashSet<Trigger>();
			setProperties(new HashMap<String, Property>());
		}
		
		public String getServiceid() {
			return serviceid;
		}
		public void setServiceid(String serviceid) {
			this.serviceid = serviceid;
		}
		public String getServiceType() {
			return serviceType;
		}
		public void setServiceType(String serviceType) {
			this.serviceType = serviceType;
		}
		public Set<Action> getActions() {
			return actions;
		}
		public void setActions(Set<Action> actions) {
			this.actions = actions;
		}
		public Set<Trigger> getTrigger() {
			return triggers;
		}
		public void setTrigger(Set<Trigger> trigger) {
			this.triggers = trigger;
		}

		public void addAction(Action action) {
			actions.add(action);
		}

		public Map<String,Property> getProperties() {
			return properties;
		}

		public void setProperties(Map<String,Property> properties) {
			this.properties = properties;
		}
		
		public void addProperties(Property property) {
			properties.put(property.name, property);
		}
		
		public void addTrigger(Trigger trigger) {
			triggers.add(trigger);
		}
		
		
		
	}