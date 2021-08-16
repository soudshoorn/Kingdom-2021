package me.niko.kingdom.guilds.menu;

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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.guilds.Guild;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.pagination.PaginatedMenu;

public class MembersMenu extends PaginatedMenu {

	private Guild guild;
	
	public MembersMenu(Guild guild) {
		this.guild = guild;
	}
	
	@Override
	public String getPrePaginatedTitle(Player p0) {
		return "&8Guild Members";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<>();
		
		int i = 0;
		
		for(UUID uuid : this.guild.getMembers()) {
			String formatPlayer = guild.formatPlayer(uuid);
			
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			
			buttons.put(i++, new Button() {
				
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
					ItemStack stack = new ItemMaker(Material.SKULL_ITEM, 1, (short) (3))
							.setName(formatPlayer)
							.setLore("", (guild.getLeader().equals(player.getUniqueId()) ? "&e&lRIGHT-CLICK &eto kick this player" : ""))
							.build();
					
					SkullMeta meta = (SkullMeta) stack.getItemMeta();
					meta.setOwner(offlinePlayer.getName());
					stack.setItemMeta(meta);
					
					return stack;
				}
				
				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					if(clickType != ClickType.RIGHT) {
						return;
					}
					
					if(!guild.getLeader().equals(player.getUniqueId())) {
						return;
					}
					
					player.performCommand("guild kick " + offlinePlayer.getName());
				}
			});
		}
			
		return buttons;
	}
	
	
}
