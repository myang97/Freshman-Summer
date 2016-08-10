package database;
import java.io.File;
import java.io.IOException;

public class DerbyServer {
	private static final String batchLocation = "C:/Users/thomas.m.ciesielski/Desktop/Eclipse/workspaces/Personal/Homepage/Batch Files/";
	private static final File targetDBLocation = new File("C:/Apache/derby-10.9.1.0/MyDatabases/");
	
	public void start() {
		try {
			ProcessBuilder pbStart = new ProcessBuilder("cmd.exe", "/c", 
					batchLocation+"StartDerbyServer.bat");
			pbStart.directory(targetDBLocation);
			Process startServer = pbStart.start();
			
			//Let server start
			System.out.println("Sleeping...");
			int seconds = 20;
			Thread.sleep(seconds*1000);
			System.out.println("Done sleeping");
			
				
			ProcessResultReader stdout = new ProcessResultReader(
					startServer.getInputStream(), "STDOUT");
			ProcessResultReader stderr = new ProcessResultReader(
					startServer.getInputStream(), "STDERR");
	
			stdout.start();
			stderr.start();
	
			System.out.println(stdout.toString());
			System.out.println(stderr.toString());			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public void stop() {
		try {
			ProcessBuilder pbStop = new ProcessBuilder("cmd.exe", "/c", 
			batchLocation+"StopDerbyServer.bat");
			pbStop.directory(targetDBLocation);
			Process stopServer = pbStop.start();
				
			ProcessResultReader stdout = new ProcessResultReader(
					stopServer.getInputStream(), "STDOUT");
			ProcessResultReader stderr = new ProcessResultReader(
					stopServer.getInputStream(), "STDERR");
			
			stdout.start();
			stderr.start();
			
			int exitValue = stopServer.waitFor();
			if (exitValue == 0)
				System.out.println(stdout.toString());
			else
				System.out.println(stderr.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}

}
