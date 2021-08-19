package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.niko.kingdom.events.war.WarHandler;

public class WarCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/" + label + " start");
			sender.sendMessage(ChatColor.RED + "/" + label + " stop");
			sender.sendMessage(ChatColor.RED + "/" + label + " addtime <seconds>");
			sender.sendMessage(ChatColor.RED + "/" + label + " removetime <seconds>");
			
			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "start": {
				if(WarHandler.isEnabled()) {
					sender.sendMessage(ChatColor.RED + "War is already running.");
					return true;
				}
				
				WarHandler.start();
				sender.sendMessage(ChatColor.GREEN + "War has started.");
				
				break;
			}
			case "stop": {
				if(!WarHandler.isEnabled()) {
					sender.sendMessage(ChatColor.RED + "War is not running.");
					return true;
				}
				
				WarHandler.stop();
				sender.sendMessage(ChatColor.GREEN + "War has stopped.");
				
				break;
			}
			
			case "settime": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <seconds>");
					return true;
				}
				
				if(!WarHandler.isEnabled()) {
					sender.sendMessage(ChatColor.RED + "War is not running.");
					return true;
				}
				
				int time = Integer.parseInt(args[1]);
				
				WarHandler.TIME_IN_SECONDS = time;
				sender.sendMessage(ChatColor.GREEN + "(Set) WAR time is now " + WarHandler.TIME_IN_SECONDS);
			}
			
			case "addtime": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <seconds>");
					return true;
				}
				
				if(!WarHandler.isEnabled()) {
					sender.sendMessage(ChatColor.RED + "War is not running.");
					return true;
				}
				
				int time = Integer.parseInt(args[1]);
				
				WarHandler.TIME_IN_SECONDS += time;
				sender.sendMessage(ChatColor.GREEN + "(Added) WAR time is now " + WarHandler.TIME_IN_SECONDS);
			}
			
			case "removetime": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <seconds>");
					return true;
				}
				
				if(!WarHandler.isEnabled()) {
					sender.sendMessage(ChatColor.RED + "War is not running.");
					return true;
				}
				
				int time = Integer.parseInt(args[1]);
				
				WarHandler.TIME_IN_SECONDS -= time;
				sender.sendMessage(ChatColor.GREEN + "(Removed) WAR time is now " + WarHandler.TIME_IN_SECONDS);
			}
			
			default: {
				sender.sendMessage(ChatColor.RED + "/" + label + " start");
				sender.sendMessage(ChatColor.RED + "/" + label + " stop");
				sender.sendMessage(ChatColor.RED + "/" + label + " addtime <seconds>");
				sender.sendMessage(ChatColor.RED + "/" + label + " removetime <seconds>");
				
				break;
			}
		}
		return false;
	}

}
