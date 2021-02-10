package mage_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CatDevil
 *
 */

public class LocalBaseConnection 
{
	public Connection conn = null;
	public Statement statmt = null;
	public ResultSet resSet = null;
		
	public Connection getConnection() throws ClassNotFoundException, SQLException
	{
		 String driver = "org.sqlite.JDBC";	  
		 String url = "jdbc:sqlite:base/Messages.db";
		        
		 Class.forName(driver);
		 conn = (Connection) DriverManager.getConnection(url);
		 statmt = ((Connection) conn).createStatement();
		 System.out.println("Connection with database <<Local database>> is stable...");
	    return conn; 
	}
		
	public void closeConn() throws Throwable
	{
		conn.close();
		statmt.close();
		resSet.close();
		System.out.println("Database's <<Local database>> connection was closed...");
	}
}