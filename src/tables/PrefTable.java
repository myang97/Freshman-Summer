package tables;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import database.MusicDB;
import objects.InitialSongPref;

public class PrefTable {
	private static Connection derbyConn = null;
	private static String tablePrefName = "PREF_EVENTS";
	private static String tableDisplayName = "PREF_EVENTS_DISPLAYED";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public PrefTable(Connection conn) {		
		derbyConn = conn;
	}

	public void addSong(String vote, String songName, String amountListened, String duration, String type) throws IOException, SQLException {
		Statement s = derbyConn.createStatement();
		//add/update with everything but the vote to prevent upvote and downvote durations affecting
		if(vote.equals("up") || vote.equals("down")){
			// Add song if it doesn't exist
			int upVote = 0;
			int downVote = 0;
			if(vote.equals("up")){
				upVote = 1;
			}
			else {
				downVote = 1;
			}
			s.execute("insert into "+tablePrefName+" (SONG, UPVOTE, DOWNVOTE, EVENT) values"
					+" ('"+songName+"', "+upVote+", "+downVote+", '"+type+"')");
		}
		//add/update only with the vote
		else if(!type.equals("clickedOn")){
			System.out.println(songName + " " + amountListened + " " + duration + " " + type);
			s.execute("insert into "+tablePrefName+" (SONG, LISTENED_AMT, DURATION, EVENT) values"
					+" ('"+songName+"', "+amountListened+", "+duration+", '"+type+"')");
		}
		else{
			s.execute("insert into "+tablePrefName+" (SONG, EVENT) values"
					+" ('"+songName+"', '"+type+"')"); 
		}
		s.close();
	}
	
	//obtains the list of songs sorted by the rating given
	public ArrayList<InitialSongPref> getPlaylist() throws SQLException{
		ResultSet results;
		Statement s = null;
		ArrayList<InitialSongPref> songs = new ArrayList<InitialSongPref>();
		try {
			s = derbyConn.createStatement();
			//* has song name, listened amount, duration, upvote, downvote, event
			results = s.executeQuery("select * from PREF_EVENTS");
			InitialSongPref song;
			while (results.next()) {
				boolean updated = false;
				if(results.getString(6).equals("clickedOn")){
					song = new InitialSongPref(results.getString(1), results.getString(6));
					for(int i = 0; i < songs.size(); i++){
						if(songs.get(i).getSongName().equals(song.getSongName())){
							songs.get(i).updateClickRating();
							updated = true;
						}
					}
				}
				//obtains the upvote downvote stuff
				else if(results.getString(2) == null){
					song = new InitialSongPref(results.getString(1), Integer.parseInt(results.getString(4)), Integer.parseInt(results.getString(5)), results.getString(6));
					for(int i = 0; i < songs.size(); i++){
						if(songs.get(i).getSongName().equals(song.getSongName())){
							songs.get(i).updateVotes(song.getUpVote(), song.getDownVote());
							updated = true;
						}
					}
				}
				//obtains the duration information
				else{
					song = new InitialSongPref(results.getString(1), Double.parseDouble(results.getString(2)), Double.parseDouble(results.getString(3)), results.getString(6));
					for(int i = 0; i < songs.size(); i++){
						if(songs.get(i).getSongName().equals(song.getSongName())){
							songs.get(i).updateTimes(Double.parseDouble(results.getString(2)), song.getDuration());
							updated = true;
						}
					}
				}
				if(!updated){
					songs.add(song);
				}
			}
			
		} finally {
        	s.close();
        }
		
		Collections.sort(songs);
		return songs;
	}
	
	public void addDisplayValues(String songName, double totalListened, int upVote, int downVote, int rating) throws SQLException{
		Statement s = derbyConn.createStatement();
		songName = songName.replace("'", "''");
		DecimalFormat dec = new DecimalFormat("#.00");
		String totalListenedRounded = dec.format(totalListened);

		s.execute("insert into "+tableDisplayName+" values"
					+" ('"+songName+"', "+totalListenedRounded+", "+upVote+", "+downVote+", "+rating+")");
		s.close();
	}
	
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
}
