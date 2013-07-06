package servicediscovery;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * This is a location class. It is a property. 
 * The match function expects an input in the form of the key value pairs listed below: 
 * PROPERTYNAME Location
 * HOME  		<name of the home>
 * FLOOR  		<name of the floor>
 * ROOM  		<name of the room>
 * INROOM 		<On of the values of the enum from  Top, Bottom, Left, Right, Front, Back>
 * USERTAG		<any tag from the user>
 * @author parth
 *
 */
public class Location extends Property {

	// a map of home name and location inside the home name. 
	Map<String,SubLocation> locations;
	public boolean DEBUG = true;
	public Location(){
		locations = new HashMap<String, Location.SubLocation>();
		name = "Location";
	}
	
	public void addLocation(String home, String floor, String room, String inRoom, String userTag) {
		
		if(home == null) return;
		
		SubLocation subL = new SubLocation(floor,room,inRoom,userTag);
		if(home!=null)
			locations.put(home.toLowerCase(),subL);
		
		if(DEBUG) System.out.println("After adding " + locations.toString());
		
	}
	
	@Override
	public boolean match(Map<String,String> queryProperties) {
		
		// if the property name of the object is the same as your property name
		if(queryProperties.get("PROPERTYNAME")==null || !queryProperties.get("PROPERTYNAME").equals(name))
			return false;
		
		// if the service has this home in the location object then get
		// its sublocation else return false.
		String homeName = queryProperties.get("HOME").toLowerCase();
		if(homeName == null) 
		{
			if(DEBUG)System.out.println("Home not in query");
			return false;
		}
			 
		SubLocation subL = locations.get(homeName);
		if(subL==null)
		{
			if(DEBUG)System.out.println("Home not found");
			return false;
		}
		// this is the matching scheme given to location
		if(DEBUG)System.out.println("Checking Floor");
		String floor = queryProperties.get("FLOOR");
		if(floor!=null && !floor.equalsIgnoreCase(subL.floor)) 
			return false;
		
		if(DEBUG)System.out.println("Checking room");
		String room = queryProperties.get("ROOM");
		if(room!=null && !room.equalsIgnoreCase(subL.room)) 
			return false;
		
		if(DEBUG)System.out.println("Checking inRoom");
		String inRoom = queryProperties.get("INROOM");
		if(inRoom!=null && !inRoom.equalsIgnoreCase(subL.inRoom)) 
			return false;
		
		if(DEBUG)System.out.println("Checking usertag");
		String userTag = queryProperties.get("USERTAG");
		if(userTag!=null && !userTag.equalsIgnoreCase(subL.userTag)) 
			return false;
		
		return true;
	}

	private static class SubLocation {
		String floor;
		String room;
		String inRoom;
		String userTag;
		
		public SubLocation(String floor2, String room2, String inRoom2,
				String userTag2) {
			floor = floor2;
			room = room2;
			userTag = userTag2;
			if(inRoom2!=null) 
			{
				if(inRoom2.equalsIgnoreCase("top")) inRoom = "Top" ;
				else if(inRoom2.equalsIgnoreCase("bottom")) inRoom = "Bottom" ;
				else if(inRoom2.equalsIgnoreCase("right")) inRoom = "Right" ;
				else if(inRoom2.equalsIgnoreCase("left")) inRoom = "Left" ;
				else if(inRoom2.equalsIgnoreCase("front")) inRoom = "Front" ;
				else if(inRoom2.equalsIgnoreCase("back")) inRoom = "Back" ;
				else throw new IllegalArgumentException("Illegal Argument");
			}
		}
		
		public String toString()
		{
			return floor +" " + room +" "+ inRoom +" "+ userTag;
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Unit tests for location object");
		
		Location speakerLocation = new Location();
		speakerLocation.addLocation("MyHome", "one", "bedroom", "top", "Nearwindow");
		System.out.println(speakerLocation.printProperty());
		Map<String,String> map = new HashMap<String,String>();
		map.put("HOME","MyHome");
		checkMatch(speakerLocation, map);
		map.put("PROPERTYNAME","Location");
		checkMatch(speakerLocation, map);
		map.put("FLOOR","one");
		checkMatch(speakerLocation, map);
		map.put("ROOM","bedroom");
		checkMatch(speakerLocation, map);
		map.put("INROOM","top");
		checkMatch(speakerLocation, map);
		map.put("USERTAG","nearwindow");
		checkMatch(speakerLocation, map);
		
		speakerLocation.addLocation("urhome", null, null, null, "ONTHEFLOOR");
		map = new HashMap<String,String>();
		map.put("PROPERTYNAME","Location");
		map.put("HOME","myhome");
		map.put("USERTAG","onthefloor");
		checkMatch(speakerLocation, map);

		
	}
	public static void checkMatch(Location speakerLocation, Map<String,String> map) {
		if(speakerLocation.match(map))
			System.out.println("The location is a match for "+ map.toString());
		else
			System.out.println("The location is NOT a match for "+ map.toString());
	}

	@Override
	public String printProperty() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("PROPERTYNAME-");
		buffer.append("Location");
		buffer.append(",");
		for(Entry<String,SubLocation> entry : locations.entrySet()) 
		{
			buffer.append("HOME-");
			buffer.append(entry.getKey());
			buffer.append(",");

			buffer.append("FLOOR-");
			buffer.append(entry.getValue().floor);
			buffer.append(",");
			buffer.append("ROOM-");
			buffer.append(entry.getValue().room);
			buffer.append(",");
			buffer.append("INROOM-");
			buffer.append(entry.getValue().inRoom);
			buffer.append(",");
			buffer.append("USERTAG-");
			buffer.append(entry.getValue().userTag);
			buffer.append(",");
		}
		return buffer.toString();
	}

}
