package me.niko.kingdom.hell;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.listeners.PortalListener;
import me.niko.kingdom.utils.ConfigUtils;

public class HellHandler {
	
	@Getter private boolean enabled = false;
	
	public HellHandler() {
		Date date = new Date();
		
		if(date.getHours() >= 0 && date.getHours() < 8) {
			enabled = false;
		} else if(date.getHours() >= 8) {
			enabled = true;
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Date date = new Date();
				
				if(date.getHours() >= 0 && date.getHours() < 8) {
					enabled = false;
				} else if(date.getHours() >= 8) {
					enabled = true;
				}
				
				if(date.getHours() == 0) {
					enabled = false;
					
					if(date.getMinutes() == 0) {
						
						for(String line : ConfigUtils.getFormattedValueList("messages.tordisti.disabled")) {
							Bukkit.broadcastMessage(line);
						}
						
						//Bukkit.broadcastMessage("");
						//Bukkit.broadcastMessage(ChatColor.RED + "Tordisti has been disabled till 08:00!");
						//Bukkit.broadcastMessage(ChatColor.RED + "All the players in there have received Wither II");
						//Bukkit.broadcastMessage(ChatColor.RED + "and won't be removed till they leave.");
						//Bukkit.broadcastMessage("");

						for(Player player : Bukkit.getOnlinePlayers()) {
							if(PortalListener.isInHell(player)) {
								player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 1f, 1f);
								
								//player.sendMessage(ChatColor.RED + "Leave the Tordisti before you die from the Wither.");
								player.sendMessage(ConfigUtils.getFormattedValue("messages.tordisti.player_leave"));
								
								player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 1));
							}
						}
					}
				} else if(date.getHours() == 8) {
					enabled = true;
					
					if(date.getMinutes() == 0) {
						
						for(String line : ConfigUtils.getFormattedValueList("messages.tordisti.enabled")) {
							Bukkit.broadcastMessage(line);
						}
						
						//Bukkit.broadcastMessage("");
						//Bukkit.broadcastMessage(ChatColor.GREEN + "Tordisti has been enabled till 00:00 and will be disabled in 08:00");
						//Bukkit.broadcastMessage("");
					}
				}
				
				if(enabled && date.getHours() == 23) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(PortalListener.isInHell(player)) {
							switch(date.getMinutes()) {
								case 45:
								case 50:
								case 55:
								case 56:
								case 57:
								case 58:
								case 59: {
									//player.sendMessage(ChatColor.RED + "You have " + (60 - date.getMinutes()) + " minutes left to leave.");
									player.sendMessage(ConfigUtils.getFormattedValue("messages.tordisti.time_to_leave").replaceAll("%time%", (60 - date.getMinutes()) + ""));
									break;
								}
									
								default: {
									break;
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(Kingdom.getInstance(), 20L * 60, 20L * 60);
	}
	
	
}
