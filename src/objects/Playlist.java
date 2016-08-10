package objects;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import database.MusicDB;
import tables.YoutubeTable;
import tables.PlaylistTable;
import tables.PrefTable;

public class Playlist {
	private ArrayList<ArrayList<PlaylistSong>> playlist = new ArrayList<ArrayList<PlaylistSong>>();
	private ArrayList<ArrayList<PlaylistSong>> preferredPlaylist = new ArrayList<ArrayList<PlaylistSong>>();
	private ArrayList<PlaylistSong> singlePreferredPlaylist = new ArrayList<PlaylistSong>();
	private ArrayList<PlaylistSong> singlePlaylist = new ArrayList<PlaylistSong>();
	
	//called in the getpreferredplaylist servlet
	//this method returns an arraylist of an arraylist of playlist songs, which were grabbed from the youtubetable
	public ArrayList<ArrayList<PlaylistSong>> getPreferredPlaylist() throws ClassNotFoundException, SQLException{
		//obtains list, which is the sorted playlist by rating with the youtube table values in string array
		//this arraylist can then be used in the playlist method
		Connection derbyConn = MusicDB.getConnection();
		PrefTable prefTable = new PrefTable(derbyConn);
		YoutubeTable youtubeTable = new YoutubeTable(derbyConn);
		//the arraylist of initialsongpref is the list of songs sorted by the rating given
		//the values in each initialsongpref can be added to a database
		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
		//the string array contains ID, SONG_NAME, YOUTUBE_LINK, PLAY_COUNT
		ArrayList<String[]> list = new ArrayList<String[]>();

		for(InitialSongPref song : songs){
			String songName = song.getSongName().replace("amp;", "");
			list.add(youtubeTable.getSpecificSongByName(songName));
		}
		
		//turns the string arrays into playlistsongs and adds them to the global singlepreferredplaylist
		addPreferredPlaylistSongs(list);
		
		for(int i = 1; i <= singlePreferredPlaylist.size(); i++){
			singlePreferredPlaylist.get(i - 1).setListId(i);
		}
		preferredPlaylist.add(singlePreferredPlaylist);
		
		return preferredPlaylist;
	}
	
	//almost like the getpreferredplaylist method above, this method is called from the getplaylist servlet
	public ArrayList<ArrayList<PlaylistSong>> getPlaylist(int count, int newDate, int random) throws ClassNotFoundException, SQLException, ParseException {
		Connection conn = MusicDB.getConnection();
		PlaylistTable playlistTable = new PlaylistTable(conn);
		//PlaylistTable playlistTable = new PlaylistTable(conn);
		
		//id, song, link, count in the string array
		ArrayList<String[]> songsByCount = playlistTable.getSpecificSongs("PLAY_COUNT DESC");
		ArrayList<String[]> songsByNewDate = playlistTable.getSpecificSongs("NEWEST_DATE DESC");
		ArrayList<String[]> songsByRandom = playlistTable.getSpecificSongs("RANDOM()");
		
		addSongs(songsByCount, count);
		addSongs(songsByNewDate, newDate);
		addSongs(songsByRandom, random);
		
		//add the list numbers to the playlistsongs
		//also sort the playlist songs by the counter, so that the most played will the be the first in every playlist
		Collections.sort(singlePlaylist);
		for(int i = 1; i <= singlePlaylist.size(); i++){
			singlePlaylist.get(i - 1).setListId(i);
		}
		playlist.add(singlePlaylist);
		singlePlaylist = new ArrayList<PlaylistSong>();
		return playlist;
	}

	//called from the getpreferredplaylist method above, this method gets the string array and changes it to a playlistsong
	//the same song on the playlisttable is removed from and then the song is added to the playlist to be added to the arraylist of arraylist of songs
	public void addPreferredPlaylistSongs(ArrayList<String[]> songsToAdd) throws ClassNotFoundException, SQLException{
		Connection conn = MusicDB.getConnection();
		PlaylistTable playlistTable = new PlaylistTable(conn);
		int size = songsToAdd.size();
		for(int i = 0; i < size; i++){
			PlaylistSong playlistSong = new PlaylistSong(songsToAdd.get(i)[1], songsToAdd.get(i)[2], Integer.parseInt(songsToAdd.get(i)[3]));
			playlistTable.removeSong(songsToAdd.get(i)[0]);
			singlePreferredPlaylist.add(playlistSong);
		}
	}
	
	//called from the getplaylist method above, it functions basically the same as addpreferredplaylistsongs
	//the only difference is that the arraylist of string arrays was not sorted like that in the method above
	public void addSongs(ArrayList<String[]> songsToAdd, int number) throws SQLException, ClassNotFoundException{
		Connection conn = MusicDB.getConnection();
		PlaylistTable playlistTable = new PlaylistTable(conn);
		
		//numbers is the index of all of the songs in the arraylist of string arrays
		//the numbers are shuffled in order to randomize which songs are chosen from the original 10% list
		ArrayList<Integer> numbers = new ArrayList<Integer>();     
		for(int i = 0; i < songsToAdd.size(); i++){       
			numbers.add(i);     
		}      
		Collections.shuffle(numbers);
		
		//grabs unique songs based on the shuffled numbers (number variable is the amount to grab)
		for(int i = 0; i < number; i++){
			String[] song = songsToAdd.get(numbers.get(i));
			PlaylistSong playlistSong = new PlaylistSong(song[1], song[2], Integer.parseInt(song[3]));
			if(!singlePlaylist.contains(playlistSong)){
				//remove by id
				playlistTable.removeSong(song[0]);
				singlePlaylist.add(playlistSong);
			}
			else{
				number++;
			}
		}
	}
	
