package recipeDatabase;

import java.sql.*;
import java.util.Properties;

/**
 * Handles connection to the database as well as executing MySQL
 * 
 * @author Samuel Barker
 *
 */
public class ShokuhinDatabaseConnection
{
	public static Connection getConnection(String ipAddress, int port, String username, String password)
	{
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", username);
		connectionProps.put("password", password);

		try
		{
			conn = DriverManager.getConnection("jdbc:mysql" + "://" + ipAddress + ":" + port + "/", connectionProps);
		}
		catch (SQLException e)
		{
			System.out.println("Problem establishing connection to the database!");
		}

		return conn;
	}

	public static void executeStatement(Connection con, String sql)
	{
		PreparedStatement prep = null;

		try
		{
			con.setAutoCommit(false);
			// Prepare the statement
			prep = con.prepareStatement(sql);
			// set the values here...
			// prep.setInt(1, myNumber);
			// prep.executeUpdate();
			// con.commit();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		finally
		{
			if (prep != null)
			{
				try
				{
					prep.close();
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
			}

			try
			{
				con.setAutoCommit(true);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
