package me.niko.kingdom.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.mount.HorseHandler;
import me.niko.kingdom.utils.ConfigUtils;

public class HorseMountListener implements Listener {

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		if (!(event.getRightClicked() instanceof Horse)) {
			return;
		}

		Horse horse = (Horse) event.getRightClicked();

		if (HorseHandler.getHorseSpawned().containsValue(horse)) {
			if (!HorseHandler.getHorseSpawned().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
				player.sendMessage(ConfigUtils.getFormattedValue("messages.mount.not_your_horse"));

				return;
			}

			Horse horse2 = (Horse) HorseHandler.getHorseSpawned().get(player.getUniqueId());

			if (!horse2.equals(horse)) {
				event.setCancelled(true);
				player.sendMessage(ConfigUtils.getFormattedValue("messages.mount.not_your_horse"));

				return;
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (event.getClickedInventory() == null) {
			return;
		}

		if (!(event.getClickedInventory().getHolder() instanceof Horse)) {
			return;
		}

		Horse horse = (Horse) event.getClickedInventory().getHolder();

		if (HorseHandler.getHorseSpawned().containsValue(horse) && HorseHandler.getHorseSpawned().containsKey(player.getUniqueId())
				&& ((Horse) HorseHandler.getHorseSpawned().get(player.getUniqueId())).equals(horse)) {
			if (event.getSlot() == 0) {
				event.setCancelled(true);
				return;
			}

			Inventory inventory = horse.getInventory();
			ItemStack[] items = inventory.getContents();

			String items64 = HorseHandler.itemStackArrayToBase64(items);

			if (HorseHandler.donkeyIv.containsKey(player.getUniqueId())) {
				HorseHandler.donkeyIv.replace(player.getUniqueId(), items64);
			} else {
				HorseHandler.donkeyIv.put(player.getUniqueId(), items64);
			}

			return;
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Horse)) {
			return;
		}

		Horse horse = (Horse) event.getEntity();

		if (HorseHandler.getHorseSpawned().containsValue(horse)) {
			event.getDrops().clear();
			event.setDroppedExp(0);

			return;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (!(event.getInventory().getHolder() instanceof Horse)) {
			return;
		}

		Horse horse = (Horse) event.getInventory().getHolder();
		if (HorseHandler.getHorseSpawned().containsValue(horse) && HorseHandler.getHorseSpawned().containsKey(player.getUniqueId())
				&& ((Horse) HorseHandler.getHorseSpawned().get(player.getUniqueId())).equals(horse)) {
			Inventory hIv = horse.getInventory();
			ItemStack[] items = hIv.getContents();
			String items64 = HorseHandler.itemStackArrayToBase64(items);
			if (HorseHandler.donkeyIv.containsKey(player.getUniqueId())) {
				HorseHandler.donkeyIv.replace(player.getUniqueId(), items64);
			} else {
				HorseHandler.donkeyIv.put(player.getUniqueId(), items64);
			}

			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDamageEvent(EntityDamageByEntityEvent event) {
		Player damager = null;

		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				damager = (Player) arrow.getShooter();
			}
		} else if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		}

		if (event.getEntity() instanceof Player) {
			Player target = (Player) event.getEntity();
			if (HorseHandler.getMountingTimer().containsKey(target.getUniqueId())) {
				HorseHandler.getMountingTimer().remove(target.getUniqueId());
			}
		} else if (damager != null && HorseHandler.getMountingTimer().containsKey(damager.getUniqueId())) {
			HorseHandler.getMountingTimer().remove(damager.getUniqueId());
		}
		
		Horse horse;
		if (damager != null && damager.isInsideVehicle() && damager.getVehicle() instanceof Horse) {
			horse = (Horse) damager.getVehicle();
			if (HorseHandler.getHorseSpawned().containsKey(damager.getUniqueId())
					&& HorseHandler.getHorseSpawned().containsValue(horse)
					&& ((Horse) HorseHandler.getHorseSpawned().get(damager.getUniqueId())).equals(horse)) {
				event.setCancelled(true);
				damager.sendMessage(ConfigUtils.getFormattedValue("messages.mount.cant_damage_mounted"));
				damager.playSound(damager.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);

				return;
			}
		}

		if (damager != null && event.getEntity() instanceof Horse) {
			horse = (Horse) event.getEntity();
			if (HorseHandler.getHorseSpawned().containsKey(damager.getUniqueId())
					&& HorseHandler.getHorseSpawned().containsValue(horse)
					&& ((Horse) HorseHandler.getHorseSpawned().get(damager.getUniqueId())).equals(horse)) {
				event.setCancelled(true);
				damager.sendMessage(ConfigUtils.getFormattedValue("messages.mount.cant_damage_your_horse"));
				damager.playSound(damager.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);

				return;
			}
		}

		if (damager != null && event.getEntity() instanceof Horse) {
			
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(damager);
			KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

			horse = (Horse) event.getEntity();
			if (HorseHandler.getHorseSpawned().containsValue(horse)) {
				String horseKingdom = horse.getCustomName().split(" ")[1];

				KingdomConstructor kingdomConstructor = KingdomHandler.getKingdom(horse.hasMetadata("kingdom")
						? horse.getMetadata("kingdom").get(0).asString()
						: null);
				
				if (KingdomHandler.isSimiliarKingdom(kingdom, kingdomConstructor)) {
					damager.sendMessage(ConfigUtils.getFormattedValue("messages.mount.cant_damage_your_kingdom"));
					event.setCancelled(true);
					damager.playSound(damager.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);

					return;
				}

				if (horse.getPassenger() != null && horse.getPassenger() instanceof Player) {
					Player target = (Player) horse.getPassenger();
					target.sendMessage(ConfigUtils.getFormattedValue("messages.mount.got_unmounted"));

					target.playSound(target.getLocation(), Sound.DIG_GRASS, 5.0F, 1.0F);
				}

				event.setCancelled(true);
				horse.remove();

				return;
			}
		}

		if (event.getEntity() instanceof Player && damager != null) {
			Player target = (Player) event.getEntity();

			KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(damager);
			
			KingdomConstructor playerKingdom = KingdomHandler.getKingdom(kingdomPlayer);
			KingdomConstructor targetKingdom = KingdomHandler.getKingdom(kingdomTarget);

			if (!KingdomHandler.isSimiliarKingdom(playerKingdom, targetKingdom)) {
				if (HorseHandler.getHorseSpawned().containsKey(damager.getUniqueId())
						&& !((Horse) HorseHandler.getHorseSpawned().get(damager.getUniqueId())).isDead()) {
					horse = (Horse) HorseHandler.getHorseSpawned().get(damager.getUniqueId());
					horse.remove();

					HorseHandler.getHorseSpawned().remove(damager.getUniqueId());
				}

				if (HorseHandler.getHorseSpawned().containsKey(target.getUniqueId())
						&& !((Horse) HorseHandler.getHorseSpawned().get(target.getUniqueId())).isDead()) {
					horse = (Horse) HorseHandler.getHorseSpawned().get(target.getUniqueId());
					horse.remove();

					HorseHandler.getHorseSpawned().remove(target.getUniqueId());
				}
			}
		}
	}

	@EventHandler
	public void onToggleShift(EntityDismountEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		if (e.getDismounted().getType() == EntityType.HORSE) {
			e.getDismounted().remove();
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (HorseHandler.getHorseSpawned().containsKey(player.getUniqueId())) {
			Horse horse = (Horse) HorseHandler.getHorseSpawned().get(player.getUniqueId());
			
			if (!horse.isDead()) {
				horse.remove();
			}

			HorseHandler.getHorseSpawned().remove(player.getUniqueId());
		}
	}

}
