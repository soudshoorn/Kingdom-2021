package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.niko.kingdom.listeners.EndExitListener;

public class SetEndExitCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a player.");
			return true;
		}
		
		Player player = (Player) sender;
		
		EndExitListener.setEndExit(player.getLocation());
		
		player.sendMessage(ChatColor.GREEN + "End exit location saved!");
		
		return false;
	}

}
