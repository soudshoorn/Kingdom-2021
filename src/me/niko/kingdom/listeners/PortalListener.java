package me.niko.kingdom.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffectType;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.LocationUtils;

public class PortalListener implements Listener {
	
	@EventHandler
	public void onPortalEnter(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		
		if(event.getCause() != TeleportCause.NETHER_PORTAL) {
			return;
		}
		
		if(isInPortal(event.getFrom(), 1)) {
			Location location = teleportToPortalExit(player, 1);

			if(location != null) {
				player.teleport(location);
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		} else if(isInPortal(event.getFrom(), 2)) {
			Location location = teleportToPortalExit(player, 2);
			
			if(location != null) {
				player.teleport(location);
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(!Kingdom.getInstance().getHell().isEnabled()
				&& isInHell(player)) {
			
			//player.sendMessage(ChatColor.RED + "You have been teleported to the normal world since Tordisti is disabled now.");
			
			player.sendMessage(ConfigUtils.getFormattedValue("messages.tordisti.teleported_to_overworld"));
			
			Location location = teleportToPortalExit(player, 2);
			
			if(location != null) {
				player.teleport(location);
			}
		}
	}
	
	@EventHandler
	public void onDrink(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		
		if(event.getItem().getType() == Material.MILK_BUCKET) {
			player.sendMessage(ChatColor.RED + "You cannot drink milk on here.");
			event.setCancelled(true);
		}
	}

	public static boolean isInHell(Player player) {
		return player.getLocation().getWorld().getName().equalsIgnoreCase("Tordisti");
	}
	
	public Location teleportToPortalExit(Player player, int number) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "demensions.yml");
		
		if(!file.exists()) {
			player.sendMessage(ChatColor.RED + "Send this to an admin.");
			return null;
		}
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		if(yamlConfiguration.get("portal." + number + ".tp_location") == null) {
			player.sendMessage(ChatColor.RED + "Send this to an admin.");
			return null;
		}

		Location location = LocationUtils.fromStrToLocation(yamlConfiguration.getString("portal." + number + ".tp_location"));
		
		if(player.hasPotionEffect(PotionEffectType.WITHER) && number == 2) {
			player.removePotionEffect(PotionEffectType.WITHER);
		}
		
		if(number == 1
				&& !Kingdom.getInstance().getHell().isEnabled()) {
			player.sendMessage(ChatColor.RED + "Tordisti is currently disabled.");
			return null;
		}
		
		return location;
	}
	
	public boolean isInPortal(Player player, int number) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "demensions.yml");
		
		if(!file.exists()) {
			return false;
		}
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		if(yamlConfiguration.get("portal." + number + ".world") == null) {
			return false;
		}
		
		World world = Bukkit.getWorld(yamlConfiguration.getString("portal." + number + ".world"));
		
		if(!player.getLocation().getWorld().getName().equals(world.getName())) {
			return false;
		}
		
		Location loc1 = new Location(
				world,
				yamlConfiguration.getInt("portal." + number + ".x1"),
				yamlConfiguration.getInt("portal." + number + ".y1"),
				yamlConfiguration.getInt("portal." + number + ".z1"));
		
		Location loc2 = new Location(
				world,
				yamlConfiguration.getInt("portal." + number + ".x2"),
				yamlConfiguration.getInt("portal." + number + ".y2"),
				yamlConfiguration.getInt("portal." + number + ".z2"));
		
		return loc1.getBlockX() <= player.getLocation().getBlockX()
				&& loc2.getBlockX() >= player.getLocation().getBlockX() && loc1.getBlockY() <= player.getLocation().getBlockY()
				&& loc2.getBlockY() >= player.getLocation().getBlockY() && loc1.getBlockZ() <= player.getLocation().getBlockZ()
				&& loc2.getBlockZ() >= player.getLocation().getBlockZ();
		
	}
	
	public boolean isInPortal(Location location, int number) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "demensions.yml");
		
		if(!file.exists()) {
			return false;
		}
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		if(yamlConfiguration.get("portal." + number + ".world") == null) {
			return false;
		}
		
		World world = Bukkit.getWorld(yamlConfiguration.getString("portal." + number + ".world"));
		
		if(!location.getWorld().getName().equals(world.getName())) {
			return false;
		}
		
		Location loc1 = new Location(
				world,
				yamlConfiguration.getInt("portal." + number + ".x1"),
				yamlConfiguration.getInt("portal." + number + ".y1"),
				yamlConfiguration.getInt("portal." + number + ".z1"));
		
		Location loc2 = new Location(
				world,
				yamlConfiguration.getInt("portal." + number + ".x2"),
				yamlConfiguration.getInt("portal." + number + ".y2"),
				yamlConfiguration.getInt("portal." + number + ".z2"));
		
		return inArea(location, loc1, loc2);
		
	}
	
	public boolean inArea(Location targetLocation, Location inAreaLocation1, Location inAreaLocation2){
		if((targetLocation.getBlockX() >= inAreaLocation1.getBlockX() && targetLocation.getBlockX() <= inAreaLocation2.getBlockX()) || (targetLocation.getBlockX() <= inAreaLocation1.getBlockX() && targetLocation.getBlockX() >= inAreaLocation2.getBlockX())){
			if((targetLocation.getBlockZ() >= inAreaLocation1.getBlockZ() && targetLocation.getBlockZ() <= inAreaLocation2.getBlockZ()) || (targetLocation.getBlockZ() <= inAreaLocation1.getBlockZ() && targetLocation.getBlockZ() >= inAreaLocation2.getBlockZ())){
				return true;
			}
		}
		
	    return false;
	}
}
