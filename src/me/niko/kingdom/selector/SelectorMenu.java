package me.niko.kingdom.selector;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.listeners.PlayerListeners;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.ItemStackUtils;
import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;

public class SelectorMenu extends Menu {
	
	@Override
	public String getTitle(Player player) {
		return ConfigUtils.getFormattedValue("selector_menu.title");
	}
	
	@Override
	public int size(Map<Integer, Button> buttons) {
		return 9 * Kingdom.getInstance().getConfig().getInt("selector_menu.rows");
	}
	
	@Override
	public void onClose(Player player) {
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		//if(ItemStackUtils.isSimiliar(player.getItemInHand(), SELECTOR)) {
		try {
			player.getInventory().removeItem(ItemStackUtils.SELECTOR); 
		} catch (Exception e) { }
		//}
	}
	
	@Override
	public Map<Integer, Button> getButtons(Player p0) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		
		if(Kingdom.getInstance().getConfig().getBoolean("selector_menu.fill.enabled")) {
			for(int i = 0; i < (9 * Kingdom.getInstance().getConfig().getInt("selector_menu.rows")); i++) {
				buttons.put(i, new FillButton());
			}
		}
		
		for(String slotStr : Kingdom.getInstance().getConfig().getConfigurationSection("selector_menu.items").getKeys(false)) {
			int slot = Integer.parseInt(slotStr);
			
			buttons.put(slot, new SelectorButton(slot));
		}
		
		return buttons;
	}
	
	

}
