package me.niko.kingdom.tablist.ziggurat;

import lombok.Getter;
import me.niko.kingdom.tablist.utils.PlayerUtil;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersion;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersionHandler;
import me.niko.kingdom.tablist.ziggurat.utils.BufferedTabObject;
import me.niko.kingdom.tablist.ziggurat.utils.LegacyClientUtils;
import me.niko.kingdom.tablist.ziggurat.utils.TabColumn;
import me.niko.kingdom.tablist.ziggurat.utils.TabEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

@Getter
public class ZigguratTablist {

    private Player player;
    private Scoreboard scoreboard;

    private Set<TabEntry> currentEntries = new HashSet<>();

    public ZigguratTablist(Player player) {
        this.player = player;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        //Scoreboard
        if (Ziggurat.getInstance().isHook() && !this.player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = player.getScoreboard();
        }

        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }

        this.setup();
        Team team1 = player.getScoreboard().getTeam("\\u000181");
        if (team1 == null) {
            team1 = player.getScoreboard().registerNewTeam("\\u000181");
        }
        team1.addEntry(player.getName());
        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            Team team = loopPlayer.getScoreboard().getTeam("\\u000181");
            if (team == null) {
                team = loopPlayer.getScoreboard().registerNewTeam("\\u000181");
            }
            team.addEntry(player.getName());
            team.addEntry(loopPlayer.getName());
            team1.addEntry(loopPlayer.getName());
            team1.addEntry(player.getName());
        }
    }

    private void setup() {
        final int possibleSlots = (PlayerUtil.getPlayerVersion(player) == PlayerVersion.v1_7 ? 60 : 80);
        for (int i = 1; i <= possibleSlots; i++) {
            if (this.scoreboard == null || this.scoreboard != player.getScoreboard()) {
                continue;
            }
            final TabColumn tabColumn = TabColumn.getFromSlot(player, i);
            if (tabColumn == null) {
                continue;
            }
            TabEntry tabEntry = Ziggurat.getInstance().getImplementation().createFakePlayer(
                    this,
                    "0" + (i > 9 ? i : "0" + i) + "|Tab",
                    tabColumn,
                    tabColumn.getNumb(player, i),
                    i
            );
            
            currentEntries.add(tabEntry);
        }
    }

    public void update() {
        Set<TabEntry> previous = new HashSet<>(currentEntries);
        Set<BufferedTabObject> processedObjects;
        //Null checker for twats
        if (Ziggurat.getInstance().getAdapter().getSlots(player) == null) {
            processedObjects = new HashSet<>();
        }else {
            processedObjects = Ziggurat.getInstance().getAdapter().getSlots(player);
        }
        for (BufferedTabObject scoreObject : processedObjects) {
            TabEntry tabEntry = getEntry(scoreObject.getColumn(), scoreObject.getSlot());
            if (tabEntry != null) {
                previous.remove(tabEntry);
                Ziggurat.getInstance().getImplementation().updateFakeName(this, tabEntry, scoreObject.getText());
                Ziggurat.getInstance().getImplementation().updateFakeLatency(this, tabEntry, scoreObject.getPing());
                if (PlayerUtil.getPlayerVersion(player) != PlayerVersion.v1_7) {
                    if (!tabEntry.getTexture().toString().equals(scoreObject.getSkinTexture().toString())) {
                        Ziggurat.getInstance().getImplementation().updateFakeSkin(this, tabEntry, scoreObject.getSkinTexture());
                    }
                }
            }
        }
        for (TabEntry tabEntry : previous) {
            Ziggurat.getInstance().getImplementation().updateFakeName(this, tabEntry, "");
            Ziggurat.getInstance().getImplementation().updateFakeLatency(this, tabEntry, 0);
            if (PlayerUtil.getPlayerVersion(player) != PlayerVersion.v1_7) {
                Ziggurat.getInstance().getImplementation().updateFakeSkin(this, tabEntry, ZigguratCommons.defaultTexture);
            }
        }
        previous.clear();
    }

    public TabEntry getEntry(TabColumn column, Integer slot){
        for (TabEntry entry : currentEntries){
            if (entry.getColumn().name().equalsIgnoreCase(column.name()) && entry.getSlot() == slot){
                return entry;
            }
        }
        return null;
    }

    public static String[] splitStrings(String text, int rawSlot) {
        if (text.length() > 16) {
            String prefix = text.substring(0, 16);
            String suffix;

            if (prefix.charAt(15) == ChatColor.COLOR_CHAR || prefix.charAt(15) == '&') {
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15, text.length());
            } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR || prefix.charAt(14) == '&') {
                prefix = prefix.substring(0, 14);
                suffix = text.substring(14, text.length());
            } else {
                suffix = ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&',prefix)) + text.substring(16, text.length());
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            return new String[] {
                    prefix,
                    suffix
            };
        } else {
            return new String[] {
                    text
            };
        }
    }
}
