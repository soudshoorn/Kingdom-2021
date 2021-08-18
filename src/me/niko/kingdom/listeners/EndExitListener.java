package me.niko.kingdom.listeners;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.LocationUtils;

public class EndExitListener implements Listener {
	
	private static File file = new File(Kingdom.getInstance().getDataFolder(), "end_exit.yml");
	
	public static void setEndExit(Location location) {		
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		
		yamlConfiguration.set("exit", LocationUtils.fromLocToString(location));
		
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Location getEndLocation() {
		if(!file.exists()) {
			return new Location(Bukkit.getWorld("world"), 10, 80, 10);
		}
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		Location location = LocationUtils.fromStrToLocation(yamlConfiguration.getString("exit"));
		
		return location;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.getWorld().getEnvironment() != Environment.THE_END) {
			return;
		}
		
		Location from = event.getFrom();
		Location to = event.getTo();
		Location playerLocation = player.getLocation();
		
		if(playerLocation.getBlock() != null
				&& playerLocation.getBlock().getType() != Material.AIR 
				&& (playerLocation.getBlock().getType() == Material.WATER || playerLocation.getBlock().getType() == Material.STATIONARY_WATER)) {
			player.teleport(getEndLocation());
		}
	}
}
