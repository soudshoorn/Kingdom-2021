package me.niko.kingdom.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {
	
	public static ItemStack SELECTOR = new ItemMaker(Material.NETHER_STAR).setName("&eKingdom Selector").build();
	
	public static boolean isSimiliar(ItemStack toCompare, ItemStack stack2) {
		return toCompare != null 
				&& toCompare.getType() == stack2.getType() 
				&& toCompare.hasItemMeta()
				&& toCompare.getItemMeta().hasDisplayName()
				&& toCompare.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', stack2.getItemMeta().getDisplayName()));
	}
	
	public static List<String> translateLore(List<String> lore) {
		List<String> newLore = new ArrayList<String>();
		
		for(String line : lore) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		return newLore;
	}
}
