package me.niko.kingdom.listeners;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.data.players.rank.KingdomRank;
import me.niko.kingdom.events.war.WarHandler;
import me.niko.kingdom.utils.ConfigUtils;

public class DamageListeners implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
		
		if(kingdom == null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

		kingdomPlayer.setDeaths(kingdomPlayer.getDeaths() - 1);
		
		kingdomPlayer.save();
		
		if(Kingdom.getInstance().isBeta()) {
			player.getInventory().clear();
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
		
		if(player.getKiller() != null) {
			
			if(!(player.getKiller() instanceof Player)) {
				return;
			}
			
			Player killer = player.getKiller();
			KingdomPlayer kingdomKiller = KingdomHandler.getKingdomPlayer(killer);
			KingdomConstructor killerKingdom = KingdomHandler.getKingdom(kingdomKiller);

			kingdomKiller.setKills(kingdomKiller.getKills() + 1);
			
			if(WarHandler.isEnabled() && !killerKingdom.isStaffOnly()) {
				WarHandler.getWarKills().put(killerKingdom.getName(), WarHandler.getWarKills().getOrDefault(killerKingdom.getName(), 1));
			}
			
			event.setDeathMessage(ConfigUtils.getFormattedValue("messages.death_message")
					.replaceAll("%player_kingdom%", kingdom.getDisplayName())
					.replaceAll("%player_kills%", kingdomPlayer.getKills() + "")
					.replaceAll("%player_kingdom_rank%", KingdomHandler.getRanks().get(kingdomPlayer.getKingdomRank()).getPrefix())
					.replaceAll("%killer_kingdom%", killerKingdom.getDisplayName())
					.replaceAll("%killer_kills%", kingdomKiller.getKills() + "")
					.replaceAll("%killer_kingdom_rank%", KingdomHandler.getRanks().get(kingdomKiller.getKingdomRank()).getPrefix()));					

			if(killerKingdom != null) {
				killerKingdom.setPoints(killerKingdom.getPoints() + 1);
			}
			
			if(kingdom != null) {
				kingdom.setPoints(kingdom.getPoints() - 1);
			}
			
			killerKingdom.save();
			kingdomKiller.save();
			kingdom.save();
		}
	}
	
	@EventHandler
	public void onDamageByPlayer(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player victim = (Player) event.getEntity();
		
		Player damager;
		
		if(event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if(event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			
			if(!(projectile.getShooter() instanceof Player)) {
				return;
			}
			
			damager = (Player) projectile.getShooter();
			
		} else {
			return;
		}
		
		KingdomPlayer kingdomVictim = KingdomHandler.getKingdomPlayer(victim);
		KingdomPlayer kingdomDamager = KingdomHandler.getKingdomPlayer(damager);
		
		KingdomConstructor victimKingdom = KingdomHandler.getKingdom(kingdomVictim);
		KingdomConstructor damagerKingdom = KingdomHandler.getKingdom(kingdomDamager);

		
		if(KingdomHandler.isSimiliarKingdom(victimKingdom, damagerKingdom)) {
			event.setCancelled(true);
			
			damager.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.victim_teammate").replaceAll("%player%", victim.getName()));
			
			return;
		}
		
		if(!WarHandler.isEnabled()) {
			if(victimKingdom == null || damagerKingdom == null) {
				event.setCancelled(true);
				
				return;
			}	
			
			if(KingdomHandler.isAllyWithKingdom(victimKingdom, damagerKingdom)) {
				//damager.sendMessage(ChatColor.AQUA + victim.getName() + " is an ally from " + kingdomVictim.getKingdom().getDisplayName());
				
				damager.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.victim_ally")
						.replaceAll("%player%", victim.getName())
						.replaceAll("%kingdom%", victimKingdom == null ? "" : victimKingdom.getDisplayName()));

				event.setCancelled(true);
			}
		}
	}

}
