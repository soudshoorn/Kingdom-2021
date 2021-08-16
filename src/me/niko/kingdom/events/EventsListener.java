package me.niko.kingdom.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.breakthecore.BreakTheCore;
import me.niko.kingdom.events.koth.Koth;

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
			KingdomConstructor kingdom = kingdomPlayer.getKingdom();
			
			if(breakTheCore.getBreaks().containsKey(kingdom)) {
				int breaks = breakTheCore.getBreaks().getOrDefault(kingdom, breakTheCore.getHealth()-1);
				
				if((breaks-1) <= 0) {
					breakTheCore.handleWinner(player, kingdom);					
					return;
				}
				
				breakTheCore.getBreaks().put(kingdom, breaks-1);
			}
		}
	}
}
