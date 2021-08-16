package me.niko.kingdom.utils;

import java.util.List;

import org.bukkit.ChatColor;

import me.niko.kingdom.Kingdom;

public class ConfigUtils {
	
	public static String getFormattedValue(String path) {
		return ChatColor.translateAlternateColorCodes('&', Kingdom.getInstance().getConfig().getString(path));
	}
	
	public static List<String> getFormattedValueList(String path) {
		return Kingdom.getInstance().getConfig().getStringList(path);
	}

}
