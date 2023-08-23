package me.niko.kingdom.tablist.ziggurat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class ZigguratListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        
        Ziggurat.getInstance().getTablists().put(player.getUniqueId(), new ZigguratTablist(event.getPlayer()));
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Ziggurat.getInstance().getTablists().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Ziggurat.getInstance().disable();
    }
}
