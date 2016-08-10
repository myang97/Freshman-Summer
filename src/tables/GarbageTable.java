package tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GarbageTable {
	private static Connection derbyConn = null;
	private static String tableName = "RAW_XM_GARBAGE";
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	
	public GarbageTable(Connection conn) {
		derbyConn = conn;
	}
	
	public void addSong(String source, String song, Date date, long twitterId) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();

		try {
			// Add song if it doesn't exist
			if(!songExists(twitterId)) {
				System.out.println("  Garbage: "+song);
				s.executeUpdate("insert into "+tableName+" (SOURCE, SONG, DATE, TWITTER_ID) values"
						+  " ('"+source+"','"+song+"', '"+sdf.format(date)+"', "+twitterId+")");
				
			// Else throw exception
			} else {
				throw new IOException("Duplicate entry attempted");
			}
		
        } catch (SQLException sqle) {
        	throw sqle;
	    } finally {
	    	s.close();
	    }
	}
	
	private boolean songExists (long twitterId) throws SQLException {
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			ResultSet results = s.executeQuery("select * from "+tableName+" where TWITTER_ID = "+twitterId);
			
			if (results.next()) {
				return true;
			}
			
        } catch (SQLException sqle) {
        		return false;
        } finally {
        	s.close();
        }
		
		return false;
	}
}