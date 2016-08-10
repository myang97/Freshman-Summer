 package executable;

import internetFunctions.BpmTwitterScraper;
import internetFunctions.FillMusicTable;
import internetFunctions.GetYoutubeLinks;
import internetFunctions.YoutubeToMp3;

public class InitialLoad {

	public static void main(String[] args) throws Exception {
		
		// XM twitter scraper [done]
		BpmTwitterScraper.initialLoad(0, 0, 0);
		
		// XM play Count consolidater [done]
		FillMusicTable.consolidateSongInstances();
		
		// Song name to Youtube converter [done]
		GetYoutubeLinks.convertSongNames(0);
		
		// Youtube song downloader [testing]
		YoutubeToMp3.downloadSongs(0, "C:\\Eclipse\\workspaces\\NuMusic\\WebContent\\musicSongs");
		
		// Ranked playlist creator [testing]

	}

}
