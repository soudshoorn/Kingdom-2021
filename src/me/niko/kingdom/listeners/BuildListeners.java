package me.niko.kingdom.listeners;

import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;

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
												
						BukkitTask task = new BukkitRunnable() {
							
							@Override
							public void run() {
								event.getBlockPlaced().removeMetadata("task", Kingdom.getInstance());
								
								event.getBlockPlaced().setType(Material.AIR);
							}
						}.runTaskLater(Kingdom.getInstance(), 35 * 20);
						
						event.getBlockPlaced().setMetadata("task", new FixedMetadataValue(Kingdom.getInstance(), task.getTaskId()));
						
						break;
					}
						
					default: {
						player.sendMessage(ConfigUtils.getFormattedValue("messages.regions.place"));
						event.setCancelled(true);
						
						break;
					}
					
				}
			}
		}
		
		
		if(KingdomHandler.influenceCheck(player, 0, player.getLocation(), event.getBlock())) {			
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
            	} else {
            		
            		if (!event.getBlock().hasMetadata("task")) {
            			return;
            		}
            		
            		int index = Kingdom.getInstance().getTasks().indexOf(event.getBlock().getMetadata("task").get(0).asInt());
            		
            		if (index != -1) {
            			BukkitTask task = Kingdom.getInstance().getTasks().get(index);
            		
            			if (task != null)
            				task.cancel();
            		}
            		
            		event.getBlock().removeMetadata("task", Kingdom.getInstance());
            	}
        	} else if(region.getId().contains("spawn_")) {
        		if(!(!kingdomPlayer.isKing() || !kingdomPlayer.isHertog())) {
        			event.setCancelled(true);
        		}
        	}
        }
		
		if(KingdomHandler.influenceCheck(player, 1, player.getLocation(), event.getBlock())) {			
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
					&& !(!kingdomPlayer.isKing() || !kingdomPlayer.isHertog())) {
				event.setCancelled(true);
			}
		}
		
		ArrayList<Material> allowed = new ArrayList<>();

		for (String mat : Kingdom.getInstance().getConfig().getStringList("settings.use_materials")) {
			allowed.add(Material.valueOf(mat.toUpperCase()));
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && allowed.contains(event.getClickedBlock().getType())
				&& KingdomHandler.influenceCheck(player, 2, player.getLocation(), event.getClickedBlock())) {
			//player.sendMessage(ChatColor.RED + "Je hebt te weinig influence hiervoor.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		RegionManager regionM = WGBukkit.getRegionManager(player.getLocation().getWorld());
		
		Block waterSource = event.getBlockClicked().getRelative(event.getBlockFace());
		
		if(event.getBucket() != Material.WATER_BUCKET) {
			return;
		}
		
		if(player.getGameMode() != GameMode.SURVIVAL) {
			return;
		}
		
		for(ProtectedRegion region : regionM.getApplicableRegions(waterSource.getLocation()).getRegions()) {
			if(region.getId().contains("eventrg_")) {				
				BukkitTask task = new BukkitRunnable() {
					
					@Override
					public void run() {
						waterSource.removeMetadata("task", Kingdom.getInstance());
						
						waterSource.setType(Material.AIR);
					}
				}.runTaskLater(Kingdom.getInstance(), 35 * 20);
				
				waterSource.setMetadata("task", new FixedMetadataValue(Kingdom.getInstance(), task.getTaskId()));
				
				return;
			}
		}
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		RegionManager regionM = WGBukkit.getRegionManager(player.getLocation().getWorld());
		
		Block waterSource = event.getBlockClicked().getRelative(event.getBlockFace());
		
		if (!waterSource.hasMetadata("task")) {
			return;
		}
				
		int index = Kingdom.getInstance().getTasks().indexOf(waterSource.getMetadata("task").get(0).asInt());
		
		if(index != -1) {
			BukkitTask task = Kingdom.getInstance().getTasks().get(index);
			
			if (task != null)
				task.cancel();
		}
		
		waterSource.removeMetadata("task", Kingdom.getInstance());
	}
}
