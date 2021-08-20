package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import me.niko.kingdom.Kingdom;

public class ConfigReloadCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		long started = System.currentTimeMillis();
		
		Kingdom.getInstance().reloadConfig();
		
		sender.sendMessage(ChatColor.GREEN + "Config reloaded in " + (System.currentTimeMillis() - started) + "ms.");
		
		return false;
	}

}
