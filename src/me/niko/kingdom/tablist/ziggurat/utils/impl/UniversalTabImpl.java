package me.niko.kingdom.tablist.ziggurat.utils.impl;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.niko.kingdom.tablist.utils.PlayerUtil;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersion;
import me.niko.kingdom.tablist.ziggurat.ZigguratTablist;
import me.niko.kingdom.tablist.ziggurat.utils.IZigguratHelper;
import me.niko.kingdom.tablist.ziggurat.utils.SkinTexture;
import me.niko.kingdom.tablist.ziggurat.utils.TabColumn;
import me.niko.kingdom.tablist.ziggurat.utils.TabEntry;

import java.util.Map;
import java.util.UUID;

public class UniversalTabImpl implements IZigguratHelper {

    @Override
    public TabEntry createFakePlayer(ZigguratTablist zigguratTablist, String string, TabColumn column, Integer slot, Integer rawSlot) {
        final OfflinePlayer offlinePlayer = new OfflinePlayer() {
            private final UUID uuid = UUID.randomUUID();

            @Override
            public boolean isOnline() {
                return true;
            }

            @Override
            public String getName() {
                return string;
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }

            @Override
            public boolean isBanned() {
                return false;
            }

            @Override
            public void setBanned(boolean b) {

            }

            @Override
            public boolean isWhitelisted() {
                return false;
            }

            @Override
            public void setWhitelisted(boolean b) {

            }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public long getFirstPlayed() {
                return 0;
            }

            @Override
            public long getLastPlayed() {
                return 0;
            }

            /*@Override
            public long getLastLogin() {
                return 0;
            }

            @Override
            public long getLastLogout() {
                return 0;
            }*/

            @Override
            public boolean hasPlayedBefore() {
                return false;
            }

            @Override
            public Location getBedSpawnLocation() {
                return null;
            }

            @Override
            public Map<String, Object> serialize() {
                return null;
            }

            @Override
            public boolean isOp() {
                return false;
            }

            @Override
            public void setOp(boolean b) {

            }
        };
        final Player player = zigguratTablist.getPlayer();
        final PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);

        return null;
    }

    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {

    }

    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {

    }

    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {

    }

    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {

    }
}
