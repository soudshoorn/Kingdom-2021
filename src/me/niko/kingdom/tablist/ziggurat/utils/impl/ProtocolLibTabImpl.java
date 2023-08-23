package me.niko.kingdom.tablist.ziggurat.utils.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;

import me.niko.kingdom.tablist.utils.PlayerUtil;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersion;
import me.niko.kingdom.tablist.ziggurat.ZigguratCommons;
import me.niko.kingdom.tablist.ziggurat.ZigguratTablist;
import me.niko.kingdom.tablist.ziggurat.utils.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class ProtocolLibTabImpl implements IZigguratHelper {

    public ProtocolLibTabImpl() {
    }

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

            /*public long getLastLogin() {
                return 0;
            }

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

        final PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);

        packet.getPlayerInfoAction().write(0,EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        final WrappedGameProfile profile = new WrappedGameProfile(offlinePlayer.getUniqueId(), playerVersion != PlayerVersion.v1_7  ? string : LegacyClientUtils.tabEntrys.get(rawSlot - 1) + "");

        final PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(playerVersion != PlayerVersion.v1_7 ?  "" : profile.getName()));

        if (playerVersion != PlayerVersion.v1_7) {
            playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", ZigguratCommons.defaultTexture.SKIN_VALUE, ZigguratCommons.defaultTexture.SKIN_SIGNATURE));
        }

        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));

        sendPacket(player, packet);

        return new TabEntry(string, offlinePlayer, "", zigguratTablist, ZigguratCommons.defaultTexture, column, slot, rawSlot, 0);
    }

    @Override
    public void updateFakeName(ZigguratTablist zigguratTablist, TabEntry tabEntry, String text) {
        final Player player = zigguratTablist.getPlayer();
        final PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);
        String[] newStrings = ZigguratTablist.splitStrings(text, tabEntry.getRawSlot());
        if (playerVersion == PlayerVersion.v1_7) {
            Team team = player.getScoreboard().getTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot()-1));
            if (team == null) {
                team = player.getScoreboard().registerNewTeam(LegacyClientUtils.teamNames.get(tabEntry.getRawSlot()-1));
            }
            team.setPrefix(ChatColor.translateAlternateColorCodes('&', newStrings[0]));
            if (newStrings.length > 1) {
                team.setSuffix(ChatColor.translateAlternateColorCodes('&', newStrings[1]));
            } else {
                team.setSuffix("");
            }
        } else {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
            WrappedGameProfile profile = new WrappedGameProfile(
                    tabEntry.getOfflinePlayer().getUniqueId(),
                    tabEntry.getId()
            );
            PlayerInfoData playerInfoData = new PlayerInfoData(
                    profile,
                    1,
                    EnumWrappers.NativeGameMode.SURVIVAL,
                    WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', newStrings.length > 1 ? newStrings[0] + newStrings[1] : newStrings[0]))
            );
            packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
            sendPacket(player, packet);
        }
        tabEntry.setText(text);
    }

    @Override
    public void updateFakeLatency(ZigguratTablist zigguratTablist, TabEntry tabEntry, Integer latency) {
        if (tabEntry.getLatency() == latency) {
            return;
        }

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);

        WrappedGameProfile profile = new WrappedGameProfile(
                tabEntry.getOfflinePlayer().getUniqueId(),
                tabEntry.getId()
        );

        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                latency,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', tabEntry.getText()))
        );

        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));
        sendPacket(zigguratTablist.getPlayer(), packet);
        tabEntry.setLatency(latency);
    }

    @Override
    public void updateFakeSkin(ZigguratTablist zigguratTablist, TabEntry tabEntry, SkinTexture skinTexture) {
        if (tabEntry.getTexture() == skinTexture) {
            return;
        }

        final Player player = zigguratTablist.getPlayer();
        final PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);

        WrappedGameProfile profile = new WrappedGameProfile(tabEntry.getOfflinePlayer().getUniqueId(), playerVersion != PlayerVersion.v1_7  ? tabEntry.getId() : LegacyClientUtils.tabEntrys.get(tabEntry.getRawSlot() - 1) + "");
        PlayerInfoData playerInfoData = new PlayerInfoData(profile, 1, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(playerVersion != PlayerVersion.v1_7 ?  "" : profile.getName()));

        if (playerVersion != PlayerVersion.v1_7) {
            playerInfoData.getProfile().getProperties().put("texture", new WrappedSignedProperty("textures", skinTexture.SKIN_VALUE, skinTexture.SKIN_SIGNATURE));
        }

        PacketContainer remove = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        remove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        remove.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));


        PacketContainer add = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        add.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));

        sendPacket(player, remove);
        sendPacket(player, add);

        tabEntry.setTexture(skinTexture);
    }

    @Override
    public void updateHeaderAndFooter(ZigguratTablist zigguratTablist, String header, String footer) {
        PacketContainer headerAndFooter = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        final Player player = zigguratTablist.getPlayer();
        final PlayerVersion playerVersion = PlayerUtil.getPlayerVersion(player);

        if (playerVersion != PlayerVersion.v1_7) {

            headerAndFooter.getChatComponents().write(0, WrappedChatComponent.fromText(header));
            headerAndFooter.getChatComponents().write(1, WrappedChatComponent.fromText(footer));

            sendPacket(player, headerAndFooter);
        }
    }

    private static void sendPacket(Player player, PacketContainer packetContainer){
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
