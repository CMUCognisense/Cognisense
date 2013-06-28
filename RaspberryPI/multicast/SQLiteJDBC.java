package multicast;
import java.sql.*;
import java.util.HashSet;
import java.util.LinkedList;

public class SQLiteJDBC
{
	Connection c;
    
	
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
	
 public static void main( String args[] )
  {
	 SQLiteJDBC db = new SQLiteJDBC();
	 System.out.println("Before.."+db.query().toString());
	 
	 db.insert("asdas");
	 System.out.println("After.."+db.query().toString());
	 
  }
}
