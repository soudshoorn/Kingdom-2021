package me.niko.kingdom.events.war;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.TitleAPI;

public class WarHandler {
	
	@Getter public static boolean enabled = false;
	@Getter public static BukkitTask task;
	@Getter public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	
	public static int TIME_IN_SECONDS;
	
	public static void start() {
		enabled = true;
		
		//3 Hours
		TIME_IN_SECONDS = (60 * 60) * 3;
		
		for(Player player : Bukkit.getOnlinePlayers()) {
            TitleAPI.send(player, ChatColor.RED + "OORLOG!", ChatColor.RED + "Help je land en vecht mee!", 10, 60, 10);
        }
		
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if(enabled) {
					if(TIME_IN_SECONDS > 0) {
						TIME_IN_SECONDS--;
					} else {						
						stop();
						
						cancel();
					}
				}
			}
		}.runTaskTimerAsynchronously(Kingdom.getInstance(), 20L, 20L);
	}
	
	public static void stop() {
		enabled = false;
		
		task.cancel();
		task = null;
		
		String winner = "";
        int highest = 0;
        
        for(String kingdom : kills.keySet()) {
        	if(kills.get(kingdom) > highest) {
        		winner = kingdom;
        		highest = kills.get(kingdom);
        	}
        }
       
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            TitleAPI.send(player, ChatColor.RED + "Einde!", ChatColor.RED + "De oorlog is afgelopen, " + winner + ChatColor.RED + " heeft gewonnen.", 10, 60, 10);
        }

        kills.clear();
	}
}
