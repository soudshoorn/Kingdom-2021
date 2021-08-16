package me.niko.kingdom.utils;

import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;

public class AttackUtils {
	
	public static boolean canAttack(Player player, Player target) {
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
		
		//If they dont have a kingdom
		if(kingdomPlayer.getKingdom() == null || kingdomTarget.getKingdom() == null) {
			return false;
		}
		
		if(kingdomPlayer.getKingdom().getName().equals(kingdomTarget.getKingdom().getName())) {
			return false;
		}
		
		
		return false;
	}
}
