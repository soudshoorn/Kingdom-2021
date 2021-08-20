package me.niko.kingdom.utils;

import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;

public class AttackUtils {
	
	public static boolean canAttack(Player player, Player target) {
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
		
		KingdomConstructor playerKingdom = KingdomHandler.getKingdom(kingdomPlayer);
		KingdomConstructor targetKingdom = KingdomHandler.getKingdom(kingdomTarget);

		
		//If they dont have a kingdom
		if(playerKingdom == null || targetKingdom == null) {
			return false;
		}
		
		if(playerKingdom.getName().equals(targetKingdom.getName())) {
			return false;
		}
		
		
		return false;
	}
}
