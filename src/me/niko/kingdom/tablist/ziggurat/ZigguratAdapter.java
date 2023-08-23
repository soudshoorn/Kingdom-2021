package me.niko.kingdom.tablist.ziggurat;

import org.bukkit.entity.Player;

import me.niko.kingdom.tablist.ziggurat.utils.BufferedTabObject;

import java.util.Set;

public interface ZigguratAdapter {

    Set<BufferedTabObject> getSlots(Player player);

    String getFooter();

    String getHeader();

}
