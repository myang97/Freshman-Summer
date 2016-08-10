package tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import objects.XmSong;

public class XmTable {
	private static Connection derbyConn = null;
	private static String tableName = "XM_SONGS";
	private int size;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	
	public XmTable(Connection conn) throws SQLException {
		derbyConn = conn;
		Statement s = derbyConn.createStatement();
		ResultSet results = s.executeQuery("select * from "+tableName);
		while(results.next()) {
		    size++;
		}
	}
	
	public void addSong(XmSong xmSong) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();
		String song = xmSong.getSongName();
		String artist = xmSong.getArtist();
		String title = xmSong.getTitle();
		Date newestDate = xmSong.getNewestDate();
		Date oldestDate = xmSong.getOldestDate();
		int playCount = xmSong.getPlayCount();

		try {
			// Add song if it doesn't exist
			if(!songExists(xmSong)) {
				System.out.println("Adding: "+song);
				song = song.replace("'", "''");
				artist = artist.replace("'", "''");
				title = title.replace("'", "''");
				s.executeUpdate("insert into "+tableName+" (ARTIST, TITLE, SONG, NEWEST_DATE, OLDEST_DATE, PLAY_COUNT) values"
						+  " ('"+artist+"', '"+title+"', '"+song+"', '"+sdf.format(newestDate)+"', '"+sdf.format(oldestDate)+"', "+playCount+")");
				
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
	
	private boolean songExists (XmSong xmSong) throws SQLException {
		Statement s = null;
		String song = xmSong.getSongName();
		
		try {
			s = derbyConn.createStatement();
			ResultSet results = s.executeQuery("select * from "+tableName+" where SONG = '"+song+"'");
			
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
	
	public ArrayList<XmSong> getSongs() throws SQLException, ParseException {
		ArrayList<XmSong> songs = new ArrayList<XmSong>();
		ResultSet results;
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			//* has id, artist, title, song, newest, oldest, count
			results = s.executeQuery("select * from "+tableName+" order by PLAY_COUNT desc, NEWEST_DATE asc");
			
			while (results.next()) {
				XmSong song = new XmSong(results.getString(2), results.getString(3), results.getString(4), sdf.parse(results.getString(5)), 
						sdf.parse(results.getString(6)), Integer.parseInt(results.getString(7)));
				songs.add(song);
			}
        } finally {
        	s.close();
        }
		
		return songs;
	}
	
	public ArrayList<XmSong> getSpecificSongs(String option) throws SQLException, ParseException {
		ArrayList<XmSong> songs = new ArrayList<XmSong>();
		ResultSet results;
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			//* has id, artist, title, song, newest, oldest, count
			results = s.executeQuery("select * from "+tableName+" order by " + option + " FETCH NEXT " + (int)Math.floor(size * .1) + " ROWS ONLY");
			while (results.next()) {
				XmSong song = new XmSong(results.getString(2), results.getString(3), results.getString(4), sdf.parse(results.getString(5)), 
						sdf.parse(results.getString(6)), Integer.parseInt(results.getString(7)));
				songs.add(song);
			}
        } finally {
        	s.close();
        }
		
		return songs;
	}
	
	public String getSize() throws SQLException{
		Statement s = null;
		int i = 0;
		try {
			s = derbyConn.createStatement();
			ResultSet results = s.executeQuery("select * from "+tableName);
			i = 0;
			while(results.next()) {
			    i++;
			}
		} finally {
        	s.close();
        }
		
		return i + "";
	}
}
