package me.niko.kingdom.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class WorldListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		Entity entity = event.getEntity();

		if(entity instanceof Horse) {
			if (event.getSpawnReason() == SpawnReason.NATURAL 
					|| event.getSpawnReason() == SpawnReason.CHUNK_GEN) {
				Horse horse = (Horse) entity;
				
				if (!horse.hasMetadata("unNatural")) {
					event.setCancelled(true);
				}
			}
			
			return;
		}
		
		if(event.getSpawnReason() == SpawnReason.CUSTOM) {
			event.setCancelled(false);
			
			return;
		}
		
		event.setCancelled(true);
	}

}
