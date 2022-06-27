package com.github.eokasta.player_sync;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class FooListener implements Listener {

    private static final Logger LOGGER = LogManager.getLogger("player-sync");

    private final PlayerSyncPlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        switch (plugin.applyProperties(player)) {
            case SUCCESS: {
                LOGGER.info("Applied properties to player " + player.getName());
                break;
            }
            case FAILURE: {
                LOGGER.error("Failed to apply properties to player " + player.getName());
                break;
            }
            case NO_PROPERTIES: {
                LOGGER.info("No properties found for player " + player.getName());
                break;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        plugin.updateProperties(player).whenComplete((syncResponse, throwable) -> {
            if (syncResponse == SyncResponse.SUCCESS) {
                LOGGER.info("Updated properties for player " + player.getName());
            } else {
                LOGGER.info("Could not update properties for player " + player.getName());
            }
        });
    }

}
