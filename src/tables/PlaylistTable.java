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

//the playlist table obtains all the info from playlistsongs and also is basically a replica of the youtubetable
public class PlaylistTable {
	private static Connection derbyConn = null;
	private static String tableName = "PLAYLIST_SONGS";
	private int size;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	
	public PlaylistTable(Connection conn) throws SQLException {		
		derbyConn = conn;
		Statement s = derbyConn.createStatement();
		ResultSet results = s.executeQuery("select * from "+tableName);
		while(results.next()) {
		    size++;
		}
	}
	
	//this method is called from getyoutubelinks in internet functions, and it just adds all of the songs obtained to the table
	public void addSong(String songName, String youtubeLink, String youtubeTitle, int viewCount, Date newestDate, Date oldestDate, int playCount, int id) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();
		boolean add = true;
		songName = songName.replace("'", "''");
		youtubeTitle = youtubeTitle.replace("'", "''");
		
		ResultSet results = s.executeQuery("select count(*) from "+tableName + " where SONG_NAME = '" + songName + "'");
		while(results.next()){
			if(Integer.parseInt(results.getString(1)) != 0){
				add = false;
			}
		}
		
		// Add song if it doesn't exist
		if(!songExists(songName) && add) {
			System.out.println("Adding: "+youtubeTitle);
			s.execute("insert into "+tableName+" (ID, SONG_NAME, YOUTUBE_LINK, YOUTUBE_TITLE, VIEW_COUNT, NEWEST_DATE, OLDEST_DATE, PLAY_COUNT) values"
					+  " (" + id + ", '" +songName+"', '"+youtubeLink+"', '"+youtubeTitle+"', "+viewCount+", '"+sdf.format(newestDate)+"', '"+sdf.format(oldestDate)+"', "+playCount+")");
		// Else skip
		}
		else{
			System.out.println("ignored playlisttable add");
		}
		s.close();
	}
	
	//a helper method in addSong, songExists checks if the song name is already in the table
	private boolean songExists (String song) throws SQLException {
		Statement s = null;
		ResultSet results = null;
		try {
			s = derbyConn.createStatement();
			results = s.executeQuery("select SONG_NAME from "+tableName + " where SONG_NAME = '" + song + "'");
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
	
	//this important method, called in the playlist class, obtains the top 10%  songs of whatever column sort is passed in
	public ArrayList<String[]> getSpecificSongs(String option) throws SQLException, ParseException {
		ArrayList<String[]> songs = new ArrayList<String[]>();
		ResultSet results;
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			//id, song, link, count
			results = s.executeQuery("select ID, SONG_NAME, YOUTUBE_LINK, PLAY_COUNT from "+tableName+" order by " + option + " FETCH NEXT " + (int)Math.floor(size * .1) + " ROWS ONLY");
			while (results.next()) {
				songs.add(new String[] {results.getString(1), results.getString(2), results.getString(3), results.getString(4)});
			}
        } finally {
        	s.close();
        }
		
		return songs;
	}
	
	//this method, called in playlist after adding the song in the row, deletes the row
	public void removeSong(String id) throws SQLException{
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			//* has id, artist, title, song, newest, oldest, count
			s.executeUpdate("DELETE FROM " + tableName + " WHERE ID = " + id);
        } finally {
        	s.close();
        }
	}
	
	//this method is used to obtain the actual size of the playlisttable at the moment
	//size is edited in here, so just returning size without the other code would return the original size
	public int getSize() throws SQLException{
		int counter = 0;
		Statement s = null;
		s = derbyConn.createStatement();
		ResultSet results = s.executeQuery("select * from "+tableName);
		while(results.next()) {
		    counter++;
		}
		size = counter;
		return size;
	}
}
