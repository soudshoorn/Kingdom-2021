package me.niko.kingdom.listeners;

import java.util.AbstractMap.SimpleEntry;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.niko.kingdom.Kingdom;

public class MinesListeners implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		RegionManager regionM = WGBukkit.getRegionManager(player.getWorld());
            
		for(ProtectedRegion r : regionM.getApplicableRegions(player.getLocation()).getRegions()) {
			for(String Id : Kingdom.getInstance().getConfig().getStringList("mines.regions")) {
				if(r.getId().equalsIgnoreCase(Id.toLowerCase())) {
						
					if(player.getGameMode() != GameMode.CREATIVE) {
						event.setCancelled(true);
					}
						
					if(event.getBlock().getType().name().toLowerCase().contains("ore")
							|| event.getBlock().getType() == Material.LOG) {
						if(player.getGameMode() == GameMode.CREATIVE) {
							return;
						}
								
						event.setCancelled(true);
	
						Block block = event.getBlock();
															
						int count = CraftMagicNumbers.getBlock(block).getDropCount(player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), Kingdom.getInstance().getRandom());
								
						for(ItemStack stack : event.getBlock().getDrops()) {
							if(Kingdom.getInstance().getAutoSmelting().contains(player.getUniqueId())) {
								if(stack.getType() == Material.GOLD_ORE) {
									stack.setType(Material.GOLD_INGOT);
								} else if(stack.getType() == Material.IRON_ORE) {
									stack.setType(Material.IRON_INGOT);
								}
							}
									
							for(int i = 0; i < count; i++) {
								player.getInventory().addItem(stack);
							}
						}
	
						player.giveExp(event.getExpToDrop());
								
						Material oldType = block.getType();
						byte data = block.getData();
								
						Kingdom.getInstance().getBrokenBlocks().put(block, new SimpleEntry(oldType, data));
								
						block.setType(Material.BEDROCK);
								
						int seconds = Kingdom.getInstance().getConfig().getInt("mines.reset_after_seconds");
								
						if(oldType == Material.LOG) {
							seconds = 5;
						}
								
						Kingdom.getInstance().getTasks().add(new BukkitRunnable() {
							public void run() {
								block.setType(oldType);
								block.setData(data);
	
								Kingdom.getInstance().getBrokenBlocks().remove(block);
							}
						}.runTaskLater(Kingdom.getInstance(), 20 * seconds));
					}
				}
            }
		}
	}
}
