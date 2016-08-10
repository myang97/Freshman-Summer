package internetFunctions;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import tables.GarbageTable;
import tables.ControlTable;
import tables.RawXmTable;
import database.MusicDB;

public class BpmTwitterScraper {
	
    public static void initialLoad(int startMonth, int startDay, int startYear) throws Exception {
    	// Get database connections
    	Connection conn = MusicDB.getConnection();
		RawXmTable rawXmTable = new RawXmTable(conn); 
		ControlTable controlTable = new ControlTable(conn);
		GarbageTable garbageTable = new GarbageTable(conn);
		
		// Set pre-loop variables
        String xmChannel = "bpm_playlist";
    	
    	Date startDate = null;
    	Date endDate = null;
    	Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //Date finalEndDate = sdf.parse("2011-02-04");
        
        if(startMonth == 0){
        	Scanner sc = new Scanner(System.in);
            System.out.println("This is the information for the end date (oldest date is Feb 4, 2011)");
            System.out.print("Enter month with the two digits (Ex. 01, 10, 30) : ");
            String monthInt = sc.nextLine().trim();
            
            System.out.print("Enter date with the two digits  (Ex. 01, 10, 30) : ");
            String day = sc.nextLine().trim();
            
            System.out.print("Enter year with four digits     (Ex. 1997, 2016) : ");
            String year = sc.nextLine().trim();
            
            startDate = sdf.parse(year + "-" + monthInt + "-" + day);
            sc.close();
            
            // Set startDate as the first day to check tweets, moving forwards after each day
         	// Set endDate as yesterday, today's tweets won't finish until EOD
            
            Calendar c1 = Calendar.getInstance();
            c1.setTime(new Date()); // Get today's date
            c1.add(Calendar.DATE, -30); // End day = 1 day ago
            endDate = c1.getTime();
        }
        else{
        	startDate = sdf.parse(startYear + "-" + startMonth + "-" + startDay);
        	c.setTime(startDate);
        	
        	Calendar c1 = Calendar.getInstance();
            c1.setTime(startDate); // Get start date
            c1.add(Calendar.DATE, +1); // End day = 1 day later
            endDate = c1.getTime();
        }
        
        c.setTime(startDate); // Get start date
        startDate = c.getTime();
        String[] compareEndDates = startDate.toString().split("\\s+");
        String[] endDates = endDate.toString().split("\\s+");
        System.out.println("Start from " + startDate.toString() + " and go till " + endDate.toString());
        
        // While loop scrapes 1 day of tweets at a time
        while(true) {  
        	// Need to set an interval 1 day before start date
        	Date tempEndDate = null;
            c.add(Calendar.DATE, +1);
            tempEndDate = c.getTime();            
            System.out.println(startDate.getDate());
            System.out.println("\nSearching between "+sdf.format(startDate)+" and "+sdf.format(tempEndDate));

            // Get control table dates to skip
            Date newestDate = controlTable.getNewestDate(xmChannel);
            Date oldestDate = controlTable.getOldestDate(xmChannel);
            
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            // Check for initialLoad end conditions
            if(compareEndDates[1].equals(endDates[1]) && compareEndDates[2].equals(endDates[2]) && compareEndDates[5].equals(endDates[5])){
            	System.out.println("End of twitter history reached");
            	break;
        	// if startDate is between newestDate and oldestDate skip
        	// need to be grabbing stuff older than oldestDate and newer 
        	//		than finalEndDate
            } else if(startDate.before(newestDate) &&
            		startDate.after(oldestDate)) {
            	System.out.println("Already scraped this date range");
            	//continue;
            }

            // Open a firefox page to twitter search page
            // Selenium has dependancy on firefox version
            // Selenium 2.53.0 compatible with firefox 45.0
            // https://ftp.mozilla.org/pub/firefox/releases/45.0/win64/en-US/
            WebDriver driver = new FirefoxDriver();
            boolean reachedBottom = false;
            String url = "https://twitter.com/search?f=realtime&q=from%3A"+xmChannel+
            		"%20since%3A"+sdf.format(startDate)+
            		"%20until%3A"+sdf.format(tempEndDate)+"&src=typd";
            driver.get(url);
            // Check for 1 tweet
            List<WebElement> tweets = driver.findElements(By.className("tweet"));
            int numTweets = tweets.size();
            if(numTweets <= 1) {
            	driver.quit();
            	System.out.println("    No tweets in this range");
            	continue;
            }

            // Scroll to bottom
            JavascriptExecutor javascript = (JavascriptExecutor) driver;
            int scrollCount = 0;
            while(!reachedBottom) {
            	javascript.executeScript("window.scrollTo(0, document.body.scrollHeight)", "");
            	WebElement bottom = driver.findElement(By.className("back-to-top"));            
            	
                if(bottom.isDisplayed()) {
                	reachedBottom = true;
                }
                Thread.sleep(500);
                scrollCount++;
                
                if(scrollCount == 200) {
                	break;
                }
            }
            
            // Get the full list of the day's tweets
            tweets = driver.findElements(By.className("tweet"));
            
            for(int x=0; x<tweets.size() - 1; x++) {
            	WebElement tweet = tweets.get(x);
            	
            	String song = null;
            	String artist = null;
            	String title = null;
            	Date date = null;
            	String idString = null;
            	long id = 0;
            	
            	song = tweet.findElement(By.className("tweet-text")).getText();
               	
            	
            	long dateInMs = Long.parseLong(tweet.findElement(By.className("_timestamp")).getAttribute("data-time-ms"));
            	date = new Date(dateInMs);
            	
            	idString = tweet.getAttribute("data-tweet-id");
            	id = Long.parseLong(idString);
            	
            	// Clean the garbage strings out of the tweet
            	song = cleanGarbage(song);
            	
            	// Check if the tweet is not a song
            	int atCheck = song.length() - song.replace("@", "").length();
            	int hashCheck = song.length() - song.replace("#", "").length();
            	int httpCheck = song.length() - song.replace("http:", "").length();
            	int mixCheck = song.length() - song.replace("(MIX)", "").length();
            	
            	song = song.replace("'", "''");
            	// if the song contains any flagged characters add it to garbage table
            	if(atCheck > 0 || hashCheck > 0 || httpCheck > 0 || mixCheck > 0) {
        			garbageTable.addSong(xmChannel, song, date, id);
        		// else add it to the XM song table
            	} else {
            		song = song.replace("A-Trak", "A Trak");
            		song = song.replace("Ne-Yo", "Ne Yo");
    				song = song.replace("C-Ro", "C Ro");
    				song = song.replace("Blink-182", "Blink 182");
    				song = song.replace("Keys-N-Crates", "Keys N Crates");
            		
            		String[] songSplit = song.split("-", 2);
                	artist = songSplit[0];
                	title = songSplit[1];
        			rawXmTable.addSong(xmChannel, artist, title, song, date, id);
            	}
            	
            	// update the control table		    	
            	controlTable.update(xmChannel, date);
            }
            
            // close the browser
            driver.quit();
            
            // set startDate to tempEndDate to move 1 day forwards
            startDate = tempEndDate;
            compareEndDates = startDate.toString().split("\\s+");
        }
    }
    
    
	private static String cleanGarbage(String song) {
		String split = " playing on ";
    	if(song.contains(split)) {
    		String[] split1 = song.split(split);
    		song = split1[0];
    	}
    	
    	//#bpmDebut?
		String[] garbageStrings = {"#bpmDebut", "#bpmBreaker", "#BpmBreaker", "#Debut", "#Ultra2014", "#AandBSXM"};
    	for(String garbage : garbageStrings) {
    		if(song.contains(garbage)) {
    			String garbage1 = " "+garbage+" ";
        		String garbage2 = " "+garbage;
        		String garbage3 = garbage;
        		if (song.contains(garbage1)) {
        			song = song.replace(garbage1, "");
        		} else if (song.contains(garbage2)) {
        			song = song.replace(garbage2, "");
        		} else if (song.contains(garbage3)) {
        			song = song.replace(garbage3, "");
        		} else {
        			song = "ERROR cleaning string: "+garbage;
        		}
    		}
    	}
    	
		return song;
	}
	

}