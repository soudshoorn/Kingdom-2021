package me.niko.kingdom.listeners;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.niko.kingdom.Kingdom;
import net.minelink.ctplus.Npc;
import net.minelink.ctplus.event.CombatLogEvent;

public class CombatLoggerListener implements Listener {
	
	public static String NPC_META = "Kingdom_Combat_Npc";
	
	@EventHandler
	public void onSpawn(CombatLogEvent event){
		UUID id = event.getPlayer().getUniqueId();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {

				Npc n = Kingdom.getInstance().getCombatTagPlus().getNpcManager().getSpawnedNpc(id);
				
				if(n == null || n.getEntity() == null) { 
					return;
				}
				
				n.getEntity().setMetadata(NPC_META, new FixedMetadataValue(Kingdom.getInstance(), id));

			}
		}.runTaskLater(Kingdom.getInstance(), 2L);
	}
}
