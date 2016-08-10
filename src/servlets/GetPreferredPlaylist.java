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

import objects.Playlist;
import objects.PlaylistSong;

/**
 * Servlet implementation class GetPreferredPlaylist
 */
@WebServlet("/GetPreferredPlaylist")
public class GetPreferredPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPreferredPlaylist() {
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
		//songs with all values taken from the user interaction with the playlist
		ArrayList<ArrayList<PlaylistSong>> songs = new ArrayList<ArrayList<PlaylistSong>>();
		//songs that will be displayed as the preferred playlist onto the website
		//must be in batches of 15
		ArrayList<ArrayList<PlaylistSong>> songsDisplayed = new ArrayList<ArrayList<PlaylistSong>>();
		songsDisplayed.add(new ArrayList<PlaylistSong>());
		int size = 0;
		try {
			songs = playlist.getPreferredPlaylist();
			size = songs.get(0).size() / 15;
			for(int i = 0; i < size * 15; i++){
				songsDisplayed.get(0).add(songs.get(0).get(i));
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			try {
				playlist.toPreferredXML(songsDisplayed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		System.out.println("finished creating YOUR playlist!");
		out.println(size);
	}
}
