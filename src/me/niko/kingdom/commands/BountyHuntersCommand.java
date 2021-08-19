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
import me.niko.kingdom.events.EventConstants;
import me.niko.kingdom.events.bountyhunters.BountyHunters;

public class BountyHuntersCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("bountyhunters.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name> <timeInSeconds>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name> <one|two|three|four|five|six>");
			
			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "create": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1].toLowerCase();
				
				File file = new File(Kingdom.getInstance().getDataFolder(), "bountyhunters.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("bountyhunters." + name + ".name") == null) {
					yamlConfiguration.set("bountyhunters." + name + ".name", name);
					
					try {
						yamlConfiguration.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					sender.sendMessage(ChatColor.GREEN + "Created a BountyHunters event named " + name);
				} else {
					sender.sendMessage(ChatColor.RED + "This bounty hunters already exists");
					return true;
				}
				
				break;
			}
			
			case "setcapzone": {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return true;
				}
				
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <one|two|three|four|five|six>");
					return true;
				}
				
				Player player = (Player) sender;
				String name = args[1].toLowerCase();

				switch (args[2].toLowerCase()) {
				case "one":
				case "two":
				case "three":
				case "four":
				case "five":
				case "six": {
					String capzone = args[2].toLowerCase();
					File file = new File(Kingdom.getInstance().getDataFolder(), "bountyhunters.yml");
					
					if(!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
					
					if(yamlConfiguration.get("bountyhunters." + name + ".name") == null) {
						player.sendMessage(ChatColor.RED + "This bounty hunters does not exists");
					} else {
						
                        Selection selection = Kingdom.getInstance().getWorldEdit().getSelection(player);
                        
                        if(selection == null) {
                        	player.sendMessage(ChatColor.RED + "Please select both corners of the capzone with WorldEdit.");
                        	return true;
                        }
						
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".world", selection.getMinimumPoint().getWorld().getName());
                        
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".x1", selection.getMinimumPoint().getBlockX());
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".y1", selection.getMinimumPoint().getBlockY());
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".z1", selection.getMinimumPoint().getBlockZ());

						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".x2", selection.getMaximumPoint().getBlockX());
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".y2", selection.getMaximumPoint().getBlockY());
						yamlConfiguration.set("bountyhunters." + name + "." + capzone + ".z2", selection.getMaximumPoint().getBlockZ());

						player.sendMessage(ChatColor.GREEN + "BountyHunters with name " + name + " capzone " + capzone.toUpperCase() + " location has been set.");
						
						try {
							yamlConfiguration.save(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
					break;
				}
				
				default:
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <one|two|three|four|five|six>");
					break;
				}
			}
			
			case "start": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <timeInSeconds>");
					return true;
				}
				
				String name = args[1].toLowerCase();
				int time = Integer.parseInt(args[2]);
				File file = new File(Kingdom.getInstance().getDataFolder(), "bountyhunters.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("bountyhunters." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This bounty hunters does not exists");
				} else {
					BountyHunters bountyHunters = new BountyHunters(name);
					
					bountyHunters.start(time);
					sender.sendMessage(ChatColor.GREEN + "BountyHunters " + name + " started for " + time + ".");

				}
				
				break;
			}
			
			case "stop": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1];
				File file = new File(Kingdom.getInstance().getDataFolder(), "bountyhunters.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("bountyhunters." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This bountyhunters event does not exists");
				} else {
					
					if(Kingdom.getInstance().getEventConstants().getActiveBountyHunters().size() == 0) {
						sender.sendMessage(ChatColor.GREEN + "There is no active bounty hunters event.");
						return true;
					}
					
					BountyHunters bountyHunters = Kingdom.getInstance().getEventConstants().getActiveBountyHunters().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
					
					if(!bountyHunters.isActive()) {
						sender.sendMessage(ChatColor.GREEN + "This bountyhunters is not active.");
						return true;
					}
					
					bountyHunters.stop();
					sender.sendMessage(ChatColor.GREEN + "BountyHunters " + name + " has been stopped.");
				}
				
				break;
			}
			
			default:
				sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name> <one|two|three|four|five|six|seven>");
				
				break;
		}
		
		return false;
	}
}
