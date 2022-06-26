package com.github.eokasta.player_sync;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private final HikariDataSource dataSource;

    public DatabaseConnection(Properties properties) {
        final HikariConfig hikariConfig = new HikariConfig();

//        hikariConfig.setDataSourceClassName(properties.getProperty("class-name"));
        hikariConfig.setDriverClassName(properties.getProperty("class-name"));
        hikariConfig.setJdbcUrl("jdbc:mysql://" + properties.getProperty("host") + ":" + properties.getProperty("port") + "/" + properties.getProperty("database"));
        hikariConfig.addDataSourceProperty("user", properties.getProperty("user"));
        hikariConfig.addDataSourceProperty("password", properties.getProperty("password"));
        hikariConfig.addDataSourceProperty("autoReconnect", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.setMaximumPoolSize((Integer) properties.get("connection-pool-size"));

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public void shutdown() {
        dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
