package me.niko.kingdom.commands;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;

public class InfluenceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/" + label + " set <playerName> <amount>");
			sender.sendMessage(ChatColor.RED + "/" + label + " add <playerName> <amount>");
			sender.sendMessage(ChatColor.RED + "/" + label + " remove <playerName> <amount>");
			sender.sendMessage(ChatColor.RED + "/" + label + " check <playerName>");
			
			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "set": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName> <amount>");
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				int amount = Integer.parseInt(args[2]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
				
				kingdomTarget.setInfluence(amount);
				kingdomTarget.save();
				
				sender.sendMessage(ChatColor.GREEN + "Influence of " + target.getName() + " has been set to " + amount);
				
				break;
			}
			
			case "add": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName> <amount>");
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				int amount = Integer.parseInt(args[2]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
				
				int oldInfluence = kingdomTarget.getInfluence();
				
				kingdomTarget.setInfluence(oldInfluence + amount);
				kingdomTarget.save();
				
				sender.sendMessage(ChatColor.GREEN + "Influence of " + target.getName() + " has been modified to " + kingdomTarget.getInfluence() + " from " + oldInfluence);
				
				break;
			}
			
			case "remove": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName> <amount>");
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				int amount = Integer.parseInt(args[2]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
				
				int oldInfluence = kingdomTarget.getInfluence();
				
				kingdomTarget.setInfluence(oldInfluence - amount);
				
				if (kingdomTarget.getInfluence() < 0) {
					kingdomTarget.setInfluence(0);
				}
				
				kingdomTarget.save();
				
				sender.sendMessage(ChatColor.GREEN + "Influence of " + target.getName() + " has been modified to " + kingdomTarget.getInfluence() + " from " + oldInfluence);
				
				break;
			}
			
			case "check": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName>");
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
				
				kingdomTarget.save();
				
				sender.sendMessage(ChatColor.GREEN + "Influence of " + target.getName() + " is " + kingdomTarget.getInfluence());
				
				break;
			}

			default:
				sender.sendMessage(ChatColor.RED + "/" + label + " set <playerName> <amount>");
				sender.sendMessage(ChatColor.RED + "/" + label + " check <playerName>");
				
				break;
		}
		
		return false;
	}
	
	
}
