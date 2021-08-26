package me.niko.kingdom.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.enderchest.EnderchestMenu;

public class EnderchestCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		
		Player player = (Player) sender;
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		new EnderchestMenu(kingdomPlayer, kingdomPlayer).openMenu(player);
		return false;
	}

}
