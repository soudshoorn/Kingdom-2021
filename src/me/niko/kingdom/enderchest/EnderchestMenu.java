package me.niko.kingdom.enderchest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;

public class EnderchestMenu extends Menu {

	public KingdomPlayer targetPlayer;
	public KingdomPlayer viewerPlayer;
	
	public EnderchestMenu(KingdomPlayer targetPlayer, KingdomPlayer viewerPlayer) {
		this.targetPlayer = targetPlayer;
		this.viewerPlayer = viewerPlayer;
		
		setUpdateAfterClick(false);
		setNoncancellingInventory(true);
	}
	
	@Override
	public String getTitle(Player player) {
		return "Enderchest";
	}
	
	@Override
	public void onClose(Player player) {
		ArrayList<Integer> blockedSlots = EnderchestHandler.getSlotsNotToSave(player);
				
		int index = -1;
		
		for (ItemStack stack : player.getOpenInventory().getTopInventory().getContents()) {
			index++;

			if(blockedSlots.contains(index)) {
				continue;
			}
			
			targetPlayer.getEnderchestItems().put(index, stack);
		}
		
		targetPlayer.save();
	}
	
	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		
		for (Entry<Integer, ItemStack> entry : targetPlayer.getEnderchestItems().entrySet()) {
			buttons.put(entry.getKey(), new ItemButtons(entry.getValue()));
		}
		
		ArrayList<Integer> blockedSlots = EnderchestHandler.getSlotsNotToSave(player);
				
		for (String rank : Kingdom.getInstance().getConfig().getConfigurationSection("ender_chest").getKeys(false)) {
			
			Kingdom.getInstance().getConfig().getIntegerList("ender_chest." + rank + ".slots").forEach(slot -> {
				if(blockedSlots.contains(slot)) {
					buttons.put(slot, new BlockedButton(Kingdom.getInstance().getConfig().getConfigurationSection("ender_chest." + rank)));
				}
			});
		}
		
		return buttons;
	}
	
	@Override
	public int size(Map<Integer, Button> buttons) {
		return 6 * 9;
	}

}
