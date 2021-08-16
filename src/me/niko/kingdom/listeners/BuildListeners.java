package me.niko.kingdom.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;

public class BuildListeners implements Listener {
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		RegionManager regionM = WGBukkit.getRegionManager(player.getWorld());
		
		if((event.getBlock().getType() == Material.BEACON || event.getItemInHand().getType() == Material.LAVA_BUCKET) 
				&& player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			return;
		}
		
		for(ProtectedRegion region : regionM.getApplicableRegions(player.getLocation()).getRegions()) {
			if(region.getId().contains("eventrg_") && player.getGameMode() != GameMode.CREATIVE) {
				switch(event.getItemInHand().getType()) {
					case WATER_BUCKET:
					case WEB: {
						break;
					}
						
					default: {
						player.sendMessage(ChatColor.RED + "You can only place water buckets and cobweb here.");
						event.setCancelled(true);
						
						break;
					}
					
				}
			}
		}
		
		
		if(KingdomHandler.influenceCheck(player, 0, player.getLocation(), event.getBlock())) {
			player.sendMessage(ChatColor.RED + "You don't have enough influence to build here.");
			
			event.setCancelled(true);
		}
		
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		RegionManager regionM = WGBukkit.getRegionManager(player.getWorld());
        
        for(ProtectedRegion region : regionM.getApplicableRegions(player.getLocation()).getRegions()) {
        	if(region.getId().contains("eventrg_")) {
        		if(event.getBlock().getType() != Material.WEB && player.getGameMode() != GameMode.CREATIVE) {
            		//p.sendMessage(ChatColor.RED + "You can only pickup water buckets and break cobweb here.");
                  	event.setCancelled(true);
            	}
        	} else if(region.getId().contains("spawn_")) {
        		if(!kingdomPlayer.isKing()) {
        			event.setCancelled(true);
        		}
        	}
        }
		
		if(KingdomHandler.influenceCheck(player, 1, player.getLocation(), event.getBlock())) {
			player.sendMessage(ChatColor.RED + "You don't have enough influence to build here.");
			
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (player.getItemInHand() != null
				&& player.getItemInHand().getType() == Material.LAVA_BUCKET
				&& player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			
			player.sendMessage(ChatColor.RED + "You cannot use lava on here.");
			
			player.setItemInHand(new ItemStack(Material.BUCKET));
		}
		
		RegionManager regionM = WGBukkit.getRegionManager(player.getLocation().getWorld());

		if(event.getClickedBlock() == null
				|| event.getClickedBlock().getType() == null) {
			return;
		}
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		for(ProtectedRegion region : regionM.getApplicableRegions(event.getClickedBlock().getLocation()).getRegions()) {
			if(region.getId().contains("spawn_")
					&& !(event.getClickedBlock().getType() == Material.ENDER_CHEST
					|| event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE
					|| event.getClickedBlock().getType() == Material.ANVIL)
					&& !kingdomPlayer.isKing()) {
				event.setCancelled(true);
			}
		}
		
		ArrayList<Material> allowed = new ArrayList<>();

		for (String mat : Kingdom.getInstance().getConfig().getStringList("settings.use_materials")) {
			allowed.add(Material.valueOf(mat.toUpperCase()));
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && allowed.contains(event.getClickedBlock().getType())
				&& KingdomHandler.influenceCheck(player, 2, player.getLocation(), event.getClickedBlock())) {
			player.sendMessage(ChatColor.RED + "Je hebt te weinig influence hiervoor.");
			event.setCancelled(true);
		}
	}
}
