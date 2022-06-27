package com.github.eokasta.player_sync;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class FooListener implements Listener {

    private final PlayerSyncBungee playerSyncBungee;
    private final Logger logger;

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final ServerConnectEvent.Reason reason = event.getReason();
        logger.info("Player " + player.getName() + " changed server. Reason: " + reason.name());
        if (reason == ServerConnectEvent.Reason.JOIN_PROXY || reason == ServerConnectEvent.Reason.PLUGIN_MESSAGE) return;

        event.setCancelled(true);
        logger.info("Sending request to save player " + player.getUniqueId());
        playerSyncBungee.sendRequestSave(player, event.getTarget());
    }

    @EventHandler
    public void onChannelMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("player:sync")) return;

        final ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        final String subChannel = input.readUTF();
        if (subChannel.equals("response-save")) {
            final UUID uniqueId = UUID.fromString(input.readUTF());
            final String serverInfoName = input.readUTF();
            final boolean success = input.readBoolean();

            final ProxyServer proxy = playerSyncBungee.getProxy();
            final ProxiedPlayer proxyPlayer = proxy.getPlayer(uniqueId);
            if (success) {
                proxyPlayer.sendMessage(new TextComponent("§aConnecting..."));
                logger.info("Player " + uniqueId + " saved to server " + serverInfoName);
                final ServerInfo serverInfo = proxy.getServerInfo(serverInfoName);
                proxyPlayer.connect(serverInfo, ServerConnectEvent.Reason.PLUGIN_MESSAGE);
            } else {
                proxyPlayer.sendMessage(new TextComponent("§cCould not save your properties!"));
                logger.info("Player " + uniqueId + " failed to save to server " + serverInfoName);
            }
        }
    }

}
