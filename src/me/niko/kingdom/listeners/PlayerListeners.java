package me.niko.kingdom.listeners;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.avaje.ebeaninternal.server.el.ElSetValue;
import com.lunarclient.bukkitapi.object.LCWaypoint;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.api.KingdomEnterEvent;
import me.niko.kingdom.api.KingdomLeaveEvent;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.enderchest.EnderchestMenu;
import me.niko.kingdom.events.war.WarHandler;
import me.niko.kingdom.mount.HorseHandler;
import me.niko.kingdom.selector.SelectorMenu;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.ItemStackUtils;
import me.niko.kingdom.utils.TitleAPI;
import me.niko.kingdom.visibility.VisibilityManager;

public class PlayerListeners implements Listener {
		
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		
		KingdomPlayer kingdomPlayer = new KingdomPlayer(player);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

		KingdomHandler.addOnlinePlayer(player, kingdom);
		Kingdom.getInstance().getPlayersMap().put(player.getUniqueId(), kingdomPlayer);

		if(!player.hasPlayedBefore() || kingdom == null) {
			player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.select_kingdom_before_continue"));
			
			player.getInventory().setItem(4, ItemStackUtils.SELECTOR);
			
			World world = Bukkit.getWorld("spawn");
			
			if (world != null) {
				player.teleport(world.getSpawnLocation());
			}
		}
		
