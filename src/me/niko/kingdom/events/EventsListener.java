package me.niko.kingdom.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.breakthecore.BreakTheCore;

public class EventsListener implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(Kingdom.getInstance().getEventConstants().getActiveBTCs().size() == 0) {
			return;
		}
		
		BreakTheCore breakTheCore = Kingdom.getInstance().getEventConstants().getActiveBTCs().get(0);
		
		if(breakTheCore.getBlockLocation() == null) {
			return;
		}
		
		if(breakTheCore.isActive() 
				&& event.getBlock().getLocation().equals(breakTheCore.getBlockLocation())) {
			
			event.setCancelled(true);
			
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
			KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
			
			KingdomConstructor foundKingdom = breakTheCore.getBreaks().keySet().stream()
					.filter(m -> m.getName().equals(kingdom.getName())).findFirst().orElse(null);
			
			if(foundKingdom != null) {
				int breaks = breakTheCore.getBreaks().get(foundKingdom);
				
				if((breaks-1) <= 0) {
					breakTheCore.handleWinner(player, foundKingdom);
					return;
				}
				
				breakTheCore.getBreaks().put(foundKingdom, breaks-1);
			}
		}
	}
}
