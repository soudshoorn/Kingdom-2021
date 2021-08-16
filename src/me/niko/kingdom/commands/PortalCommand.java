package me.niko.kingdom.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.Selection;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.LocationUtils;

public class PortalCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("portal.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.YELLOW + "1 - The portal you need to get in to be teleported location");
			sender.sendMessage(ChatColor.YELLOW + "[setteleport 1]... same for 2.");
			sender.sendMessage("");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setportal <1/2>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setteleport <1/2>");
			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "setportal": {
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <1/2>");
					return true;
				}
				
				int number = Integer.parseInt(args[1]);
				Player player = (Player) sender;
				
				if(number == 1
						|| number == 2) {
					File file = new File(Kingdom.getInstance().getDataFolder(), "demensions.yml");
					
					if(!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
					
					Selection selection = Kingdom.getInstance().getWorldEdit().getSelection(player);
                    
                    if(selection == null) {
                    	sender.sendMessage(ChatColor.RED + "Please select the portal with WorldEdit.");
                    	return true;
                    }
					
					yamlConfiguration.set("portal." + number + ".world", selection.getMinimumPoint().getWorld().getName());
					
					yamlConfiguration.set("portal." + number + ".x1", selection.getMinimumPoint().getX());
					yamlConfiguration.set("portal." + number + ".y1", selection.getMinimumPoint().getY());
					yamlConfiguration.set("portal." + number + ".z1", selection.getMinimumPoint().getZ());
					
					yamlConfiguration.set("portal." + number + ".x2", selection.getMaximumPoint().getX());
					yamlConfiguration.set("portal." + number + ".y2", selection.getMaximumPoint().getY());
					yamlConfiguration.set("portal." + number + ".z2", selection.getMaximumPoint().getZ());
					
					try {
						yamlConfiguration.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					player.sendMessage(ChatColor.GREEN + "Portal " + number + " enter has been set.");
				} else {
					sender.sendMessage(ChatColor.RED + "The number needs to be either 1 or 2.");
					return true;
				}
				
				break;
			}
			case "setteleport": {
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <1/2>");
					return true;
				}
				
				int number = Integer.parseInt(args[1]);
				Player player = (Player) sender;
				
				if(number == 1
						|| number == 2) {
					File file = new File(Kingdom.getInstance().getDataFolder(), "demensions.yml");
					
					if(!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
					
					yamlConfiguration.set("portal." + number + ".tp_location", LocationUtils.fromLocToString(player.getLocation()));
					
					try {
						yamlConfiguration.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					player.sendMessage(ChatColor.GREEN + "Location for portal " + number + " spawn has been set at your location.");
				} else {
					sender.sendMessage(ChatColor.RED + "The number needs to be either 1 or 2.");
					return true;
				}
				
				break;
			}
			default: {
				sender.sendMessage(ChatColor.YELLOW + "1 - The portal you need to get in to be teleported location");
				sender.sendMessage(ChatColor.YELLOW + "[setteleport 1]... same for 2.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setportal <1/2>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setteleport <1/2>");
				break;
			}
		}
		
		return false;
	}
}