		if(Kingdom.getInstance().getConfig().getBoolean("settings.hide_players_spawn")) {
			VisibilityManager.update(player);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		
		Player player = event.getPlayer();
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		kingdomPlayer.save();
		
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
		
		KingdomHandler.removeOnlinePlayer(player, kingdom);
		
		Kingdom.getInstance().getPlayersMap().remove(player.getUniqueId());
		
		if(KingdomHandler.getWaypointsMap().containsKey(player.getUniqueId())) {
			LCWaypoint waypoint = KingdomHandler.getWaypointsMap().get(player.getUniqueId());
			
			Kingdom.getLunarClientAPI().getInstance().removeWaypoint(player, waypoint);
			KingdomHandler.getWaypointsMap().remove(player.getUniqueId());
		}
		
		if(Kingdom.getInstance().getChat().getChatSpy().contains(player.getUniqueId())) {
			Kingdom.getInstance().getChat().getChatSpy().remove(player.getUniqueId());
		}
		
		if(Kingdom.getInstance().getAutoSmelting().contains(player.getUniqueId())) {
			Kingdom.getInstance().getAutoSmelting().remove(player.getUniqueId());
		}
		
		if(Kingdom.getInstance().getConfig().getBoolean("settings.hide_players_spawn")) {
			VisibilityManager.update(player);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
			event.setCancelled(true);
			new EnderchestMenu(kingdomPlayer, kingdomPlayer).openMenu(player);
			
			//Adding return here cuz if they use any other item that opens an inventory that might fuck our plugin XD
			return;
		}
		
		if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && ItemStackUtils.isSimiliar(player.getItemInHand(), ItemStackUtils.SELECTOR)) {
			
			KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

			if(kingdom == null) {
				new SelectorMenu().openMenu(player);
				
				return;
			} else {
				player.setItemInHand(null);
				
				return;
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		if(kingdomPlayer == null && !player.isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()) {			
			if (HorseHandler.getMountingTimer().containsKey(player.getUniqueId())) {
				HorseHandler.getMountingTimer().remove(player.getUniqueId());
			}
			
			KingdomConstructor kingdomFrom = KingdomHandler.getKingdomByLocation(event.getFrom());
			KingdomConstructor kingdomTo = KingdomHandler.getKingdomByLocation(event.getTo());
						
			if (!KingdomHandler.isSimiliarKingdom(kingdomFrom, kingdomTo) && !WarHandler.isEnabled()) {
				if (kingdomTo == null) {					
					TitleAPI.send(player, ConfigUtils.getFormattedValue("title.top"), 
							ConfigUtils.getFormattedValue("title.subtitle").replaceAll("%kingdom%", "Onbekend"), 2, 6, 8);
				} else {					
					TitleAPI.send(player, ConfigUtils.getFormattedValue("title.top"), 
							ConfigUtils.getFormattedValue("title.subtitle").replaceAll("%kingdom%", kingdomTo.getDisplayName()), 2, 6, 8);
				}
				
				Bukkit.getPluginManager().callEvent(new KingdomEnterEvent(player, kingdomTo));
				Bukkit.getPluginManager().callEvent(new KingdomLeaveEvent(player, kingdomFrom));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

		if(kingdom != null) {
			
			if(Kingdom.getInstance().isBeta()) {
				new BukkitRunnable() {
	
					@Override
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kit beta " + player.getName());
					}
	        	}.runTaskLater(Kingdom.getInstance(), 20);
			}
						
			if(kingdom.doesExists()) {
				event.setRespawnLocation(kingdom.getSpawnLocation());
			}
		} else {
			World world = Bukkit.getWorld("spawn");
			
			if (world != null) {
				event.setRespawnLocation(world.getSpawnLocation());
			} else {
				event.setRespawnLocation(null);
			}
		}
	}
	
	@EventHandler
	public void onPreCommandProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if(message.startsWith("/set_waypoint_lc_lol_")) {
			event.setCancelled(true);
			
			message = message.replace("/set_waypoint_lc_lol_ ", "");
			
			Player target = Bukkit.getPlayer(message);
			Location playerLocation = target.getLocation();
			
			if(target != null) {
			
				if (!playerLocation.getWorld().getName().equals(player.getWorld().getName())) {
					player.sendMessage(ChatColor.RED + "You are not in the same world as that player.");
					
					return;
				}
								
				if(KingdomHandler.getWaypointsMap().containsKey(player.getUniqueId())) {
					LCWaypoint waypoint = KingdomHandler.getWaypointsMap().get(player.getUniqueId());
					
					Kingdom.getLunarClientAPI().getInstance().removeWaypoint(player, waypoint);
					KingdomHandler.getWaypointsMap().remove(player.getUniqueId());
				}
				
				LCWaypoint waypoint = new LCWaypoint(ConfigUtils.getFormattedValue("messages.tell_location.waypoint.name").replaceAll("%player%", target.getName()), 
						playerLocation, 
						Color.fromRGB(Kingdom.getInstance().getConfig().getInt("messages.tell_location.waypoint.color.r"), 
								Kingdom.getInstance().getConfig().getInt("messages.tell_location.waypoint.color.g"), 
								Kingdom.getInstance().getConfig().getInt("messages.tell_location.waypoint.color.b")).asRGB(), 
						true, true);
				
				Kingdom.getLunarClientAPI().getInstance().sendWaypoint(player, waypoint);
				KingdomHandler.getWaypointsMap().put(player.getUniqueId(), waypoint);
				
				/*new BukkitRunnable() {
					final LCWaypoint mainWaypoint = waypoint;
					@Override
					public void run() {
						if(KingdomHandler.getWaypointsMap().containsKey(player.getUniqueId())) {
							
							LCWaypoint waypoint = KingdomHandler.getWaypointsMap().get(player.getUniqueId());
							
							if(waypoint.getName().equals(mainWaypoint.getName())) {
								Kingdom.getLunarClientAPI().getInstance().removeWaypoint(player, waypoint);
								
								KingdomHandler.getWaypointsMap().remove(player.getUniqueId());
							} else {
								cancel();
							}
						} else {
							cancel();
						}
					}
				}.runTaskLater(Kingdom.getInstance(), 20 * 60); // 1 Minute and clear it!*/
				
			} else {
				player.sendMessage(ChatColor.RED + "That player is no longer online!");
				
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCraft(PrepareItemCraftEvent event) {
		if(event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() == InventoryType.WORKBENCH) {
			
			if(event.getRecipe().getResult().getType() == Material.REDSTONE) {
				event.getInventory().setResult(new ItemStack(Material.COOKED_BEEF, 64));
			} else if(event.getRecipe().getResult().getType() == Material.BEACON) {
				event.getInventory().setResult(new ItemStack(Material.AIR, 64));
			}
		}
	}

}
