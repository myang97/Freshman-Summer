package executable;

import internetFunctions.FillMusicTable;
import internetFunctions.GetYoutubeLinks;
import internetFunctions.YoutubeToMp3;
import tables.ControlTable;
import tables.PlaylistTable;
import tables.XmTable;
import tables.YoutubeTable;

import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import database.MusicDB;
import internetFunctions.BpmTwitterScraper;

public class IncrementalLoad {

	public static void main(String[] args) throws Exception {
		Connection conn = MusicDB.getConnection();
		ControlTable controlTable = new ControlTable(conn);
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd");
        Calendar c = Calendar.getInstance();
        c.setTime(controlTable.getNewestDate("bpm_playlist")); 
        c.add(Calendar.DATE, +1);
        String dateToStr = format.format(c.getTime());
        String[] split = dateToStr.split(" ");
        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);
        
		// XM twitter scraper [done]
		//currently the latest date obtained in the parameters are 06, 29, 2016
		//earliest was 1/1/16
		BpmTwitterScraper.initialLoad(month, day, year);
		XmTable xmTable = new XmTable(conn);
		int size = Integer.parseInt(xmTable.getSize());
		System.out.println(size);
		
		// XM play Count consolidater [done]
		FillMusicTable.consolidateSongInstances();
		
		// Song name to Youtube converter [done]
		GetYoutubeLinks.convertSongNames(size);
		
		// Youtube song downloader [not started]
		YoutubeToMp3.downloadSongs(size, "C:\\Eclipse\\workspaces\\NuMusic\\WebContent\\musicSongs");
		YoutubeToMp3.downloadSongs(size, "C:\\Apache\\tomcat\\webapps\\ROOT\\musicSongs");
		
		// Ranked playlist creator [testing]

	}

}
