package me.niko.kingdom.tablist.utils.serverversion.impl;


import org.bukkit.entity.Player;

import me.niko.kingdom.tablist.utils.serverversion.IServerVersion;

public class ServerVersionUnknownImpl implements IServerVersion {

    @Override
    public void clearArrowsFromPlayer(Player player) {

    }

    @Override
    public String getPlayerLanguage(Player player) {
        return "en";
    }
}
