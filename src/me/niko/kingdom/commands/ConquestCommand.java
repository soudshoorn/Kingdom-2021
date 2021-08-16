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
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.events.EventConstants;
import me.niko.kingdom.events.conquest.Conquest;

public class ConquestCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("conquest.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length <= 0) {
			sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
			sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name> <green|blue|yellow|red>");
			
			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "create": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1].toLowerCase();
				
				File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("conquest." + name + ".name") == null) {
					yamlConfiguration.set("conquest." + name + ".name", name);
					
					try {
						yamlConfiguration.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					sender.sendMessage(ChatColor.GREEN + "Created a conquest named " + name);
				} else {
					sender.sendMessage(ChatColor.RED + "This conquest already exists");
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
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <green|blue|yellow|red>");
					return true;
				}
				
				Player player = (Player) sender;
				String name = args[1].toLowerCase();

				switch (args[2].toLowerCase()) {
				case "green":
				case "blue":
				case "yellow":
				case "red": {
					String capzone = args[2].toLowerCase();
					File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
					
					if(!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
					
					if(yamlConfiguration.get("conquest." + name + ".name") == null) {
						player.sendMessage(ChatColor.RED + "This conquest does not exists");
					} else {
						
                        Selection selection = Kingdom.getInstance().getWorldEdit().getSelection(player);
                        
                        if(selection == null) {
                        	player.sendMessage(ChatColor.RED + "Please select both corners of the capzone with WorldEdit.");
                        	return true;
                        }
						
						yamlConfiguration.set("conquest." + name + "." + capzone + ".world", selection.getMinimumPoint().getWorld().getName());
                        
						yamlConfiguration.set("conquest." + name + "." + capzone + ".x1", selection.getMinimumPoint().getBlockX());
						yamlConfiguration.set("conquest." + name + "." + capzone + ".y1", selection.getMinimumPoint().getBlockY());
						yamlConfiguration.set("conquest." + name + "." + capzone + ".z1", selection.getMinimumPoint().getBlockZ());

						yamlConfiguration.set("conquest." + name + "." + capzone + ".x2", selection.getMaximumPoint().getBlockX());
						yamlConfiguration.set("conquest." + name + "." + capzone + ".y2", selection.getMaximumPoint().getBlockY());
						yamlConfiguration.set("conquest." + name + "." + capzone + ".z2", selection.getMaximumPoint().getBlockZ());

						player.sendMessage(ChatColor.GREEN + "Conquest with name " + name + " capzone " + capzone.toUpperCase() + " location has been set.");
						
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
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <green|blue|yellow|red>");
					break;
				}
				
				
			}
			
			case "start": {
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <maxPoints>");
					return true;
				}
				
				String name = args[1];
				int points = Integer.parseInt(args[2]);
				File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("conquest." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This conquest does not exists");
				} else {
					Conquest conquest = new Conquest(name);
					
					conquest.start(points);
					sender.sendMessage(ChatColor.GREEN + "Conquest " + name + " started.");

				}
				
				break;
			}
			
			case "stop": {
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name>");
					return true;
				}
				
				String name = args[1];
				File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("conquest." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This conquest does not exists");
				} else {
					
					if(Kingdom.getInstance().getEventConstants().getActiveConquests().size() == 0) {
						sender.sendMessage(ChatColor.GREEN + "There is no active conquest.");
						return true;
					}
					
					Conquest conquest = Kingdom.getInstance().getEventConstants().getActiveConquests().stream().filter(c-> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
					
					if(!conquest.isActive()) {
						sender.sendMessage(ChatColor.GREEN + "This conquest is not active.");
						return true;
					}
					
					conquest.stop();
					sender.sendMessage(ChatColor.GREEN + "Conquest " + name + " has been stopped.");
				}
				
				break;
			}
			
			case "setpoints": {
				if(args.length < 4) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <name> <kingdom> <points>");
					return true;
				}
				
				String name = args[1];
				
				KingdomConstructor kingdomConstructor = new KingdomConstructor(args[2]);
				
				if(!kingdomConstructor.doesExists()) {
					sender.sendMessage(ChatColor.RED + "That kingdom does not exists.");
				}
				
				int points = Integer.parseInt(args[3]);

				File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
				
				if(yamlConfiguration.get("conquest." + name + ".name") == null) {
					sender.sendMessage(ChatColor.RED + "This conquest does not exists");
				} else {
					
					if(Kingdom.getInstance().getEventConstants().getActiveConquests().size() == 0) {
						sender.sendMessage(ChatColor.GREEN + "There is no active conquest.");
						return true;
					}
					
					Conquest conquest = Kingdom.getInstance().getEventConstants().getActiveConquests().stream().filter(c-> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
					
					if(!conquest.isActive()) {
						sender.sendMessage(ChatColor.GREEN + "This conquest is not active.");
						return true;
					}
					
					//conquest.addPoints(null, , points2);
					conquest.getPoints().put(kingdomConstructor, points);
					sender.sendMessage(ChatColor.GREEN + "Conquest " + name + " updated the points for " + kingdomConstructor.getDisplayName() + " new points " + points);
				}
				
				break;
			}
			
			default:
				sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " start <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " stop <name>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " setcapzone <name> <green|blue|yellow|red>");
				
				break;
		}
		
		return false;
	}

}
