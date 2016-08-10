package test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import objects.InitialSongPref;
import objects.Playlist;
import objects.PlaylistSong;
import objects.XmSong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import tables.PlaylistTable;
import tables.PrefTable;
import tables.XmTable;
import tables.YoutubeTable;
import database.MusicDB;

public class Tester {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException, ParseException, ParserConfigurationException, TransformerException {
		//removes songs to add to the playlist to use for the web
//		for(int i = 0; i < 15; i++){
//			Connection conn = MusicDB.getConnection();
//			PlaylistTable playlistTable = new PlaylistTable(conn);
//			System.out.println(playlistTable.getSize());
//			Playlist playlist = new Playlist();
//			ArrayList<PlaylistSong> songs = playlist.getPlaylist(4, 4, 7);
//			for(PlaylistSong song: songs){
//				System.out.println(song.getSongName());
//			}
//			System.out.println(playlistTable.getSize());
//			//playlist.toXML(songs);
//		}
		
//		Playlist playlist = new Playlist();
//		ArrayList<ArrayList<PlaylistSong>> songs = new ArrayList<ArrayList<PlaylistSong>>();
//		for(int i = 0; i < 3; i++){
//			songs = playlist.getPlaylist(4, 4, 7);
//		}
//		playlist.toXML(songs);
//		System.out.println("SAVED");

		//replaces the database used to get the playlists for the web (adds songs previously removed)
		Connection conn = MusicDB.getConnection();
		YoutubeTable youtubeTable = new YoutubeTable(conn);
		PlaylistTable playlistTable = new PlaylistTable(conn);
		System.out.println(playlistTable.getSize());
		youtubeTable.getAllSongsToAddToPlaylistTable();
		
		System.out.println(playlistTable.getSize());
		
		//obtains list, which is the sorted playlist by rating with the youtube table values in string array
		//this arraylist can then be used in the playlist method
//		Connection derbyConn = MusicDB.getConnection();
//		ResultSet results;
//		Statement s = null;
//		PrefTable prefTable = new PrefTable(derbyConn);
//		YoutubeTable youtubeTable = new YoutubeTable(derbyConn);
//		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
//		ArrayList<ArrayList<PlaylistSong>> playlist = new ArrayList<ArrayList<PlaylistSong>>();
//		ArrayList<String[]> list = new ArrayList<String[]>();
//		
//		for(InitialSongPref song : songs){
//			list.add(youtubeTable.getSpecificSongByName(song.getSongName()));
//		}
//		for(String[] song : list){
//			System.out.println(song[0] + " " + song[1] + " " + song[2] + " " + song[3]);
//		}
		
		/*
		for(InitialSongPref song : songs){
			System.out.println("Song: " + song.getSongName());
			System.out.println("Upvote: " + song.getUpVote() + " Downvote: " + song.getDownVote());
			System.out.println("Duration: " + song.getDuration());
			System.out.println("Rating: " + song.getRating());
			System.out.println();
		}
		*/
		
//		//gets the preferred playlist
//		Playlist playlist = new Playlist();
//		//songs with all values taken from the user interaction with the playlist
//		ArrayList<ArrayList<PlaylistSong>> songs = new ArrayList<ArrayList<PlaylistSong>>();
//		//songs that will be displayed as the preferred playlist onto the website
//		//must be in batches of 15
//		ArrayList<ArrayList<PlaylistSong>> songsDisplayed = new ArrayList<ArrayList<PlaylistSong>>();
//		songsDisplayed.add(new ArrayList<PlaylistSong>());
//		int size = 0;
//		try {
//			songs = playlist.getPreferredPlaylist();
//			size = songs.get(0).size() / 15;
//			for(int i = 0; i < size * 15; i++){
//				songsDisplayed.get(0).add(songs.get(0).get(i));
//			}
//		} catch (ClassNotFoundException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			playlist.toPreferredXML(songsDisplayed);
//		} catch (ParserConfigurationException | TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		//obtains list, which is the sorted playlist by rating with the youtube table values in string array
//		//this arraylist can then be used in the playlist method
//		Connection derbyConn = MusicDB.getConnection();
//		Statement s = derbyConn.createStatement();
//		PrefTable prefTable = new PrefTable(derbyConn);
//		//the arraylist of initialsongpref is the list of songs sorted by the rating given
//		//the values in each initialsongpref can be added to a database
//		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
//		s.executeUpdate("TRUNCATE TABLE PREF_EVENTS_DISPLAYED");
//
//		for(InitialSongPref song : songs){
//			String songName = song.getSongName().replace("amp;", "");
//			prefTable.addDisplayValues(songName, song.getTotalListenedAmount(), song.getUpVote(), song.getDownVote(), song.getRating());
//		}
		
		
//		Connection derbyConn = MusicDB.getConnection();
//		Statement s = derbyConn.createStatement();
//		PrefTable prefTable = new PrefTable(derbyConn);
//		//the arraylist of initialsongpref is the list of songs sorted by the rating given
//		//the values in each initialsongpref can be added to a database
//		ArrayList<InitialSongPref> songs = prefTable.getPlaylist();
//		System.out.println("is this working");
//		s.executeUpdate("TRUNCATE TABLE PREF_EVENTS_DISPLAYED");
//		System.out.println("is this working");
	}

}
