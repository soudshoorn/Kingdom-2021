package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;

public class AutoSmeltCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a player.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(!player.hasPermission("mines.autosmelt")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			
			return true;
		}
		
		if(Kingdom.getInstance().getAutoSmelting().contains(player.getUniqueId())) {
			Kingdom.getInstance().getAutoSmelting().remove(player.getUniqueId());
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Kingdom.getInstance().getConfig().getString("messages.auto_smelt.disabled")));
		} else {
			Kingdom.getInstance().getAutoSmelting().add(player.getUniqueId());
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', Kingdom.getInstance().getConfig().getString("messages.auto_smelt.enabled")));
		}
		
		return false;
	}
}