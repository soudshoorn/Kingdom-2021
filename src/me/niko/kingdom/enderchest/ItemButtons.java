package me.niko.kingdom.enderchest;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.utils.menu.menu.Button;

public class ItemButtons extends Button {

	private ItemStack stack;
	
	public ItemButtons(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public String getName(Player p0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDescription(Player p0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Material getMaterial(Player p0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ItemStack getButtonItem(Player player) {	
		if(this.stack == null) {
			this.stack = new ItemStack(Material.AIR);
		}
		
		return this.stack;
	}
	
	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		// TODO Auto-generated method stub
		super.clicked(player, slot, clickType);
	}
	
	@Override
	public boolean shouldCancel(Player player, int slot, ClickType clickType) {
		// TODO Auto-generated method stub
		return false;
	}
}
