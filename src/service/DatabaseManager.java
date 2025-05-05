package service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    // XAMPP MySQL default configuration
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP MySQL default password is empty

    private static final HikariDataSource dataSource;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            
            // Connection pool configuration
            config.setMaximumPoolSize(10); // Maximum number of connections in the pool
            config.setMinimumIdle(5);      // Minimum number of idle connections
            config.setIdleTimeout(300000); // 5 minutes
            config.setConnectionTimeout(30000); // 30 seconds
            config.setMaxLifetime(1800000);    // 30 minutes
            
            // MySQL-specific optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            System.out.println("Successfully initialized connection pool!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure you have run download_mysql_connector.bat");
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get connection from pool. Make sure:");
            System.err.println("1. XAMPP is running");
            System.err.println("2. MySQL service is started in XAMPP Control Panel");
            System.err.println("3. The banking_system database exists");
            throw e;
        }
    }

    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 
