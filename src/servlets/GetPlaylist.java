package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import database.MusicDB;
import objects.Playlist;
import objects.PlaylistSong;
import tables.PlaylistTable;
import tables.YoutubeTable;

/**
 * Servlet implementation class GetPlaylist
 */
@WebServlet("/GetPlaylist")
public class GetPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPlaylist() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Playlist playlist = new Playlist();
		ArrayList<ArrayList<PlaylistSong>> songs = new ArrayList<ArrayList<PlaylistSong>>();
		int size = 0;
		
		Connection conn;
		try {
			conn = MusicDB.getConnection();
			PlaylistTable playlistTable = new PlaylistTable(conn);
			if(playlistTable.getSize() < 145){
				YoutubeTable youtubeTable = new YoutubeTable(conn);
				youtubeTable.getAllSongsToAddToPlaylistTable();
			}
		} catch (ClassNotFoundException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			for(int i = 0; i < 5; i++){
				songs = playlist.getPlaylist(4, 4, 7);
			}
			size = songs.size();
			System.out.println(size);
		} catch (ClassNotFoundException | SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			playlist.toXML(songs);
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.println(size);
	}
}
