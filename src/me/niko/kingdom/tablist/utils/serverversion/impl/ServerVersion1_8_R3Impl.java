package me.niko.kingdom.tablist.utils.serverversion.impl;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.niko.kingdom.tablist.utils.serverversion.IServerVersion;

public class ServerVersion1_8_R3Impl implements IServerVersion {

    @Override
    public void clearArrowsFromPlayer(Player player) {

        ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
    }

    @Override
    public String getPlayerLanguage(Player player) {
        return ((CraftPlayer) player).getHandle().locale.substring(0, 2);
    }
}
