package me.niko.kingdom.tablist.utils.playerversion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import me.niko.kingdom.tablist.utils.playerversion.impl.PlayerVersionProtocolLibImpl;
import me.niko.kingdom.tablist.utils.serverversion.ServerVersionHandler;

public class PlayerVersionHandler {

    public static IPlayerVersion version;

    public PlayerVersionHandler() {
        /* Plugin Manager */
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        /* ProtocolSupport */
        /*if (pluginManager.getPlugin("ProtocolSupport") != null
                || pluginManager.getPlugin("CuckSupport") != null) {
            version = new PlayerVersionProtocolSupportImpl();
            return;
        }*/

        /* ProtocolLib */
        if (pluginManager.getPlugin("ProtocolLib") != null) {
            version = new PlayerVersionProtocolLibImpl();
            return;
        }
    }
}
