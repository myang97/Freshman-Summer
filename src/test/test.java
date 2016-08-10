package test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import objects.XmSong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tables.XmTable;
import tables.YoutubeTable;
import database.MusicDB;

public class test {

	public static void convertSongNames() throws ClassNotFoundException, SQLException, IOException, ParseException {
		Connection conn = MusicDB.getConnection();
		XmTable musicTable = new XmTable(conn);
		YoutubeTable youtubeTable = new YoutubeTable(conn);
		ArrayList<XmSong> songs = musicTable.getSongs();
		int j = 1;

			String youtube = "https://www.youtube.com/results?search_query=Tigerlily+Nat+Dunn+-+Feel+The+Love+%28David+Gravell+Remix%29";
			String url = youtube;
			
			Document document = null;
			try {
				document = Jsoup.connect(url).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Elements currentData = document.select("h3.yt-lockup-title > a");

			//Search top 3 results for a non-list
			int results = currentData.size();
//			int results = 5;
//			if(currentData.size() < 5) {
//				results = currentData.size();
//			}
			//System.out.println("does it even get here");
			
			if(results != 0) {
				int k = 1;
				for (int i=0; i<results; i++) {
					String link = currentData.get(i).attr("href");
					String title = currentData.get(i).attr("title");
					//System.out.println(link);
					if(link.contains("list")) {
						k+=2;
						System.out.println("ERROR: First Youtube link is a list of videos");
						continue;
					} else if(link.contains("user")){
						k+=2;
						System.out.println("ERROR: First Youtube link is a user account");
						continue;
					}
					else {
						Elements playCount = document.select("ul.yt-lockup-meta-info > li");
						int stuff = Integer.parseInt(playCount.get(k).childNode(0).toString().replace(" views", "").replace(",", ""));
						//System.out.println(stuff);
						break;
					}	
				}
			}
			
			j++;
		}

}



