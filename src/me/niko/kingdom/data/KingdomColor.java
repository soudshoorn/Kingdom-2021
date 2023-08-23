package me.niko.kingdom.data;

import org.bukkit.ChatColor;

public class KingdomColor {
	
	public static ChatColor fromWoolToChatColor(byte woolData) {		
		ChatColor color = ChatColor.WHITE;
		
		if(woolData == 1) {
			color = ChatColor.GOLD;
		} else if(woolData == 2) {
			color = ChatColor.LIGHT_PURPLE;
		} else if(woolData == 3) {
			color = ChatColor.AQUA;
		} else if(woolData == 4) {
			color = ChatColor.YELLOW;
		} else if(woolData == 5) {
			color = ChatColor.GREEN;
		} else if(woolData == 6) {
			color = ChatColor.LIGHT_PURPLE;
		} else if(woolData == 7) {
			color = ChatColor.DARK_GRAY;
		} else if(woolData == 8) {
			color = ChatColor.GRAY;
		} else if(woolData == 9) {
			color = ChatColor.DARK_AQUA;
		} else if(woolData == 10) {
			color = ChatColor.DARK_PURPLE;
		} else if(woolData == 11) {
			color = ChatColor.DARK_BLUE;
		} else if(woolData == 12) {
			color = ChatColor.BLACK;
		} else if(woolData == 13) {
			color = ChatColor.DARK_GREEN;
		} else if(woolData == 14) {
			color = ChatColor.RED;
		} else if(woolData == 15) {
			color = ChatColor.BLACK;
		} 
		
		return color;
	}
}
