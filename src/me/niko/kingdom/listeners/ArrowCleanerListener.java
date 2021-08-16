package me.niko.kingdom.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.niko.kingdom.Kingdom;

public class ArrowCleanerListener implements Listener {
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if(event.getEntityType() == EntityType.ARROW) {
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					event.getEntity().remove();
				}
			}.runTaskLater(Kingdom.getInstance(), 1L);
		}
	}

}
