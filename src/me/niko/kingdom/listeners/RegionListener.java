package me.niko.kingdom.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;

import me.niko.kingdom.utils.ConfigUtils;

public class RegionListener implements Listener {
	
	@EventHandler
	public void onRegionEnter(RegionEnterEvent event) {
		Player player = event.getPlayer();
		
		if(event.getRegion() == null) {
			return;
		}
		
		if(event.getRegion().getId().contains("donators_") && !player.hasPermission("kingdom.region.donators")) {
			player.sendMessage(ConfigUtils.getFormattedValue("messages.donators_region"));
			event.setCancelled(true);
		}
	}

}