	//called in the getplaylist servlet, this method generates an xml file of the playlist songs
	public Document toXML(ArrayList<ArrayList<PlaylistSong>> playlistSongs) throws ParserConfigurationException, TransformerException, SQLException, ClassNotFoundException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root playlist element	
		Document xmlDoc = docBuilder.newDocument();
		Element rootElement = xmlDoc.createElement("PLAYLIST");
		xmlDoc.appendChild(rootElement);
		
		//updates the prefeventsdisplayed table
		Connection derbyConn = MusicDB.getConnection();
		Statement s = derbyConn.createStatement();
		PrefTable prefTable = new PrefTable(derbyConn);
		//the arraylist of initialsongpref is the list of songs sorted by the rating given
		//the values in each initialsongpref can be added to a database
		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
		s.executeUpdate("TRUNCATE TABLE PREF_EVENTS_DISPLAYED");
		for(InitialSongPref song : songs){
			String songName = song.getSongName().replace("amp;", "");
			prefTable.addDisplayValues(songName, song.getTotalListenedAmount(), song.getUpVote(), song.getDownVote(), song.getRating());
		}
		
		for(ArrayList<PlaylistSong> songList : playlistSongs) {
			for(PlaylistSong song: songList){
				String songName = song.getSongName();
				songName = songName.replace("A-Trak", "A Trak");
				songName = songName.replace("Ne-Yo", "Ne Yo");
				songName = songName.replace("C-Ro", "C Ro");
				songName = songName.replace("Blink-182", "Blink 182");
				songName = songName.replace("Keys-N-Crates", "Keys N Crates");
							
				String[] split = songName.split("-", 2);
				
				String artist = split[0].trim();
				String title = split[1].trim();
				
				// song element
				Element songElement = xmlDoc.createElement("SONG");
				rootElement.appendChild(songElement);
				
				// song id node
				Element idNode = xmlDoc.createElement("ID");
				idNode.appendChild(xmlDoc.createTextNode(Integer.toString(song.getListId())));
				songElement.appendChild(idNode);
				
				// song artist name node
				Element artistNameNode = xmlDoc.createElement("ARTIST_NAME");
				artistNameNode.appendChild(xmlDoc.createTextNode(artist));
				songElement.appendChild(artistNameNode);
				
				// song song name node
				Element songNameNode = xmlDoc.createElement("SONG_NAME");
				songNameNode.appendChild(xmlDoc.createTextNode(title));
				songElement.appendChild(songNameNode);
				
				// song id node
				Element linkNode = xmlDoc.createElement("YOUTUBE_LINK");
				linkNode.appendChild(xmlDoc.createTextNode(song.getYoutubeLink()));
				songElement.appendChild(linkNode);
				
				Element totalListenedNode = xmlDoc.createElement("TOTAL_LISTENED");
				Element upVoteNode = xmlDoc.createElement("UPVOTE");
				Element downVoteNode = xmlDoc.createElement("DOWNVOTE");
				Element ratingNode = xmlDoc.createElement("RATING");
				
				//try to find the title in the database and if not located, then just display null for
				//Total listened, upvote, downvote, rating 
				ResultSet results;
				try {
					s = derbyConn.createStatement();
					//Total listened, upvote, downvote, rating 
					title = title.replace("'", "''");
					results = s.executeQuery("select * from APP.PREF_EVENTS_DISPLAYED where SONG = '"+title+"'");
					if (results.next()) {
						totalListenedNode.appendChild(xmlDoc.createTextNode(results.getString(2)));
						upVoteNode.appendChild(xmlDoc.createTextNode(results.getString(3)));
						downVoteNode.appendChild(xmlDoc.createTextNode(results.getString(4)));
						ratingNode.appendChild(xmlDoc.createTextNode(results.getString(5)));
					}
					else {
						totalListenedNode.appendChild(xmlDoc.createTextNode("NA"));
						upVoteNode.appendChild(xmlDoc.createTextNode("NA"));
						downVoteNode.appendChild(xmlDoc.createTextNode("NA"));
						ratingNode.appendChild(xmlDoc.createTextNode("NA"));
					}
		        } finally {
		        	s.close();
		        }
				
				songElement.appendChild(totalListenedNode);
				songElement.appendChild(upVoteNode);
				songElement.appendChild(downVoteNode);
				songElement.appendChild(ratingNode);
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(xmlDoc);
		StreamResult result = new StreamResult(new File("C://Eclipse//workspaces//NuMusic//WebContent//music.xml"));
		StreamResult result2 = new StreamResult(new File("C://Apache//tomcat//webapps//ROOT//music.xml"));
		
		transformer.transform(source, result);
		transformer.transform(source, result2);
		
		return xmlDoc;
	}
	
	//called in the getpreferredplaylist servlet, this method is basically like the method above
	public Document toPreferredXML(ArrayList<ArrayList<PlaylistSong>> playlistSongs) throws ParserConfigurationException, TransformerException, ClassNotFoundException, SQLException, InterruptedException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root playlist element	
		Document xmlDoc = docBuilder.newDocument();
		Element rootElement = xmlDoc.createElement("PLAYLIST");
		xmlDoc.appendChild(rootElement);
		
		//updates the prefeventsdisplayed table
		Connection derbyConn = MusicDB.getConnection();
		Statement s = derbyConn.createStatement();
		PrefTable prefTable = new PrefTable(derbyConn);
		//the arraylist of initialsongpref is the list of songs sorted by the rating given
		//the values in each initialsongpref can be added to a database
		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
		s.executeUpdate("TRUNCATE TABLE PREF_EVENTS_DISPLAYED");

		for(InitialSongPref song : songs){
			String songName = song.getSongName().replace("amp;", "");
			prefTable.addDisplayValues(songName, song.getTotalListenedAmount(), song.getUpVote(), song.getDownVote(), song.getRating());
		}
		
		for(ArrayList<PlaylistSong> songList : playlistSongs) {
			for(PlaylistSong song: songList){
				String songName = song.getSongName();
				//System.out.println(songName);
				songName = songName.replace("A-Trak", "A Trak");
				songName = songName.replace("Ne-Yo", "Ne Yo");
				songName = songName.replace("C-Ro", "C Ro");
				songName = songName.replace("Blink-182", "Blink 182");
				songName = songName.replace("Keys-N-Crates", "Keys N Crates");
				//System.out.println(songName);			
				String[] split = song.getSongName().split("-", 2);
				
				String artist = split[0].trim().replace("+", "/");
				String title = split[1].trim();
				// song element
				Element songElement = xmlDoc.createElement("SONG");
				rootElement.appendChild(songElement);
				
				// song id node
				Element idNode = xmlDoc.createElement("ID");
				idNode.appendChild(xmlDoc.createTextNode(Integer.toString(song.getListId())));
				songElement.appendChild(idNode);
				
				// song artist name node
				Element artistNameNode = xmlDoc.createElement("ARTIST_NAME");
				artistNameNode.appendChild(xmlDoc.createTextNode(artist));
				songElement.appendChild(artistNameNode);
				
				// song song name node
				Element songNameNode = xmlDoc.createElement("SONG_NAME");
				songNameNode.appendChild(xmlDoc.createTextNode(title));
				songElement.appendChild(songNameNode);
				
				// song id node
				Element linkNode = xmlDoc.createElement("YOUTUBE_LINK");
				linkNode.appendChild(xmlDoc.createTextNode(song.getYoutubeLink()));
				songElement.appendChild(linkNode);
				
				Element totalListenedNode = xmlDoc.createElement("TOTAL_LISTENED");
				Element upVoteNode = xmlDoc.createElement("UPVOTE");
				Element downVoteNode = xmlDoc.createElement("DOWNVOTE");
				Element ratingNode = xmlDoc.createElement("RATING");
				
				//try to find the title in the database and if not located, then just display null for
				//Total listened, upvote, downvote, rating 
				ResultSet results;
				try {
					s = derbyConn.createStatement();
					//Total listened, upvote, downvote, rating 
					title = title.replace("'", "''");
					results = s.executeQuery("select * from APP.PREF_EVENTS_DISPLAYED where SONG = '"+title+"'");
					if (results.next()) {
						totalListenedNode.appendChild(xmlDoc.createTextNode(results.getString(2)));
						upVoteNode.appendChild(xmlDoc.createTextNode(results.getString(3)));
						downVoteNode.appendChild(xmlDoc.createTextNode(results.getString(4)));
						ratingNode.appendChild(xmlDoc.createTextNode(results.getString(5)));
					}
					else {
						totalListenedNode.appendChild(xmlDoc.createTextNode("NA"));
						upVoteNode.appendChild(xmlDoc.createTextNode("NA"));
						downVoteNode.appendChild(xmlDoc.createTextNode("NA"));
						ratingNode.appendChild(xmlDoc.createTextNode("NA"));
					}
		        } finally {
		        	s.close();
		        }
				
				songElement.appendChild(totalListenedNode);
				songElement.appendChild(upVoteNode);
				songElement.appendChild(downVoteNode);
				songElement.appendChild(ratingNode);
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(xmlDoc);
		StreamResult result = new StreamResult(new File("C://Eclipse//workspaces//NuMusic//WebContent//preferredMusic.xml"));
		StreamResult result2 = new StreamResult(new File("C://Apache//tomcat//webapps//ROOT//preferredMusic.xml"));
		
		transformer.transform(source, result);
		transformer.transform(source, result2);
		
		return xmlDoc;
	}
}
