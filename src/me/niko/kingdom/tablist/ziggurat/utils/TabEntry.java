package me.niko.kingdom.tablist.ziggurat.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.tablist.ziggurat.ZigguratTablist;

import org.bukkit.OfflinePlayer;

@Getter @Setter @AllArgsConstructor
public class TabEntry {

    private String id;
    private OfflinePlayer offlinePlayer;
    private String text;
    private ZigguratTablist tab;
    private SkinTexture texture;
    private TabColumn column;
    private int slot, rawSlot, latency;

}
