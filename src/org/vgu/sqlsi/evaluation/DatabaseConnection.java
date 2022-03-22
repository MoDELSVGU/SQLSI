package evaluation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://%s:%s/%s";
	private static final String DEFAULT_IP = "127.0.0.1";
	private static final String DEFAULT_PORT = "3306";
	private static final String USER = "hoang";
	private static final String PASSWORD = "123456";
	public static Connection getConnection(String scenario) {
		final String url = String.format(URL, DEFAULT_IP, DEFAULT_PORT, scenario);
		try {
			Connection connection = DriverManager.getConnection(url, USER, PASSWORD);
			return connection;
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
}
