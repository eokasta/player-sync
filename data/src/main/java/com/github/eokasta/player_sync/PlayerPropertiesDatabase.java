package com.github.eokasta.player_sync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.GameMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public record PlayerPropertiesDatabase(DatabaseConnection database) {

    private static final Logger LOGGER = LogManager.getLogger("player-sync");

    public void createTable() {
        try (final Connection connection = database.getConnection();
             final Statement statement = connection.createStatement()
        ) {
            statement.execute("CREATE TABLE IF NOT EXISTS inventories " +
                  "(player_unique_id VARCHAR(36) NOT NULL, " +
                  "inventory TEXT NOT NULL, " +
                  "gamemode VARCHAR(24) NOT NULL, " +
                  "health DOUBLE NOT NULL, " +
                  "max_health DOUBLE NOT NULL, " +
                  "exp DOUBLE NOT NULL, " +
                  "total_exp INTEGER NOT NULL, " +
                  "level INTEGER NOT NULL, " +
                  "food INTEGER NOT NULL, " +
                  "exhaustion FLOAT NOT NULL, " +
                  "saturation FLOAT NOT NULL, " +

                  "PRIMARY KEY (player_unique_id)" +
                  ");");
        } catch (SQLException exception) {
            LOGGER.error("Failed to create table", exception);
        }
    }

    public void upsertPlayerProperties(PlayerProperties properties) throws SQLException {
        try (final Connection connection = database.getConnection();
             final PreparedStatement statement = connection.prepareStatement("INSERT INTO inventories " +
                   "(player_unique_id, inventory, gamemode, health, max_health, exp, total_exp, level, food, exhaustion, saturation) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                   "inventory = ?, gamemode = ?, health = ?, max_health = ?, exp = ?, total_exp = ?, level = ?, food = ?, exhaustion = ?, saturation = ?;")
        ) {
            statement.setString(1, properties.playerUniqueId().toString());
            statement.setString(2, properties.inventory());
            statement.setString(3, properties.gameMode().name());
            statement.setDouble(4, properties.health());
            statement.setDouble(5, properties.maxHealth());
            statement.setDouble(6, properties.exp());
            statement.setInt(7, properties.totalExp());
            statement.setInt(8, properties.level());
            statement.setInt(9, properties.food());
            statement.setFloat(10, properties.exhaustion());
            statement.setFloat(11, properties.saturation());
            statement.setString(12, properties.inventory());
            statement.setString(13, properties.gameMode().name());
            statement.setDouble(14, properties.health());
            statement.setDouble(15, properties.maxHealth());
            statement.setDouble(16, properties.exp());
            statement.setInt(17, properties.totalExp());
            statement.setInt(18, properties.level());
            statement.setInt(19, properties.food());
            statement.setFloat(20, properties.exhaustion());
            statement.setFloat(21, properties.saturation());
            statement.executeUpdate();
        }
    }

    public PlayerProperties getProperties(UUID playerUniqueId) throws SQLException {
        try (final Connection connection = database.getConnection();
             final PreparedStatement statement = connection.prepareStatement(
                   "SELECT * FROM inventories WHERE player_unique_id = ?;")
        ) {
            statement.setString(1, playerUniqueId.toString());
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return new PlayerProperties(
                      playerUniqueId,
                      resultSet.getString("inventory"),
                      GameMode.valueOf(resultSet.getString("gamemode")),
                      resultSet.getDouble("health"),
                      resultSet.getDouble("max_health"),
                      resultSet.getFloat("exp"),
                      resultSet.getInt("total_exp"),
                      resultSet.getInt("level"),
                      resultSet.getInt("food"),
                      resultSet.getFloat("exhaustion"),
                      resultSet.getFloat("saturation")
                );
        }

        return null;
    }

}
