package me.niko.kingdom.utils.menu.menu.pagination;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.niko.kingdom.utils.menu.menu.Button;

public class PageButton extends Button {
	
	private int mod;
	private PaginatedMenu menu;

	@Override
	public void clicked(Player player, int i, ClickType clickType) {
		if (clickType == ClickType.RIGHT) {
			new ViewAllPagesMenu(this.menu).openMenu(player);
			Button.playNeutral(player);
		} else if (this.hasNext(player)) {
			this.menu.modPage(player, this.mod);
			Button.playNeutral(player);
		} else {
			Button.playFail(player);
		}
	}

	private boolean hasNext(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return pg > 0 && this.menu.getPages(player) >= pg;
	}

	@Override
	public String getName(Player player) {
		if (!this.hasNext(player)) {
			return (this.mod > 0) ? "§7Last page" : "§7First page";
		}
		String str = "(§e" + (this.menu.getPage() + this.mod) + "/§e" + this.menu.getPages(player) + "§a)";
		return (this.mod > 0) ? "§a\u27f6" : "§c\u27f5";
	}

	@Override
	public List<String> getDescription(Player player) {
		return new ArrayList<String>();
	}

	@Override
	public byte getDamageValue(Player player) {
		return (byte) (this.hasNext(player) ? 11 : 7);
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.CARPET;
	}

	@ConstructorProperties({ "mod", "menu" })
	public PageButton(int mod, PaginatedMenu menu) {
		this.mod = mod;
		this.menu = menu;
	}
}
