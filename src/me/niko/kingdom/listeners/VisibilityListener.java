package me.niko.kingdom.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.niko.kingdom.visibility.VisibilityManager;

public class VisibilityListener implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.getFrom().getBlockX() == event.getTo().getBlockX()
	            && event.getFrom().getBlockY() == event.getTo().getBlockY()
	            && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
		
		Player player = event.getPlayer();
		
		VisibilityManager.update(player);
	}

}
