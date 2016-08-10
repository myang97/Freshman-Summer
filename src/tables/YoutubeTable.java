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

//this table obtains the info from getyoutubelinks as well as add songs to the playlisttable class
public class YoutubeTable {
	private static Connection derbyConn = null;
	private static String tableName = "YOUTUBE_LINKS";
	private int size;
	
	PlaylistTable playlistTable;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	
	public YoutubeTable(Connection conn) throws SQLException {		
		derbyConn = conn;
		Statement s = derbyConn.createStatement();
		ResultSet results = s.executeQuery("select * from "+tableName);
		while(results.next()) {
		    size++;
		}
		playlistTable = new PlaylistTable(derbyConn);
	}
	
	//adds songs from getyoutubelinks
	public void addSong(String songName, String youtubeLink, String youtubeTitle, int viewCount, Date newestDate, Date oldestDate, int playCount) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();
		
		songName = songName.replace("'", "''");
		youtubeTitle = youtubeTitle.replace("'", "''");
		
		// Add song if it doesn't exist
		if(!songExists(songName)) {
			System.out.println("Adding: "+youtubeTitle);
			s.execute("insert into "+tableName+" (SONG_NAME, YOUTUBE_LINK, YOUTUBE_TITLE, VIEW_COUNT, NEWEST_DATE, OLDEST_DATE, PLAY_COUNT) values"
					+  " ('"+songName+"', '"+youtubeLink+"', '"+youtubeTitle+"', "+viewCount+", '"+sdf.format(newestDate)+"', '"+sdf.format(oldestDate)+"', "+playCount+")");
			
		// Else ignore
		}
		else{
			System.out.println("ignored youtubetable add");
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
	
	//called in youtubetomp3 to get all of the information from the youtube table in order to download to mp3
	public ArrayList<String[]> getLinks() throws SQLException {
		ArrayList<String[]> songs = new ArrayList<String[]>();
		ResultSet results;
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			results = s.executeQuery("select SONG_NAME, YOUTUBE_LINK, YOUTUBE_TITLE, PLAY_COUNT from "+tableName);
			
			while (results.next()) {
				songs.add(new String[] {results.getString(1), results.getString(2), results.getString(3), results.getString(4)});
			}
        } finally {
        	s.close();
        }
		
		return songs;
	}

	//this method adds all of the songs and adds them to playlisttable, and playlisttable doesn't add the duplicates
	public void getAllSongsToAddToPlaylistTable() throws SQLException, NumberFormatException, IOException, ParseException {
		ArrayList<String[]> songs = new ArrayList<String[]>();
		ResultSet results;
		Statement s = null;
		
		try {
			s = derbyConn.createStatement();
			results = s.executeQuery("select SONG_NAME, YOUTUBE_LINK, YOUTUBE_TITLE, VIEW_COUNT, NEWEST_DATE, OLDEST_DATE, PLAY_COUNT, ID from "+tableName);
			
			while (results.next()) {
				playlistTable.addSong(results.getString(1), results.getString(2), results.getString(3), Integer.parseInt(results.getString(4)), sdf.parse(results.getString(5)), sdf.parse(results.getString(6)), Integer.parseInt(results.getString(7)), Integer.parseInt(results.getString(8)));
			}
        } finally {
        	s.close();
        }
	}
	
	//called in the playlist class, this method gets the initialsongpref songname and finds the row the songname is located in the youtubetable
	public String[] getSpecificSongByName(String songName) throws SQLException{
		ResultSet results;
		Statement s = null;
		String[] song = new String[]{""};
		
		try {
			songName = songName.replace("'", "''");
			s = derbyConn.createStatement();
			results = s.executeQuery("select ID, SONG_NAME, YOUTUBE_LINK, PLAY_COUNT from APP.YOUTUBE_LINKS where SONG_NAME LIKE '%" + songName + "%'");
			while(results.next()){
				song = new String[] {results.getString(1), results.getString(2), results.getString(3), results.getString(4)};
			}
		} finally {
			s.close();
		}
		return song;
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
