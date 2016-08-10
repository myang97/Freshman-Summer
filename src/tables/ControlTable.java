package tables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlTable {
	private static Connection derbyConn = null;
	private static String tableName = "CONTROL";
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //12 hour
	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24 hour
	
	public ControlTable(Connection conn) {		
		derbyConn = conn;
	}
	
	public void update(String source, Date date) 
			throws Exception {
		
		Statement s = derbyConn.createStatement();
		
		try {
			// Update control table dates
			Date newestDate = getNewestDate(source);
			Date oldestDate = getOldestDate(source);
			
			// Check date against control table
			if(date.after(newestDate)) {
				s.executeUpdate("update "+tableName+" set NEWEST_DATE = '"+sdf.format(date)+
						"' where SOURCE = '"+source+"'");
			}
			
			if(date.before(oldestDate)) {
				s.executeUpdate("update "+tableName+" set OLDEST_DATE = '"+sdf.format(date)+
						"' where SOURCE = '"+source+"'");
			}
			
        } catch (SQLException | ParseException e) {
        	throw e;
	    } finally {
	    	s.close();
	    }
	}
	
	public Date getNewestDate (String source) throws SQLException, ParseException {
		Date newestDate = null;
		
		try {
			Statement s = derbyConn.createStatement();
			ResultSet results = s.executeQuery("select NEWEST_DATE from "+tableName+
					" where SOURCE = '"+source+"'");
			while(results.next()) {				
				String dateString = results.getString(1);
				newestDate = sdf.parse(dateString);
			}
			s.close();
        }  catch (SQLException sqle) {
        	// TODO Auto-generated catch block
        	sqle.printStackTrace();
        }
		return newestDate;
	}
	
	public Date getOldestDate (String source) throws SQLException, ParseException {
		Date oldestDate = null;
		
		try {
			Statement s = derbyConn.createStatement();
			ResultSet results = s.executeQuery("select OLDEST_DATE from "+tableName+
					" where SOURCE = '"+source+"'");
			while(results.next()) {				
				String dateString = results.getString(1);
				oldestDate = sdf.parse(dateString);
			}
			s.close();
        }  catch (SQLException sqle) {
        	// TODO Auto-generated catch block
        	sqle.printStackTrace();
        }
		return oldestDate;
	}
}
