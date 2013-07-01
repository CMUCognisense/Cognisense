package servicediscovery;

import java.util.Map;

public class Volume extends Property{

	String level;
	@Override
	public boolean match(Map<String, String> properties) {
		
		return false;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}

}
