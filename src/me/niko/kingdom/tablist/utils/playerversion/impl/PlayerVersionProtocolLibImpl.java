package me.niko.kingdom.tablist.utils.playerversion.impl;

import com.comphenix.protocol.ProtocolLibrary;

import me.niko.kingdom.tablist.utils.playerversion.IPlayerVersion;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersion;

import org.bukkit.entity.Player;

public class PlayerVersionProtocolLibImpl implements IPlayerVersion {

    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(
                ProtocolLibrary.getProtocolManager().getProtocolVersion(player)
        );
    }
}
