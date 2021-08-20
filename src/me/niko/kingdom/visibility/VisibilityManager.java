package me.niko.kingdom.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.niko.kingdom.Kingdom;

public class VisibilityManager {
	
	public static void update(Player player) {
		for(Player target : Bukkit.getOnlinePlayers()) {			
			if(shouldSee(target, player)) {
				target.showPlayer(player);
			} else {
				target.hidePlayer(player);
			}
			
			if(shouldSee(player, target)) {
				player.showPlayer(target);
			} else {
				player.hidePlayer(target);
			}
		}
	}
	
	public static boolean shouldSee(Player player, Player target) {
		boolean value = true;
		
		boolean playerIn = false;
		
		RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());
		
		for(ProtectedRegion region : WGBukkit.getRegionManager(target.getWorld()).getApplicableRegions(target.getLocation()).getRegions()) {
			if(region.getId().contains("visibility_")) {
				
				playerIn = true;
				break;
			}
		}
		
		boolean targetIn = false;

		for(ProtectedRegion region : WGBukkit.getRegionManager(target.getWorld()).getApplicableRegions(target.getLocation()).getRegions()) {
			if(region.getId().contains("visibility_")) {
				
				targetIn = true;
				break;
			}
		}
		
		Bukkit.broadcastMessage("playerIn = " + playerIn + " | targetIn = " + targetIn);
		
		return !(playerIn && targetIn);
	}
}
