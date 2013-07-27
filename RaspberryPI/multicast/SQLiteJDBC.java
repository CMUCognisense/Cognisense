package multicast;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This is a wraper for the interracting with the database. 
 * The database is a single table database to store all the mac addresses of the different devices 
 * that reply to messages. 
 * @author parth
 *
 */
public class SQLiteJDBC
{
	private Connection c;
    
	
	public SQLiteJDBC() {

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Opened database successfully");
	      Statement stmt;
	      c.setAutoCommit(false);
	      stmt = c.createStatement();
	      String sql = "CREATE TABLE IF NOT EXISTS DEVICES " +
	                   "(ID TEXT PRIMARY KEY     NOT NULL)";
	      stmt.executeUpdate(sql);
	      stmt.close();
	      
	    } catch ( Exception e ) {
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        //System.exit(0);
	      }

		
	}
	
	/**
	 * insert a new mac address into the table
	 * this is called when a new device is to be added to the list 
	 * of devices that have responded
	 * @param macAddress the macAddress 
	 */
	public void insert(String macAddress) {
		
		Statement stmt;
		try {
			stmt = c.createStatement();
		
	      String sql = "INSERT INTO DEVICES (ID)" +
	                   "VALUES ('"+macAddress+"');"; 
	      stmt.executeUpdate(sql);
	      c.commit();
		} catch (SQLException e) {
	        //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			
		}
	}
	
	/**
	 * this is to query all the mac addresses of the devices known to respond to a message. 
	 * @return this is a hashset of strings of all mac addresses that are known to respond. 
	 */
	public HashSet<String> query() {
		
		Statement stmt;
		HashSet<String> idlist = null;
		try {
			stmt = c.createStatement();
			 ResultSet rs = stmt.executeQuery( "SELECT * FROM DEVICES;" );
			 idlist = new HashSet<String>();
		      while ( rs.next() ) {
		         String  id = rs.getString("ID");
		         //System.out.println("id: "+id);
		         idlist.add(id);
		      }
		      rs.close();
		      stmt.close();
		      return idlist;
	      
		} catch (SQLException e) {
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        return idlist;
			
		}
	}
	/**
	 * this is a debug method to check if the functions work correctly
	 * @param args
	 */
 public static void main( String args[] )
  {
	 SQLiteJDBC db = new SQLiteJDBC();
	 System.out.println("Before.."+db.query().toString());
	 
	 db.insert("asdas");
	 System.out.println("After.."+db.query().toString());
	 
  }
}
