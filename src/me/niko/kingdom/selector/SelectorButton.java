package me.niko.kingdom.selector;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.ItemStackUtils;
import me.niko.kingdom.utils.menu.menu.Button;

public class SelectorButton extends Button {

	private int slot;
	private String prefix = "";
	
	public SelectorButton(int slot) {
		this.slot = slot;
		this.prefix = "selector_menu.items." + this.slot + ".";
	}
	
	@Override
	public String getName(Player p0) {
		return null;
	}

	@Override
	public List<String> getDescription(Player p0) {
		return null;
	}

	@Override
	public Material getMaterial(Player p0) {
		return null;
	}
	
	@Override
	public ItemStack getButtonItem(Player player) {		
		ItemStack stack = new ItemMaker(Material.valueOf(Kingdom.getInstance().getConfig().getString(prefix + "material")))
				.setName(ConfigUtils.getFormattedValue(prefix + "display_name"))
				.setLore(ConfigUtils.getFormattedValueList(prefix + "lore"))
				.setData(Kingdom.getInstance().getConfig().getInt(prefix + "data"))
				.setAmount(Kingdom.getInstance().getConfig().getInt(prefix + "amount"))
				.build();
		
		return stack;
	}
	
	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		KingdomConstructor oldKingom = kingdomPlayer.getKingdom();
		
		String kingdomName = Kingdom.getInstance().getConfig().getString(prefix + "kingdom");
		KingdomConstructor kingdomConstructor = kingdomName.equals("null") ? null : new KingdomConstructor(kingdomName);
		
		kingdomPlayer.setKingdom(kingdomConstructor);
		
		if(kingdomConstructor.getSpawnLocation() != null) {
			player.teleport(kingdomConstructor.getSpawnLocation());
		}
		
		kingdomPlayer.save();
		
		KingdomHandler.addOnlinePlayer(player, kingdomPlayer.getKingdom());
		
		Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.kingdom.set.broadcast")
				.replaceAll("%player%", player.getName())
				.replaceAll("%old_kingdom%", oldKingom == null ? "None" : oldKingom.getDisplayName())
				.replaceAll("%new_kingdom%", kingdomConstructor == null ? "None" : kingdomConstructor.getDisplayName()));
		
		try {
			player.getInventory().removeItem(ItemStackUtils.SELECTOR); 
		} catch (Exception e) { }
	}

}
