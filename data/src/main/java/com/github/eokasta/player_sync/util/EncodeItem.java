package com.github.eokasta.player_sync.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EncodeItem {

    public static String encodeItems(Collection<ItemStack> items) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream bukkitArrayOutputStream =
                  new BukkitObjectOutputStream(byteArrayOutputStream);

            bukkitArrayOutputStream.writeInt(items.size());
            for (ItemStack item : items)
                bukkitArrayOutputStream.writeObject(item);

            return Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static ItemStack[] decodeItems(String encodedItems) {
        try {
            final byte[] decodedBytes = Base64Coder.decodeLines(encodedItems);
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
            final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);

            final int size = bukkitObjectInputStream.readInt();
            final ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++)
                items[i] = ((ItemStack) bukkitObjectInputStream.readObject());

            return items;
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
    }

}
