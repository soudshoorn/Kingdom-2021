package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.events.breakthecore.BreakTheCore;

public class BTCCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("btc.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.GRAY + "/" + label + " start <health>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " stop");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setblock <x> <y> <z>");
			
			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "start": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <health>");
					return true;
				}
				
				BreakTheCore breakTheCore = new BreakTheCore();
				
				if(breakTheCore.isActive()) {
					sender.sendMessage(ChatColor.RED + "This break the core is already running.");
					return true;
				}
				
				breakTheCore.setHealth(Integer.parseInt(args[1]));
				breakTheCore.start();
				
				breakTheCore.save();
				
				sender.sendMessage(ChatColor.GREEN + "Started an break the core event.");
				
				break;
			}
			
			case "stop": {
				BreakTheCore breakTheCore = new BreakTheCore();
				
				if(!breakTheCore.isActive()) {
					sender.sendMessage(ChatColor.RED + "Break the core is not running.");
					return true;
				}
				
				breakTheCore.stop();
				breakTheCore.save();
				
				sender.sendMessage(ChatColor.GREEN + "Stopped the break the core event.");
				
				break;
			}
			
			case "setblock": {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only players can use this.");
					return true;
				}
				
				Player player = (Player) sender;
				
				if(args.length < 4) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <x> <y> <z>");
					return true;
				}
				
				BreakTheCore breakTheCore = new BreakTheCore();
				
				Location location = new Location(player.getWorld(), 
						Double.parseDouble(args[1]), 
						Double.parseDouble(args[2]), 
						Double.parseDouble(args[3]));
				
				breakTheCore.setBlockLocation(location);
				breakTheCore.save();
				
				sender.sendMessage(ChatColor.GREEN + "Set the block location at x: " +  args[1] + " y:" + args[2] + " z:" + args[3]);
				
				break;
			}
			
			default:
				sender.sendMessage(ChatColor.GRAY + "/" + label + " start <health>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " stop");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setblock <x> <y> <z>");
				
				break;
		}
		
		return false;
	}

}
