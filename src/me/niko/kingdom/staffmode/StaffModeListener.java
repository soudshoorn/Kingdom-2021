package me.niko.kingdom.staffmode;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.niko.kingdom.staffmode.menu.OnlineStaffMenu;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemStackUtils;

public class StaffModeListener implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = player.getItemInHand();
		
		if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) 
				&& StaffModeHandler.isInStaffMode(player)) {
			
			if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.VANISH_OFF)) {
				StaffModeHandler.setVisible(player, false);
			} else if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.VANISH_ON)) {
				StaffModeHandler.setVisible(player, true);
			} else if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.RANDOM_TELEPORT)) {
				if(Bukkit.getOnlinePlayers().size() <= 1) {
					player.sendMessage(ChatColor.RED + "No players online.");
					return;
				}
								
				Player randomPlayer = Bukkit.getOnlinePlayers().stream().skip((int) (Bukkit.getOnlinePlayers().size() * Math.random())).findFirst().orElse(null);

				if(randomPlayer == null) {
					player.sendMessage(ChatColor.RED + "The random player was null click it again.");
					return;
				}
				
				player.teleport(randomPlayer);
				player.sendMessage(ChatColor.GREEN + "Random teleported to " + randomPlayer.getName());
			} else if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.ONLINE_STAFF)) {
				new OnlineStaffMenu().openMenu(player);
			}
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if(!(event.getRightClicked() instanceof Player)) {
			return;
		}
		
		Player player = event.getPlayer();
		Player rightClicked = (Player) event.getRightClicked();
		ItemStack stack = player.getItemInHand();
		
		if(StaffModeHandler.isInStaffMode(player)) {
			if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.INSPECTOR)) {
				player.openInventory(rightClicked.getInventory());
			} else if(ItemStackUtils.isSimiliar(stack, StaffModeHandler.FREEZE)) {
				player.performCommand("freeze " + rightClicked.getName());
			}
		}
	}

	@EventHandler
	public void onClickInv(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		if(StaffModeHandler.isInStaffMode(player)
				&& !player.hasPermission("kingdom.staffmode.bypass_shit")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(StaffModeHandler.isInStaffMode(player) && !player.hasPermission("kingdom.staffmode.bypass_shit")) {
			event.setCancelled(true);
		} else if(FreezeHandler.isFrozen(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCommands(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		if(!(event.getMessage().toLowerCase().startsWith("/msg") 
				|| event.getMessage().toLowerCase().startsWith("/tell")
				|| event.getMessage().toLowerCase().startsWith("/r")
				|| event.getMessage().toLowerCase().startsWith("/reply")) && FreezeHandler.isFrozen(player)) {
			event.setCancelled(true);
			
			//player.sendMessage(ChatColor.RED + "You cannot use this command while frozen.");
			
			player.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.cannot_do_while_frozen"));
			
			return;
		}
	}
	
	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if(StaffModeHandler.isInStaffMode(player) && !player.hasPermission("kingdom.staffmode.bypass_shit")) {
			event.setCancelled(true);
		} else if(FreezeHandler.isFrozen(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		
		if(StaffModeHandler.isInStaffMode(player) && !player.hasPermission("kingdom.staffmode.bypass_shit")) {
			event.setCancelled(true);
		} else if(FreezeHandler.isFrozen(player)) {
			//player.sendMessage(ChatColor.RED + "Je kunt niet breken als je bevroren bent.");
			player.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.cannot_do_while_frozen"));

			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(StaffModeHandler.isInStaffMode(player) && !player.hasPermission("kingdom.staffmode.bypass_shit")) {
			event.setCancelled(true);
		} else if(FreezeHandler.isFrozen(player)) {
			//player.sendMessage(ChatColor.RED + "Je kunt niet bouwen als je bevroren bent.");
			player.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.cannot_do_while_frozen"));

			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if((event.getTo().getX() != event.getFrom().getX()
				|| event.getTo().getZ() != event.getFrom().getZ())
				&& FreezeHandler.isFrozen(player)) {
			event.setTo(event.getFrom());
		}
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if(!(event.getDamager() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		Player damager = (Player) event.getDamager();
		
		if(FreezeHandler.isFrozen(damager)) {
			//damager.sendMessage(ChatColor.RED + "Je kunt niet aanvallen als je bevroren bent.");
			damager.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.damager_frozen"));

			event.setCancelled(true);
		} else if(FreezeHandler.isFrozen(player)) {
			//damager.sendMessage(ChatColor.RED + "Je kunt geen bevroren spelers aanvallen.");
			damager.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.victim_frozen").replaceAll("%player%", player.getName()));
			
			event.setCancelled(true);
		}

	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		if(FreezeHandler.isFrozen(player)) {			
			event.setCancelled(true);
		}
	}
	
	//Visibility 'Manager'
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(!player.hasPermission("kingdom.staffmode")) {
			for(Player target : Bukkit.getOnlinePlayers()) {
				if(StaffModeHandler.isVanished(target)) {
					player.hidePlayer(target);
				}
			}
		}
	}
	
	//Visibility 'Manager'
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
		Player player = event.getPlayer();
        String token = event.getLastToken();
        Collection<String> completions = (Collection<String>)event.getTabCompletions();
        
        completions.clear();
        
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!player.canSee(target)) {
                continue;
            }
            
            if (!StringUtils.startsWithIgnoreCase(target.getName(), token)) {
                continue;
            }
            
            completions.add(target.getName());
        }
    }
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(StaffModeHandler.isInStaffMode(player)) {
			StaffModeHandler.setStaffMode(player, false);
		} else if(FreezeHandler.isFrozen(player)) { 
			Bukkit.getOnlinePlayers().stream()
			.filter(p -> p.hasPermission("kingdom.freeze"))
			.forEach(p -> p.sendMessage(ConfigUtils.getFormattedValue("messages.freeze.logged_out").replaceAll("%player%", player.getName())));
		}
	}
}
