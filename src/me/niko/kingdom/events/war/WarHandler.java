package me.niko.kingdom.events.war;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.lunarclient.bukkitapi.object.LCWaypoint;

import lombok.Getter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.TitleAPI;

public class WarHandler {

	@Getter public static boolean enabled = false;
	@Getter public static BukkitTask task;
	@Getter public static HashMap<String, Integer> warKills = new HashMap<String, Integer>();

	public static int TIME_IN_SECONDS;

	public static void start() {
		enabled = true;

		// 3 Hours
		TIME_IN_SECONDS = (60 * 60) * 3;
		warKills.clear();
		
		for (KingdomConstructor kingdomConstructor : KingdomHandler.getKingdoms()) {
			warKills.put(kingdomConstructor.getName(), 0);
		}

		ConfigUtils.getFormattedValueList("messages.events.war.started_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m)));

		task = new BukkitRunnable() {

			@Override
			public void run() {
				if (enabled) {
					if (TIME_IN_SECONDS > 0) {
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

		for (String kingdom : warKills.keySet()) {
			if (warKills.get(kingdom) > highest) {
				winner = kingdom;
				highest = warKills.get(kingdom);
			}
		}
		
		int position = 0; 
		
		Set<Entry<String, Integer>> set = warKills.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<>(set);
        
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
		
		for(String line : ConfigUtils.getFormattedValueList("messages.events.war.ended_broadcast")) {
			if(line.contains("%format%")) {
				for(Entry<String, Integer> entry : list) {
					position++;
					
					KingdomConstructor kingdom = new KingdomConstructor(entry.getKey());
					int kills = entry.getValue();
										
					Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.events.war.winners_format")
							.replaceAll("%position%", position + "")
							.replaceAll("%kingdom%", kingdom.getDisplayName())
							.replaceAll("%kills%", kills + ""));
				}
				
				continue;
			}
			
			KingdomConstructor kingdom = new KingdomConstructor(winner);
			
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%kingdom%", kingdom.getDisplayName())));
		}

		warKills.clear();
	}
}
