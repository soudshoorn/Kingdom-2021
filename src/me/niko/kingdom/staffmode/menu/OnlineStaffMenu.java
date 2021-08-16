package me.niko.kingdom.staffmode.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.niko.kingdom.staffmode.StaffModeHandler;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;

public class OnlineStaffMenu extends Menu {
	
	private ArrayList<Player> players;
	
	public OnlineStaffMenu() {
		players = new ArrayList<>(Bukkit.getOnlinePlayers());
	}
	
	@Override
	public String getTitle(Player player) {
		return StaffModeHandler.ONLINE_STAFF_TITLE;
	}
	
	@Override
	public int size(Map<Integer, Button> buttons) {		
		int staff = players.stream().filter(player -> player.hasPermission("kingdom.staff")).collect(Collectors.toList()).size();
		
		int size = (int) (Math.ceil(staff / 9) + 1);
		
		return (size * 9) > 54 ? 54 : (size * 9);
	}
	
	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		
		int index = 0;
		
		for(Player target : players) {
			if(!target.hasPermission("kingdom.staff")) {
				continue;
			}
			
			buttons.put(index++, new Button() {
				
				@Override
				public String getName(Player p0) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Material getMaterial(Player p0) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<String> getDescription(Player p0) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public ItemStack getButtonItem(Player player) {
					ItemStack stack = new ItemMaker(Material.SKULL_ITEM, 1, (short) 3)
							.setName("&e" + target.getName())
							.setHeadOwner(target.getName())
							.setLore("", "&7Click to teleport to him")
							.build();;
					
					return stack;
				}
				
				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					player.teleport(target.getLocation());
				}
			});
		}
		
		return buttons;
	}

}
