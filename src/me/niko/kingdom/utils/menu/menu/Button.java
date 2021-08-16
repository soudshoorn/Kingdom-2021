package me.niko.kingdom.utils.menu.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Joiner;

public abstract class Button {
	
	@Deprecated
	public static Button placeholder(Material material, byte data, String... title) {
		return placeholder(material, data,
				(title == null || title.length == 0) ? " " : Joiner.on(" ").join((Object[]) title));
	}

	public static Button placeholder(Material material) {
		return placeholder(material, " ");
	}

	public static Button placeholder(Material material, String title) {
		return placeholder(material, (byte) 0, title);
	}

	public static Button placeholder(Material material, byte data, String title) {
		return new Button() {
			@Override
			public String getName(Player player) {
				return title;
			}

			@Override
			public List<String> getDescription(Player player) {
				return new ArrayList<String>();
			}

			@Override
			public Material getMaterial(Player player) {
				return material;
			}

			@Override
			public byte getDamageValue(Player player) {
				return data;
			}
		};
	}

	public static Button fromItem(ItemStack item) {
		return new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				return item;
			}

			@Override
			public String getName(Player player) {
				return null;
			}

			@Override
			public List<String> getDescription(Player player) {
				return null;
			}

			@Override
			public Material getMaterial(Player player) {
				return null;
			}
		};
	}

	public abstract String getName(Player p0);

	public abstract List<String> getDescription(Player p0);

	public abstract Material getMaterial(Player p0);

	public byte getDamageValue(Player player) {
		return 0;
	}

	public void clicked(Player player, int slot, ClickType clickType) {
	}

	public boolean shouldCancel(Player player, int slot, ClickType clickType) {
		return true;
	}

	public int getAmount(Player player) {
		return 1;
	}

	public ItemStack getButtonItem(Player player) {
		ItemStack buttonItem = new ItemStack(this.getMaterial(player), this.getAmount(player),
				(short) this.getDamageValue(player));
		ItemMeta meta = buttonItem.getItemMeta();
		meta.setDisplayName(this.getName(player));
		List<String> description = this.getDescription(player);
		if (description != null) {
			meta.setLore((List) description);
		}
		buttonItem.setItemMeta(meta);
		return buttonItem;
	}

	public static void playFail(Player player) {
		player.playSound(player.getLocation(), Sound.DIG_GRASS, 20.0f, 0.1f);
	}

	public static void playSuccess(Player player) {
		player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 15.0f);
	}

	public static void playNeutral(Player player) {
		player.playSound(player.getLocation(), Sound.CLICK, 20.0f, 1.0f);
	}
}
