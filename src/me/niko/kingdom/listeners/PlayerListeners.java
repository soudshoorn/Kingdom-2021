package me.niko.kingdom.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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

import com.lunarclient.bukkitapi.object.LCWaypoint;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.api.KingdomEnterEvent;
import me.niko.kingdom.api.KingdomLeaveEvent;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.war.WarHandler;
import me.niko.kingdom.mount.HorseHandler;
import me.niko.kingdom.selector.SelectorMenu;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemMaker;
import me.niko.kingdom.utils.ItemStackUtils;
import me.niko.kingdom.utils.TitleAPI;

public class PlayerListeners implements Listener {
	
	private ItemStack SELECTOR = new ItemMaker(Material.NETHER_STAR).setName("&eKingdom Selector").build();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		
		KingdomPlayer kingdomPlayer = new KingdomPlayer(player);
		
		KingdomHandler.addOnlinePlayer(player, kingdomPlayer.getKingdom());
		Kingdom.getInstance().getPlayersMap().put(player.getUniqueId(), kingdomPlayer);
		
		if(!player.hasPlayedBefore() || kingdomPlayer.getKingdom() == null) {
			player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.select_kingdom_before_continue"));
			
			player.getInventory().setItem(4, SELECTOR);
		}
		
		Kingdom.getInstance().getNametags().updateNametagsManually(player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		
		Player player = event.getPlayer();
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		kingdomPlayer.save();
		
		KingdomHandler.removeOnlinePlayer(player, kingdomPlayer.getKingdom());
		
		Kingdom.getInstance().getPlayersMap().remove(player.getUniqueId());
		
		if(KingdomHandler.getWaypointsMap().containsKey(player.getUniqueId())) {
			LCWaypoint waypoint = KingdomHandler.getWaypointsMap().get(player.getUniqueId());
			
			Kingdom.getLunarClientAPI().getInstance().removeWaypoint(player, waypoint);
			KingdomHandler.getWaypointsMap().remove(player.getUniqueId());
		}
		
		if(Kingdom.getInstance().getChat().getChatSpy().contains(player.getUniqueId())) {
			Kingdom.getInstance().getChat().getChatSpy().remove(player.getUniqueId());
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && ItemStackUtils.isSimiliar(player.getItemInHand(), SELECTOR)) {
			
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
			
			if(kingdomPlayer == null) {
				new SelectorMenu().openMenu(player);
			} else {
				player.setItemInHand(null);
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
			if (HorseHandler.getMountingTimer().containsKey(player)) {
				HorseHandler.getMountingTimer().remove(player);
			}
			
			KingdomConstructor kingdomFrom = KingdomHandler.getKingdomByLocation(event.getFrom());
			KingdomConstructor kingdomTo = KingdomHandler.getKingdomByLocation(event.getTo());

			//String kingdom1 = kingdomFrom == null ? "-" : kingdomFrom.getName();
			//String kingdom2 = kingdomTo == null ? "-" : kingdomTo.getName();
						
			if (!KingdomHandler.isSimiliarKingdom(kingdomFrom, kingdomTo) && !WarHandler.isEnabled()) {
				if (kingdomTo == null) {
					//TitleAPI.send(player, ChatColor.GRAY + "Kingdom", "Onbekend", 2, 6, 8);
					
					TitleAPI.send(player, ConfigUtils.getFormattedValue("title.top"), 
							ConfigUtils.getFormattedValue("title.subtitle").replaceAll("%kingdom%", "Onbekend"), 2, 6, 8);
				} else {
					//TitleAPI.send(player, ChatColor.GRAY + "Kingdom", kingdomTo.getDisplayName(), 2, 6, 8);
					
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
		
		if(kingdomPlayer.getKingdom() != null) {
			
			if(Kingdom.getInstance().isBeta()) {
				new BukkitRunnable() {
	
					@Override
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kit beta " + player.getName());
					}
	        	}.runTaskLater(Kingdom.getInstance(), 20);
			}
			
			KingdomConstructor kingdoms = kingdomPlayer.getKingdom();
			
			if(kingdoms.doesExists()) {
				event.setRespawnLocation(kingdoms.getSpawnLocation());
			}
		} else {
			event.setRespawnLocation(null);
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
