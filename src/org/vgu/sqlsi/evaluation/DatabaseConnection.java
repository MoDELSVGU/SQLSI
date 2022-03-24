package evaluation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String URL = "jdbc:mysql://%s:%s/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static final String DEFAULT_IP = "127.0.0.1";
	private static final String DEFAULT_PORT = "3306";

	public static Connection getConnection(String scenario, String username, String password) {
		final String url = String.format(URL, DEFAULT_IP, DEFAULT_PORT, scenario);
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			return connection;
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect the database!", e);
		}
	}
}
