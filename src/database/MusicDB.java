package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MusicDB {
	private static final String dbName = "MusicDB";
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
        String driver = "org.apache.derby.jdbc.ClientDriver";
        Class.forName(driver);
		
		String connectionURL = "jdbc:derby://localhost:1527/" + dbName;
		Connection conn = DriverManager.getConnection(connectionURL);
		
		return conn;
	}

}
