 package internetFunctions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import database.MusicDB;
import tables.YoutubeTable;

public class YoutubeToMp3 {

	public static void downloadSongs(int size, String downloadDirectory) throws InterruptedException, ClassNotFoundException, SQLException, IOException {
		
		Connection conn = MusicDB.getConnection();
		YoutubeTable youtubeTable = new YoutubeTable(conn);
		ArrayList<String[]> youtubeLinks = youtubeTable.getLinks();
		String downloadDir = downloadDirectory;
		
		// Set the firefox profile settings
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.dir", downloadDir);
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.download.manager.showAlertOnComplete", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "audio/mp3");
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "audio/mpeg");		
		
		
		int j = 1;
		for(String[] youtubeLinkWithSongAndLink : youtubeLinks) {
			if(size == 0 || j > size){
				int downloadNumber = youtubeLinks.indexOf(youtubeLinkWithSongAndLink) + 1;

				// Open a firefox page to twitter search page
				WebDriver driver = new FirefoxDriver(profile);
				String url = "http://www.youtube-mp3.org/";
				driver.get(url);
		
				// Input the youtube URL
				WebElement textBox = driver.findElement(By.id("youtube-url"));
				textBox.click();
				textBox.clear();
				String youtubeLink = "http://www.youtube.com"+youtubeLinkWithSongAndLink[1];
				textBox.sendKeys(youtubeLink);
		
				WebElement convertButton = driver.findElement(By.id("submit"));
				convertButton.click();
		
				WebElement statusText = driver.findElement(By.id("status_text"));
				WebElement errorText = driver.findElement(By.id("error_text"));
		
				boolean converted = false;
				boolean error = false;
				String convertedString = "Video successfully converted to mp3";
				while (!converted) {
					// look for downloaded tag
					if (statusText.getText().equals(convertedString)) {
						converted = true;
					} else if (errorText.getText().length() > 0) {
						driver.quit();
						error = true;
						break;
					} else {
						Thread.sleep(1000);
					}
				}
		
				if(!error) {
					WebElement downloadButton = driver.findElement(By.id("dl_link"));
					List<WebElement> links = downloadButton.findElements(By.linkText("Download"));
					for (WebElement link : links) {
						if (link.isDisplayed()) {
							link.click();
						}
					}
			
					// Poll download directory to see when download is done
					File dir = new File(downloadDir);
					FilenameFilter partFilter = new FilenameFilter() {
						public boolean accept(File dir, String name) {
							String lowercaseName = name.toLowerCase();
							if (lowercaseName.endsWith(".part")) {
								return true;
							} else {
								return false;
							}
						}
					};
				
					boolean downloaded = false;
					Thread.sleep(2000);
					while(!downloaded) {
						String[] fileList = dir.list(partFilter);
						if(fileList == null || fileList.length > 0) {
							Thread.sleep(1000);
						} else {
							String name = driver.findElement(By.id("title")).getText().replace("Title: ", "");
							renameFile(name, youtubeLinkWithSongAndLink[0], downloadDir);
							System.out.println("Downloaded ("+downloadNumber+"/"+youtubeLinks.size()+")");
							downloaded = true;
						}
					}

					// close the browser
					driver.quit();
				}
			}
			j++;
		}
	}
	
	public static void renameFile(String oldName, String newName, String downloadDir) throws IOException {
		System.out.println(oldName);
		System.out.println(newName);
		
		newName = newName.replace("\\", ", ");
		newName = newName.replace(":", ", ");
		newName = newName.replace("/", ", ");
		newName = newName.replace("?", ", ");
		newName = newName.replace(">", ", ");
		newName = newName.replace("<", ", ");
		newName = newName.replace("*", ", ");
		newName = newName.replace("\"", "\"");
		newName = newName.replace("|", ", ");
		newName = newName.replace("/\\", ", ");
		newName = newName.replace("A-Trak", "A Trak");
		newName = newName.replace("Ne-Yo", "Ne Yo");
		newName = newName.replace("C-Ro", "C Ro");
		newName = newName.replace("Blink-182", "Blink 182");
		newName = newName.replace("Keys-N-Crates", "Keys N Crates");
		String[] split = newName.split("-", 2);
		String songName = split[1].trim();
		
		System.out.println(songName);
		System.out.println();
		new File(downloadDir + "\\\\" + oldName + ".mp3").renameTo(new File(downloadDir + "\\\\" + songName + ".mp3"));
	}

}
