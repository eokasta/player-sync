package com.github.eokasta.player_sync;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class PlayerSyncBungee extends Plugin {

    @Override
    public void onEnable() {
        getProxy().registerChannel("player:sync");
        getProxy().getPluginManager().registerListener(this, new FooListener(this, getLogger()));
    }

    public void sendRequestSave(ProxiedPlayer player, ServerInfo target) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        final UUID uniqueId = player.getUniqueId();
        output.writeUTF("request-save");
        output.writeUTF(uniqueId.toString());
        output.writeUTF(target.getName());

        player.getServer().getInfo().sendData("player:sync", output.toByteArray());
    }

}
