package com.github.eokasta.player_sync;

import com.github.eokasta.player_sync.util.EncodeItem;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerSyncPlugin extends JavaPlugin {

    private static final Logger LOGGER = LogManager.getLogger("player-sync");

    @Getter
    private DatabaseConnection database;
    @Getter
    private PlayerPropertiesDatabase playerPropertiesDatabase;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeDatabase();
        final Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel(this, "player:sync", new BungeeCordListener(this));
        messenger.registerOutgoingPluginChannel(this, "player:sync");

        Bukkit.getPluginManager().registerEvents(new FooListener(this), this);
    }

    @Override
    public void onDisable() {
        this.database.shutdown();
    }

    private void initializeDatabase() {
        final ConfigurationSection section = getConfig().getConfigurationSection("sql");
        if (section == null) {
            LOGGER.error("No SQL configuration found in config.yml");
            return;
        }

        final Properties properties = new Properties();
        properties.setProperty("class-name", section.getString("class-name"));
        properties.setProperty("host", section.getString("host"));
        properties.setProperty("port", section.getString("port"));
        properties.setProperty("database", section.getString("database"));
        properties.setProperty("user", section.getString("user"));
        properties.setProperty("password", section.getString("password"));
        properties.put("connection-pool-size", section.getInt("connection-pool-size"));

        this.database = new DatabaseConnection(properties);
        this.playerPropertiesDatabase = new PlayerPropertiesDatabase(database);
        playerPropertiesDatabase.createTable();
    }

    public CompletableFuture<SyncResponse> updateProperties(Player player) {
        final UUID uuid = player.getUniqueId();
        final PlayerProperties properties = new PlayerProperties(
              player.getUniqueId(),
              EncodeItem.encodeItems(Arrays.asList(player.getInventory().getContents())),
              player.getGameMode(),
              player.getHealth(),
              player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(),
              player.getExp(),
              player.getTotalExperience(),
              player.getLevel(),
              player.getFoodLevel(),
              player.getExhaustion(),
              player.getSaturation()
        );

        return CompletableFuture.supplyAsync(() -> {
            try {
                playerPropertiesDatabase.upsertPlayerProperties(properties);
                return SyncResponse.SUCCESS;
            } catch (SQLException e) {
                return SyncResponse.FAILURE;
            }
        });
    }

    public SyncResponse applyProperties(Player player) {
        final UUID playerUniqueId = player.getUniqueId();
        try {
            final PlayerProperties properties = playerPropertiesDatabase.getProperties(playerUniqueId);
            if (properties == null)
                return SyncResponse.NO_PROPERTIES;

            final ItemStack[] inventory = EncodeItem.decodeItems(properties.getInventory());
            player.getInventory().setContents(inventory); // apply inventory
            player.setGameMode(properties.getGameMode()); // apply game mode
            player.setHealth(properties.getHealth()); // apply health
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(properties.getMaxHealth()); // apply max health
            player.setExp(properties.getExp()); // apply exp
            player.setTotalExperience(properties.getTotalExp()); // apply total exp
            player.setLevel(properties.getLevel()); // apply level
            player.setFoodLevel(properties.getFood()); // apply food
            player.setExhaustion(properties.getExhaustion()); // apply exhaustion
            player.setSaturation(properties.getSaturation()); // apply saturation
            return SyncResponse.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.error("Failed to get properties for player " + playerUniqueId, exception);
        } catch (Exception exception) {
            LOGGER.error("Failed to apply properties for player " + playerUniqueId, exception);
        }

        return SyncResponse.FAILURE;
    }

    public static PlayerSyncPlugin getInstance() {
        return JavaPlugin.getPlugin(PlayerSyncPlugin.class);
    }

}
