package tables;
//all song names from twitter which aren't as good as youtube
//xm is all songs on youtube

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RawXmTable {
	private static Connection derbyConn = null;
	private static String tableName = "RAW_XM_SONGS";
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	
	public RawXmTable(Connection conn) {
		derbyConn = conn;
	}
	
	public void addSong(String source, String artist, String title, String song, Date date, long twitterId) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();
		try {
			// Add song if it doesn't exist
			if(!songExists(twitterId)) {
				System.out.println("Adding: "+song);
				s.executeUpdate("insert into "+tableName+" (SOURCE, ARTIST, TITLE, SONG, DATE, TWITTER_ID) values"
						+  " ('"+source+"', '"+artist+"', '"+title+"', '"+song+"', '"+sdf.format(date)+"', "+twitterId+")");
				
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
	
	public ArrayList<String[]> getSongs() throws SQLException {
		ArrayList<String[]> songs = new ArrayList<String[]>();
		ResultSet results;
		Statement s = null;
		 
		try {
			s = derbyConn.createStatement();
			results = s.executeQuery("select SONG, DATE, ARTIST, TITLE from "+tableName+" order by SONG asc, DATE asc");
			
			while (results.next()) {
				//not a zero based indexing with result sets
				//song, date, artist, title
				String[] song = { results.getString(1), results.getString(2), results.getString(3), results.getString(4) };
				songs.add(song);
			}
        } finally {
        	s.close();
        }
		
		return songs;
	}
}
