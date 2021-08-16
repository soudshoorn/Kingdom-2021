package me.niko.kingdom.staffmode;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;

public class StaffModeHandler {
	
	public static ItemStack VANISH_ON = new ItemMaker(Material.INK_SACK).setData(8).setName("&cBecome Invisible").build();
	public static ItemStack VANISH_OFF = new ItemMaker(Material.INK_SACK).setData(10).setName("&cBecome Visible").build();
	public static ItemStack RANDOM_TELEPORT = new ItemMaker(Material.STICK).setName("&cRandom Teleport").build();
	public static ItemStack ONLINE_STAFF = new ItemMaker(Material.SKULL_ITEM, 1, (short) 3).setName("&cOnline Staff").setHeadOwner("foodar").build();
	public static ItemStack FREEZE = new ItemMaker(Material.PACKED_ICE).setName("&cAim Freeze").build();
	public static ItemStack INSPECTOR = new ItemMaker(Material.BOOK).setName("&cInspect Inventory").build();

	public static String ONLINE_STAFF_TITLE = ChatColor.RED + "Online Staff Members";
	
	@Getter public static ArrayList<Player> inStaffMode = new ArrayList<>();
	@Getter public static ArrayList<Player> vanished = new ArrayList<>();
	@Getter public static HashMap<Player, ItemStack[]> inventory = new HashMap<>(); 
	@Getter public static HashMap<Player, ItemStack[]> armorInventory = new HashMap<>();
	
	public static boolean isVanished(Player player) {
		return vanished.contains(player);
	}
	
	public static boolean isInStaffMode(Player player) {
		return inStaffMode.contains(player);
	}
	
	public static void setStaffMode(Player player, boolean toggle) {
		if(toggle) {
			inventory.put(player, player.getInventory().getContents());
			armorInventory.put(player, player.getInventory().getArmorContents());
			
			inStaffMode.add(player);
			
			player.getInventory().clear();
			player.updateInventory();
			
			player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			
			player.getInventory().setItem(0, RANDOM_TELEPORT);
			player.getInventory().setItem(1, INSPECTOR);
			player.getInventory().setItem(4, ONLINE_STAFF);
			player.getInventory().setItem(7, FREEZE);
			player.getInventory().setItem(8, VANISH_OFF);

			setVisible(player, true);
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(ConfigUtils.getFormattedValue("messages.staff.mode_enabled"));
		} else {
			if(!isInStaffMode(player)) {
				return;
			}
			
			inStaffMode.remove(player);
			
			player.getInventory().clear();

			player.getInventory().setContents(inventory.get(player));
			player.getInventory().setArmorContents(armorInventory.get(player));
			player.updateInventory();
			
			setVisible(player, false);
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(ConfigUtils.getFormattedValue("messages.staff.mode_disabled"));
		}
	}
	
	public static void setVisible(Player player, boolean toggle) {
		if(toggle) {
			for(Player players : Bukkit.getOnlinePlayers()) {
				if(!players.hasPermission("kingdom.staffmode")) {
					players.hidePlayer(player);
				}
			}
			
			if(isInStaffMode(player)) {
				player.getInventory().setItem(8, VANISH_OFF);
				player.updateInventory();
			}
			
			vanished.add(player);
			player.sendMessage(ConfigUtils.getFormattedValue("messages.staff.vanish_enabled"));
		} else {
			if(!isVanished(player)) {
				return;
			}
			
			for(Player players : Bukkit.getServer().getOnlinePlayers()) {
				players.showPlayer(player);
			}
			
			if(isInStaffMode(player)) {
				player.getInventory().setItem(8, VANISH_ON);
				player.updateInventory();
			}
			
			vanished.remove(player);
			player.sendMessage(ConfigUtils.getFormattedValue("messages.staff.vanish_disabled"));
		}
	}
	
	public static Inventory getOnlineInventory() {
		Inventory inventory = Bukkit.createInventory(null, 2);
		
		return inventory;
	}
}

