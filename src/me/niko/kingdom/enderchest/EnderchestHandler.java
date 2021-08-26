package me.niko.kingdom.enderchest;

import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;

public class EnderchestHandler {
	
	public static ArrayList<Integer> getSlotsNotToSave(Player player) {
		ArrayList<Integer> slots = new ArrayList<>();
		ConfigurationSection configurationSection = Kingdom.getInstance().getConfig().getConfigurationSection("ender_chest");
		
	 	for (String rank : configurationSection.getKeys(false)) {
	 		if(configurationSection.getString(rank + ".permission").isEmpty()) {	 			
	 			continue;
	 		}
	 		
	 		if(!player.hasPermission(configurationSection.getString(rank + ".permission"))) {
		 		slots.addAll(configurationSection.getIntegerList(rank + ".slots"));

	 			continue;
	 		}
	 	}
		
		return slots;
	}

}
