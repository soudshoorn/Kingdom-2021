package me.niko.kingdom.utils.menu.menu.buttons;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;

public class BackButton extends Button {
	private Menu back;

	@Override
	public Material getMaterial(Player player) {
		return Material.REDSTONE;
	}

	@Override
	public byte getDamageValue(Player player) {
		return 0;
	}

	@Override
	public String getName(Player player) {
		return "§cGo back";
	}

	@Override
	public List<String> getDescription(Player player) {
		return new ArrayList<String>();
	}

	@Override
	public void clicked(Player player, int i, ClickType clickType) {
		Button.playNeutral(player);
		this.back.openMenu(player);
	}

	@ConstructorProperties({ "back" })
	public BackButton(Menu back) {
		this.back = back;
	}
}
