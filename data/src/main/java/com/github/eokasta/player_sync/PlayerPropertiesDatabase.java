package com.github.eokasta.player_sync;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.GameMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerPropertiesDatabase {

    private static final Logger LOGGER = LogManager.getLogger("player-sync");

    private final DatabaseConnection database;

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
            statement.setString(1, properties.getPlayerUniqueId().toString());
            statement.setString(2, properties.getInventory());
            statement.setString(3, properties.getGameMode().name());
            statement.setDouble(4, properties.getHealth());
            statement.setDouble(5, properties.getMaxHealth());
            statement.setDouble(6, properties.getExp());
            statement.setInt(7, properties.getTotalExp());
            statement.setInt(8, properties.getLevel());
            statement.setInt(9, properties.getFood());
            statement.setFloat(10, properties.getExhaustion());
            statement.setFloat(11, properties.getSaturation());
            statement.setString(12, properties.getInventory());
            statement.setString(13, properties.getGameMode().name());
            statement.setDouble(14, properties.getHealth());
            statement.setDouble(15, properties.getMaxHealth());
            statement.setDouble(16, properties.getExp());
            statement.setInt(17, properties.getTotalExp());
            statement.setInt(18, properties.getLevel());
            statement.setInt(19, properties.getFood());
            statement.setFloat(20, properties.getExhaustion());
            statement.setFloat(21, properties.getSaturation());
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
