package com.github.eokasta.player_sync;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class BungeeCordListener implements PluginMessageListener {

    private static final Logger LOGGER = LogManager.getLogger("player-sync");

    private final PlayerSyncPlugin plugin;

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (channel.equals("player:sync")) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(message);
            final String subChannel = input.readUTF();
            if (subChannel.equals("request-save")) {
                final UUID uniqueId = UUID.fromString(input.readUTF());
                final String serverInfo = input.readUTF();
                LOGGER.info("Received request to save player " + uniqueId + " to server " + serverInfo);

                final Messenger messenger = plugin.getServer().getMessenger();
                final ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF("response-save");
                output.writeUTF(uniqueId.toString());
                output.writeUTF(serverInfo);
                plugin.updateProperties(player).whenComplete((syncResponse, throwable) -> {
                    output.writeBoolean(syncResponse == SyncResponse.SUCCESS);
                    player.sendPluginMessage(plugin, "player:sync", output.toByteArray());

                    LOGGER.info("Sent response to player " + uniqueId + ": " + syncResponse.name());
                });
            }
        }
    }

}
