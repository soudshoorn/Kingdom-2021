package me.niko.kingdom.guilds.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import lombok.Getter;
import me.niko.kingdom.guilds.Guild;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;

public class GuildMenu extends Menu {

	@Getter private Guild guild;
	
	public GuildMenu(Guild guild) {
		this.guild = guild;
	}
	
	@Override
	public String getTitle(Player player) {
		return "&8Guild Menu";
	}
	
	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		
		buttons.put(getSlot(1, 1), new Button() {
			
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
				return new ItemMaker(Material.ARROW)
						.setName("&6Information")
						.setLore(
								"&6Kingdom " + guild.getKingdom().getDisplayName(),
								"",
								"&6Leader " + guild.formatPlayer(guild.getLeader()),
								"&6Tag &f" + guild.getTag())
						.build();
			}
		});
		
		buttons.put(getSlot(3, 1), new Button() {
			
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
				
				ArrayList<String> formattedList = new ArrayList<>();
				
				for(UUID uuid : guild.getMembers()) {
					formattedList.add("&7- " + guild.formatPlayer(uuid));
				}
				
				ItemStack stack = new ItemMaker(Material.SKULL_ITEM, 1, (short) 3)
						.setName("&cMembers")
						.setLore(formattedList)
						.build(); 
				
				SkullMeta meta = (SkullMeta) stack.getItemMeta();
				
				OfflinePlayer leader = Bukkit.getOfflinePlayer(guild.getLeader());
				meta.setOwner(leader.getName());
				stack.setItemMeta(meta);
				
				return stack;
			}
			
			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				new MembersMenu(guild).openMenu(player);
			}
		});
		
		buttons.put(getSlot(5, 1), new Button() {
			
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
				return new ItemMaker(Material.DIAMOND_SWORD, 1)
						.setName("&3Stats")
						.setLore("&7Kills " + guild.getKills(),
								"&7Deaths " + guild.getDeaths(),
								"&7K/dr " + guild.getKdrFormat())
						.build();
			}
		});
		
		buttons.put(getSlot(7, 1), new Button() {
			
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
				return new ItemMaker(Material.DIAMOND, 1)
						.setName("&eInfluence Bank")
						.setLore("&7Influence " + guild.getBank())
						.build();
			}
		});
		
		return buttons;
	}
	
	@Override
	public int size(Map<Integer, Button> buttons) {
		return 3 * 9;
	}
	
}
