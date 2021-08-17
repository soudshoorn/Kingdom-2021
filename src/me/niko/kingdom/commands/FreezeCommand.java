package me.niko.kingdom.commands;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.staffmode.FreezeHandler;
import me.niko.kingdom.utils.ConfigUtils;

public class FreezeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("kingdom.freeze")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.RED + "/" + label + " <playerName>");
			
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null) {
			sender.sendMessage(ChatColor.RED + "Player named '" + args[0] + "' not found.");
			
			return true;
		}
		
		if(target.isOp() && !sender.isOp()) {
			sender.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.player_bypass_freeze"));
			
			return true;
		}
		
		if(FreezeHandler.isFrozen(target)) {
			FreezeHandler.freeze(target, false);
			
			sender.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.player_unfrozen")
					.replaceAll("%player%", target.getName()));
		} else {
			FreezeHandler.freeze(target, true);
			
			SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
			
			sender.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.player_frozen")
					.replaceAll("%player%", target.getName())
					.replaceAll("%date%", date.format(new Date())));
		}
		return false;
	}

}
