package me.niko.kingdom.selector;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.menu.menu.Button;

public class FillButton extends Button {
	
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
		
		String prefix = "selector_menu.fill.";
		
		ItemStack stack = new ItemMaker(Material.valueOf(Kingdom.getInstance().getConfig().getString(prefix + "material")))
				.setName(ConfigUtils.getFormattedValue(prefix + "display_name"))
				.setLore(ConfigUtils.getFormattedValueList(prefix + "lore"))
				.setData(Kingdom.getInstance().getConfig().getInt(prefix + "data"))
				.setAmount(Kingdom.getInstance().getConfig().getInt(prefix + "amount"))
				.build();
		
		return stack;
	}

}
