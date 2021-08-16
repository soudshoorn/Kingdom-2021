package me.niko.kingdom.utils.menu.menu;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Preconditions;

import me.niko.kingdom.Kingdom;

public abstract class Menu {
	
	private static Method openInventoryMethod;
	private ConcurrentHashMap<Integer, Button> buttons;
	private boolean autoUpdate;
	private boolean updateAfterClick;
	private boolean placeholder;
	private boolean noncancellingInventory;
	private String staticTitle;
	public static Map<String, Menu> currentlyOpenedMenus;
	public static Map<String, BukkitRunnable> checkTasks;

	private Inventory createInventory(Player player) {
		Map<Integer, Button> invButtons = this.getButtons(player);
		Inventory inv = Bukkit.createInventory((InventoryHolder) player, this.size(invButtons),
				this.getTitle(player));
		for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
			this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
			inv.setItem((int) buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
		}
		if (this.isPlaceholder()) {
			Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, new String[0]);
			for (int index = 0; index < this.size(invButtons); ++index) {
				if (invButtons.get(index) == null) {
					this.buttons.put(index, placeholder);
					inv.setItem(index, placeholder.getButtonItem(player));
				}
			}
		}
		return inv;
	}

	private static Method getOpenInventoryMethod() {
		if (Menu.openInventoryMethod == null) {
			/*try {
				(Menu.openInventoryMethod = CraftHumanEntity.class.getDeclaredMethod("openCustomInventory",
						Inventory.class, EntityPlayer.class, Integer.TYPE)).setAccessible(true);
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
			}*/

			return null;
		}
		return Menu.openInventoryMethod;
	}

	public Menu() {
		this.buttons = new ConcurrentHashMap<Integer, Button>();
		this.autoUpdate = false;
		this.updateAfterClick = true;
		this.placeholder = false;
		this.noncancellingInventory = false;
		this.staticTitle = null;
	}

	public Menu(String staticTitle) {
		this.buttons = new ConcurrentHashMap<Integer, Button>();
		this.autoUpdate = false;
		this.updateAfterClick = true;
		this.placeholder = false;
		this.noncancellingInventory = false;
		this.staticTitle = null;
		this.staticTitle = (String) Preconditions.checkNotNull((Object) staticTitle);
	}

	public void openMenu(Player player) {
		Inventory inv = this.createInventory(player);
		try {
			//getOpenInventoryMethod().invoke(player, inv, ep, 0);
			player.openInventory(inv);

			this.update(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void update(Player player) {
		cancelCheck(player);
		Menu.currentlyOpenedMenus.put(player.getName(), this);
		this.onOpen(player);
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				if (!player.isOnline()) {
					Menu.cancelCheck(player);
					Menu.currentlyOpenedMenus.remove(player.getName());
				}
				if (Menu.this.isAutoUpdate()) {
					player.getOpenInventory().getTopInventory()
							.setContents(Menu.this.createInventory(player).getContents());
				}
			}
		};
		runnable.runTaskTimer(Kingdom.getInstance(), 10L, 10L);
		Menu.checkTasks.put(player.getName(), runnable);
	}

	public static void cancelCheck(Player player) {
		if (Menu.checkTasks.containsKey(player.getName())) {
			Menu.checkTasks.remove(player.getName()).cancel();
		}
	}

	public int size(Map<Integer, Button> buttons) {
		int highest = 0;
		for (int buttonValue : buttons.keySet()) {
			if (buttonValue > highest) {
				highest = buttonValue;
			}
		}
		return (int) (Math.ceil((highest + 1) / 9.0) * 9.0);
	}

	public int getSlot(int x, int y) {
		return 9 * y + x;
	}

	public String getTitle(Player player) {
		return this.staticTitle;
	}

	public abstract Map<Integer, Button> getButtons(Player p0);

	public void onOpen(Player player) {
	}

	public void onClose(Player player) {
	}

	public ConcurrentHashMap<Integer, Button> getButtons() {
		return this.buttons;
	}

	public boolean isAutoUpdate() {
		return this.autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public boolean isUpdateAfterClick() {
		return this.updateAfterClick;
	}

	public void setUpdateAfterClick(boolean updateAfterClick) {
		this.updateAfterClick = updateAfterClick;
	}

	public boolean isPlaceholder() {
		return this.placeholder;
	}

	public void setPlaceholder(boolean placeholder) {
		this.placeholder = placeholder;
	}

	public boolean isNoncancellingInventory() {
		return this.noncancellingInventory;
	}

	public void setNoncancellingInventory(boolean noncancellingInventory) {
		this.noncancellingInventory = noncancellingInventory;
	}

	static {
		Menu.currentlyOpenedMenus = new HashMap<String, Menu>();
		Menu.checkTasks = new HashMap<String, BukkitRunnable>();
	}
}
