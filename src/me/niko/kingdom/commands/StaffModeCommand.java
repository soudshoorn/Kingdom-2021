package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.staffmode.StaffModeHandler;

public class StaffModeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players may use this.");
			return true;
		}
		
		if(!sender.hasPermission("kingdom.staffmode")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
				
		Player player = (Player) sender;
		
		if(StaffModeHandler.isInStaffMode(player)) {
			StaffModeHandler.setStaffMode(player, false);
		} else {
			StaffModeHandler.setStaffMode(player, true);
		}
		
		return false;
	}
}
