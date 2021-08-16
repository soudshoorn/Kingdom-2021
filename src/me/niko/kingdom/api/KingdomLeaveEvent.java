package me.niko.kingdom.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.niko.kingdom.data.KingdomConstructor;

public class KingdomLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	@Getter private Player player;
	@Getter private KingdomConstructor kingdom;
	
	public KingdomLeaveEvent(Player player, KingdomConstructor kingdom) {
		this.player = player;
		this.kingdom = kingdom;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
