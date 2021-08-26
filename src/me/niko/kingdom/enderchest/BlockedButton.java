package me.niko.kingdom.enderchest;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.menu.menu.Button;

public class BlockedButton extends Button {

	private ConfigurationSection configurationSection;
	
	public BlockedButton(ConfigurationSection configurationSection) {
		this.configurationSection = configurationSection;
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
		
		ItemStack stack = new ItemMaker(Material.valueOf(configurationSection.getString("material")))
				.setName(configurationSection.getString("display_name"))
				.setData(configurationSection.getInt("data"))
				.setLore(configurationSection.getStringList("lore"))
				.build();
		
		return stack;
	}

}
