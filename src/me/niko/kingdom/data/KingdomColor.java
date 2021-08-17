package me.niko.kingdom.data;

import org.bukkit.ChatColor;

public class KingdomColor {
	
	public static String fromWoolToChatColor(byte woolData) {		
		String color = ChatColor.WHITE.toString();
		
		if(woolData == 1) {
			color = ChatColor.GOLD.toString();
		} else if(woolData == 2) {
			color = ChatColor.LIGHT_PURPLE.toString() + ChatColor.ITALIC;
		} else if(woolData == 3) {
			color = ChatColor.AQUA.toString();
		} else if(woolData == 4) {
			color = ChatColor.YELLOW.toString();
		} else if(woolData == 5) {
			color = ChatColor.GREEN.toString();
		} else if(woolData == 6) {
			color = ChatColor.LIGHT_PURPLE.toString();
		} else if(woolData == 7) {
			color = ChatColor.DARK_GRAY.toString();
		} else if(woolData == 8) {
			color = ChatColor.GRAY.toString();
		} else if(woolData == 9) {
			color = ChatColor.DARK_AQUA.toString();
		} else if(woolData == 10) {
			color = ChatColor.DARK_PURPLE.toString();
		} else if(woolData == 11) {
			color = ChatColor.DARK_BLUE.toString();
		} else if(woolData == 12) {
			color = ChatColor.BLACK.toString();
		} else if(woolData == 13) {
			color = ChatColor.DARK_GREEN.toString();
		} else if(woolData == 14) {
			color = ChatColor.RED.toString();
		} else if(woolData == 15) {
			color = ChatColor.BLACK.toString();
		} 
		
		return color;
	}
}
