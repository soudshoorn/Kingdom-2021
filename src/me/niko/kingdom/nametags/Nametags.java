package me.niko.kingdom.nametags;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomColor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;

public class Nametags implements NametagAdapter {
	
	@Override
	public List<BufferedNametag> getPlate(Player player) {
		List<BufferedNametag> nametags = new ArrayList<>();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		for (Player target : Bukkit.getOnlinePlayers()) {
			
			KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
			String color = ChatColor.WHITE.toString();
			boolean friendly = KingdomHandler.isSimiliarKingdom(kingdomPlayer.getKingdom(), kingdomTarget.getKingdom());
			
			if(kingdomTarget.getKingdom() != null) {
				color = KingdomColor.fromWoolToChatColor(kingdomTarget.getKingdom().getWoolData());
			}
			
			nametags.add(new BufferedNametag(target.getName(), color, "", friendly, target));
		}
		
		return nametags;
	}
	
	@Override
	public boolean showHealthBelowName(Player player) {
		return false;
	}

}
