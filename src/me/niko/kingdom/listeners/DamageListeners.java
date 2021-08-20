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
		
		
		if(kingdomPlayer.getKingdom() == null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		kingdomPlayer.setDeaths(kingdomPlayer.getDeaths() - 1);
		KingdomConstructor kingdomConstructor2 = kingdomPlayer.getKingdom();
		
		/*
		 * Only losing a point when died to player. moved in the if statement
		 * 
		if(kingdomConstructor2 != null) {
			kingdomConstructor2.setPoints(kingdomConstructor2.getPoints() - 1);
		}*/
		
		kingdomPlayer.save();
		kingdomConstructor2.save();
		
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
			
			kingdomKiller.setKills(kingdomKiller.getKills() + 1);
			
			if(WarHandler.isEnabled()) {
				WarHandler.getWarKills().put(kingdomKiller.getKingdom().getName(), WarHandler.getWarKills().getOrDefault(kingdomKiller.getKingdom().getName(), 1));
			}
			
			event.setDeathMessage(ConfigUtils.getFormattedValue("messages.death_message")
					.replaceAll("%player_kingdom%", kingdomPlayer.getKingdom().getDisplayName())
					.replaceAll("%player_kills%", kingdomPlayer.getKills() + "")
					.replaceAll("%player_kingdom_rank%", KingdomHandler.getRanks().get(kingdomPlayer.getKingdomRank()).getPrefix())
					.replaceAll("%killer_kingdom%", kingdomKiller.getKingdom().getDisplayName())
					.replaceAll("%killer_kills%", kingdomKiller.getKills() + "")
					.replaceAll("%killer_kingdom_rank%", KingdomHandler.getRanks().get(kingdomKiller.getKingdomRank()).getPrefix()));					
						
			KingdomConstructor kingdomConstructor = kingdomKiller.getKingdom();
			
			if(kingdomConstructor != null) {
				kingdomConstructor.setPoints(kingdomConstructor.getPoints() + 1);
			}
			
			if(kingdomConstructor2 != null) {
				kingdomConstructor2.setPoints(kingdomConstructor2.getPoints() - 1);
			}
			
			kingdomKiller.save();
			kingdomConstructor.save();
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
		
		if(KingdomHandler.isSimiliarKingdom(kingdomVictim.getKingdom(), kingdomDamager.getKingdom())) {
			event.setCancelled(true);
			
			damager.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.victim_teammate").replaceAll("%player%", victim.getName()));
			
			return;
		}
		
		if(!WarHandler.isEnabled()) {
			if(kingdomVictim.getKingdom() == null || kingdomDamager.getKingdom() == null) {
				event.setCancelled(true);
				
				return;
			}	
			
			if(KingdomHandler.isAllyWithKingdom(kingdomVictim.getKingdom(), kingdomDamager.getKingdom())) {
				//damager.sendMessage(ChatColor.AQUA + victim.getName() + " is an ally from " + kingdomVictim.getKingdom().getDisplayName());
				
				damager.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.victim_ally")
						.replaceAll("%player%", victim.getName())
						.replaceAll("%kingdom%", kingdomVictim.getKingdom() == null ? "" : kingdomVictim.getKingdom().getDisplayName()));

				event.setCancelled(true);
			}
		}
	}

}
