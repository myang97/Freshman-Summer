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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import database.MusicDB;
import objects.InitialSongPref;
import objects.Playlist;
import tables.PrefTable;
import tables.YoutubeTable;

/**
 * Servlet implementation class AddPreferences
 */
@WebServlet("/AddPreferences")
public class AddPreferences extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddPreferences() {
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
		
		String vote = request.getParameter("vote");
		String songName = request.getParameter("songName");
		String amountListened = request.getParameter("amountListened");
		String duration = request.getParameter("duration");
		String type = request.getParameter("type");
		
		//type can only be up, down, nextSong, previousSong, nextPlaylist, previousPlaylist and click
		
		songName = songName.replace("'", "''");
		try {
			PrefTable prefTable = new PrefTable(MusicDB.getConnection());
			prefTable.addSong(vote, songName, amountListened, duration, type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
