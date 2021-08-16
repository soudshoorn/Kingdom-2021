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
import me.niko.kingdom.events.koth.Koth;

public class KothCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("koth.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name> <seconds>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name>");
			
			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "create": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1].toLowerCase();
				
				File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("koth." + name + ".name") == null) {
					yamlConfiguration.set("koth." + name + ".name", name);
					
					try {
						yamlConfiguration.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					sender.sendMessage(ChatColor.GREEN + "Created a koth named " + name);
				} else {
					sender.sendMessage(ChatColor.RED + "This koth already exists");
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
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				Player player = (Player) sender;
				String name = args[1].toLowerCase();

				File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("koth." + name + ".name") == null) {
					player.sendMessage(ChatColor.RED + "This koth does not exists");
				} else {
					
                    Selection selection = Kingdom.getInstance().getWorldEdit().getSelection(player);
                    
                    if(selection == null) {
                    	player.sendMessage(ChatColor.RED + "Please select both corners of the capzone with WorldEdit.");
                    	return true;
                    }
					
					yamlConfiguration.set("koth." + name + ".cap.world", selection.getMinimumPoint().getWorld().getName());
                    
					yamlConfiguration.set("koth." + name + ".cap.x1", selection.getMinimumPoint().getBlockX());
					yamlConfiguration.set("koth." + name + ".cap.y1", selection.getMinimumPoint().getBlockY());
					yamlConfiguration.set("koth." + name + ".cap.z1", selection.getMinimumPoint().getBlockZ());

					yamlConfiguration.set("koth." + name + ".cap.x2", selection.getMaximumPoint().getBlockX());
					yamlConfiguration.set("koth." + name + ".cap.y2", selection.getMaximumPoint().getBlockY());
					yamlConfiguration.set("koth." + name + ".cap.z2", selection.getMaximumPoint().getBlockZ());

					player.sendMessage(ChatColor.GREEN + "Koth with name " + name + " capzone location has been set.");
					
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
			
			case "start": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <seconds>");
					return true;
				}
				
				String name = args[1];
				int seconds = Integer.parseInt(args[2]);
				File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("koth." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This koth does not exists");
				} else {
					Koth koth = new Koth(name);
					
					koth.start(seconds);
					sender.sendMessage(ChatColor.GREEN + "Koth " + name + " started.");

				}
				
				break;
			}
			
			case "stop": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1];
				File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("koth." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This koth does not exists");
				} else {
					if(Kingdom.getInstance().getEventConstants().getActiveKoths().size() == 0) {
						sender.sendMessage(ChatColor.GREEN + "There is no active koth.");
						return true;
					}
					
					Koth koth = Kingdom.getInstance().getEventConstants().getActiveKoths().stream().filter(c-> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
					
					if(!koth.isActive()) {
						sender.sendMessage(ChatColor.GREEN + "This koth is not active.");
						return true;
					}
					
					koth.stop();
					sender.sendMessage(ChatColor.GREEN + "Koth " + name + " has been stopped.");
				}
				
				break;
			}
			
			default:
				sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name> <seconds>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name>");
				
				break;
		}
		
		return false;
	}

}
