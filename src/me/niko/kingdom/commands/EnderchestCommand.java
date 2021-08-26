package me.niko.kingdom.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
		
		if(!player.hasPermission("kingdom.enderchest")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		if(args.length == 0) {
			new EnderchestMenu(kingdomPlayer, kingdomPlayer).openMenu(player);
			
			return true;
		}
		
		if(!player.hasPermission("kingdom.enderchest.others")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			
			return true;
		}
		
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
		
		if(offlinePlayer == null) {
			player.sendMessage(ChatColor.RED + "This player have never joined the server");
			return true;
		}
		
		KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(offlinePlayer.getUniqueId());
		
		new EnderchestMenu(kingdomTarget, kingdomPlayer).openMenu(player);
		
		player.sendMessage(ChatColor.GREEN + "Openning the enderchest of " + offlinePlayer.getName() + " with how he sees it.");
		
		return false;
	}

}
