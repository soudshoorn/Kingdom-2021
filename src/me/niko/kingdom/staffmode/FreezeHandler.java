package me.niko.kingdom.staffmode;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.ConfigUtils;

public class FreezeHandler {
	
	public static void freeze(Player target, boolean toggle) {
		if(!toggle && target.hasMetadata("frozen_ss")) {
			target.removeMetadata("frozen_ss", Kingdom.getInstance());
			
			target.sendMessage(ChatColor.RED + "You are no longer frozen.");
		} else {
			message(target);
			
			BukkitTask task = new BukkitRunnable() {
				
				@Override
				public void run() {
					if(target.hasMetadata("frozen_ss")) {
						message(target);
					} else {
						cancel();
					}
				}
			}.runTaskTimerAsynchronously(Kingdom.getInstance(), 20 * 7, 20 * 7);
			
			target.setMetadata("frozen_ss", new FixedMetadataValue(Kingdom.getInstance(), task));
		}
	}
	
	public static boolean isFrozen(Player target) {
		return target.hasMetadata("frozen_ss");
	}
	
	public static void message(Player target) {
		
		for (String line : ConfigUtils.getFormattedValueList("messages.freeze.message")) {
			target.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		/*target.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cJe bent bevroren door een medewerker!"));
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Join onze &fwachtruimte&7 en wacht rustig totdat je gesleeptwordt!"));
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&lDiscord: &fdsc.gg/reforgedmc"));
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
		target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Je hebt 5 minuten. Uitloggen is een ban."));*/
	}
}
