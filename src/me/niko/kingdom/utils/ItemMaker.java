package me.niko.kingdom.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import lombok.Getter;
import lombok.Setter;

public class ItemMaker implements Cloneable {

	
	/*
	 * 
	 * A good made API to build an ItemStack with 1 line of code instead of 5-7 LOL;
	 * 
	 */
	
	private Material type;
	private int data;
	private int amount;
	private String title;
	private String headOwner;
	private List<String> lore;
	private Color color;
	private HashMap<Enchantment, Integer> enchantments;
	private boolean unbreakable;
	private boolean glowing;

	public ItemMaker(Material type) {
		this(type, 1);
	}

	public ItemMaker(Material type, int amount) {
		this(type, amount, 0);
	}

	public ItemMaker(Material type, int amount, int data) {
		this.lore = new ArrayList<String>();
		this.type = type;
		this.amount = amount;
		this.data = data;
		this.enchantments = new HashMap<Enchantment, Integer>();
	}

	public ItemMaker(ItemStack item) {
		this.lore = new ArrayList<String>();
		Validate.notNull((Object) item);
		this.enchantments = new HashMap<Enchantment, Integer>();
		this.type = item.getType();
		this.data = item.getDurability();
		this.amount = item.getAmount();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				this.title = item.getItemMeta().getDisplayName();
			}
			if (item.getItemMeta().hasLore()) {
				this.lore = (List<String>) item.getItemMeta().getLore();
			}
		}
		if (item.getEnchantments() != null) {
			this.enchantments.putAll(item.getEnchantments());
		}
		if (item.getType().toString().toLowerCase().contains("leather")
				&& item.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta lam = (LeatherArmorMeta) item.getItemMeta();
			this.color = lam.getColor();
		}
	}

	public ItemMaker(ItemMaker item) {
		this(item.build());
	}

	public ItemMaker setUnbreakable(boolean flag) {
		this.unbreakable = flag;
		return this;
	}

	public ItemMaker addLore(String... lore) {
		for (String s : lore) {
			this.lore.add(ChatColor.translateAlternateColorCodes('&', (s)));
		}
		return this;
	}

	public ItemMaker setBase64(String base) {
		return this;
	}

	public ItemMaker setTexture(String str) {
		return this;
	}

	public ItemMaker setData(int data) {
		this.data = data;
		return this;
	}

	public ItemMaker setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemMaker setName(String title) {
		this.title = ChatColor.translateAlternateColorCodes('&', title);
		return this;
	}

	public ItemMaker setSkullType(SkullType type) {
		Validate.notNull((Object) type);
		this.setData(type.data);
		return this;
	}
	
	public ItemMaker setGlowing(boolean bool) {
		this.glowing = bool;
		return this;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public ItemMaker setLore(List<String> list) {
		List<String> newLore = new ArrayList<String>();
		
		for(String line : list) {
			newLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		this.lore = newLore;
		return this;
	}

	public Material getType() {
		return this.type;
	}

	public ItemMaker setType(Material type) {
		this.type = type;
		return this;
	}

	public ItemMaker addEnchantment(Enchantment e, int level) {
		this.enchantments.put(e, level);
		return this;
	}
	
	public ItemMaker setEnchants(List<String> list) {	
		for(String line : list) {
			if(!line.contains(",")) {
				continue;
			}
			
			Enchantment enchant = Enchantment.getByName(line.split(",")[0].replaceAll(" ", ""));
			int level = Integer.parseInt(line.split(",")[1].replaceAll(" ", ""));
			
			addEnchantment(enchant, level);
		}
		return this;
	}

	public ItemMaker setLore(String... lore) {
		setLore(Arrays.asList(lore));
		return this;
	}

	public ItemMaker setColor(Color c) {
		if (!this.type.toString().toLowerCase().contains("leather")) {
			throw new RuntimeException("Cannot set translate of non-leather items.");
		}
		this.color = c;
		return this;
	}

	public int getAmount() {
		return amount;
	}

	public Color getColor() {
		return color;
	}

	public int getData() {
		return data;
	}

	public HashMap<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public String getTitle() {
		return title;
	}

	public String getHeadOwner() {
		return headOwner;
	}
	
	public ItemMaker setHeadOwner(String headOwner) {
		this.headOwner = headOwner;
		
		return this;
	}
	
	public ItemStack build() {
		Validate.noNullElements(new Object[] { this.type, this.data, this.amount });
		ItemStack stack = new ItemStack(this.type, this.amount, (short) this.data);
		ItemMeta im = stack.getItemMeta();
		if (this.title != null && this.title != "") {
			im.setDisplayName(this.title);
		}
		if (this.lore != null && !this.lore.isEmpty()) {
			im.setLore((List<String>) this.lore);
		}
		if (this.color != null && this.type.toString().toLowerCase().contains("leather")) {
			((LeatherArmorMeta) im).setColor(this.color);
		}
		if(this.headOwner != null && this.headOwner != "") {
			((SkullMeta) im).setOwner(this.headOwner);
		}
		
		stack.setItemMeta(im);
		if (this.enchantments != null && !this.enchantments.isEmpty()) {
			stack.addUnsafeEnchantments((Map<Enchantment, Integer>) this.enchantments);
		}
		if (this.unbreakable) {
			ItemMeta meta = stack.getItemMeta();
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
		}
		return stack;
	}

	public ItemMaker clone() {
		return new ItemMaker(this);
	}

	public enum SkullType {

		SKELETON(0), WITHER_SKELETON(1), ZOMBIE(2), PLAYER(3), CREEPER(4);

		private int data;

		private SkullType(int data) {
			this.data = data;
		}

		public int getData() {
			return this.data;
		}
	}
}
