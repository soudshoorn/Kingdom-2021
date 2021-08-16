package me.niko.kingdom.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.command.ServerCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.blacklist.target.Target;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.data.players.rank.KingdomRank;
import me.niko.kingdom.data.sort.KingdomPointsSort;
import me.niko.kingdom.utils.ConfigUtils;

public class KingdomCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {		
		if(args.length == 0) {
			
			if(sender.isOp()) {
				sender.sendMessage(ChatColor.RED + "/" + label + " createkingdom <kingdomName> <WoolData> <displayName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setspawn <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " spawn <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " tphere <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setkingdom <player> <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setdisplayname <kingdomName> <displayName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " promote <playerName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " demote <playermName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " ally <kingdomName>");
			}
			
			sender.sendMessage(ChatColor.RED + "/" + label + " top");

			return true;
		}
		
		switch(args[0].toLowerCase()) {
			case "createkingdom": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 4) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdomName> <WoolData> <displayName>");
					return true;
				}
				
				String kingdomName = args[1];
				String displayName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
				int woolData = Integer.parseInt(args[2]);
				
				KingdomConstructor kingdom = new KingdomConstructor(kingdomName);
				
				if(kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + kingdomName + "' already exists.");
					return true;
				}
				
				kingdom.create();
				
				kingdom.setWoolData((byte) woolData);
				kingdom.setDisplayName(displayName);
				
				kingdom.save();
				
				sender.sendMessage(ChatColor.GREEN + "Kingdom '" + kingdomName + "' has been created successfully.");
				
				break;
			}
			
			case "setkingdom": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <player> <kingdom>");
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					
					return true;
				}
				
				String kingdomName = args[2];

				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(target);
				
				KingdomHandler.removeOnlinePlayer(target, kingdomPlayer.getKingdom());
				
				KingdomConstructor kingdom = new KingdomConstructor(kingdomName);
				
				if(!kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + kingdomName + "' does not exists.");
					return true;
				}
				
				KingdomConstructor oldKingom = kingdomPlayer.getKingdom();
				
				kingdomPlayer.setKingdom(kingdom);
				
				if(kingdom.getSpawnLocation() != null) {
					target.teleport(kingdom.getSpawnLocation());
				}
				
				kingdomPlayer.save();
				
				target.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.set.target")
						.replaceAll("%old_kingdom%", oldKingom == null ? "None" : oldKingom.getDisplayName())
						.replaceAll("%new_kingdom%", kingdom == null ? "None" : kingdom.getDisplayName()));
				
				Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.kingdom.set.broadcast")
						.replaceAll("%player%", target.getName())
						.replaceAll("%old_kingdom%", oldKingom == null ? "None" : oldKingom.getDisplayName())
						.replaceAll("%new_kingdom%", kingdom == null ? "None" : kingdom.getDisplayName()));
				
				KingdomHandler.addOnlinePlayer(target, kingdomPlayer.getKingdom());
				
				break;
			}
			
			case "setspawn": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdom>");
					return true;
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
					return true;
				}
				
				Player player = (Player) sender;
				
				String name = args[1];
				
				KingdomConstructor kingdom = new KingdomConstructor(name);
				
				if(!kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + name + "' does not exists.");
					return true;
				}
				
				kingdom.setSpawnLocation(player.getLocation());
				kingdom.save();
				
				player.sendMessage(ChatColor.GREEN + "Kingdom spawn has been saved.");
				
				break;
			}
			
			case "spawn": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdom>");
					return true;
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
					return true;
				}
				
				Player player = (Player) sender;
				
				String name = args[1];
				
				KingdomConstructor kingdom = new KingdomConstructor(name);
				
				if(!kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + name + "' does not exists.");
					return true;
				}
				
				if (kingdom.getSpawnLocation() != null) {
					player.teleport(kingdom.getSpawnLocation());
					player.sendMessage(ChatColor.GREEN + "Teleported to " + kingdom.getDisplayName() + " spawn.");
				} else {
					player.sendMessage(ChatColor.RED + "This kingdom does not have a spawn set.");
				}
				break;
			}
			
			case "tphere": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdom>");
					return true;
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
					return true;
				}
				
				Player player = (Player) sender;
				
				String name = args[1];

				int players = 0;
				
				for(Player target : Bukkit.getOnlinePlayers()) {
					KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
					
					if(!kingdomTarget.getKingdom().getName().toLowerCase().equals(name.toLowerCase())) {
						continue;	
					}
					
					players++;
					target.teleport(player);
				}
				
				player.sendMessage(ChatColor.GREEN + "Teleported " + players + " players from kingdom '" + name + "' to you.");
				
				break;
			}
			
			case "setdisplayname": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdom> <displayName>");
					return true;
				}
				
				String name = args[1];
				String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

				KingdomConstructor kingdom = new KingdomConstructor(name);
				
				if(!kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + name + "' does not exists.");
					return true;
				}
				
				sender.sendMessage(ChatColor.GREEN + "Kingdom displayname has been changed to (" + displayName + ChatColor.GREEN + ") from (" + kingdom.getDisplayName() + ChatColor.GREEN + ")");
				
				kingdom.setDisplayName(displayName);
				
				kingdom.save();
				
				break;
			}
			
			case "top": {
				ArrayList<KingdomConstructor> sortedKingdoms = new ArrayList<>(KingdomHandler.getKingdoms());
				
				Collections.sort(sortedKingdoms, new KingdomPointsSort());
				
				int index = 0;
				
				for(String line : Kingdom.getInstance().getConfig().getStringList("messages.kingdom.top.message")) {
					if(line.contains("%format%")) {
						for(KingdomConstructor kingdomConstructor : sortedKingdoms) {
							if(index == 10) {
								break;
							}
							
							index++;
							
							sender.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.top.format")
									.replaceAll("%position%", index + "")
									.replaceAll("%kingdom%", kingdomConstructor.getDisplayName())
									.replaceAll("%points%", kingdomConstructor.getPoints() + ""));
						}
						
						continue;
					}
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				
				break;
			}
			
			case "promote": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <player>");
					return true;
				}
				
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(target.getUniqueId());
				
				if (kingdomPlayer.getKingdom() == null) {
					sender.sendMessage(ChatColor.RED + "This player does not have an kingdom.");
					return true;
				}
				
				ArrayList<KingdomRank> ranks = new ArrayList<>(KingdomHandler.getRanks());
				
				if(kingdomPlayer.getKingdomRank() == (ranks.size() - 1)) {
					sender.sendMessage(ChatColor.RED + "This player is already the highest rank.");
					return true;
				}
				
				KingdomRank oldRank = ranks.get(kingdomPlayer.getKingdomRank());
				KingdomRank newRank = ranks.get(kingdomPlayer.getKingdomRank() + 1);

				kingdomPlayer.setKingdomRank(kingdomPlayer.getKingdomRank() + 1);
				
				if(target.isOnline() && target.getPlayer() != null) {
					target.getPlayer().sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.promote_target")
							.replaceAll("%oldrank%", oldRank.getPrefix())
							.replaceAll("%newrank%", newRank.getPrefix()));
				}
				
				sender.sendMessage(ChatColor.RED + target.getName() + " has been promoted from " + oldRank.getPrefix() + " to " + newRank.getPrefix());
				
				kingdomPlayer.save();
								
				break;
			}
			
			case "demote": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <player>");
					return true;
				}
				
				OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
				
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(target.getUniqueId());
				
				if (kingdomPlayer.getKingdom() == null) {
					sender.sendMessage(ChatColor.RED + "This player does not have an kingdom.");
					return true;
				}
				
				ArrayList<KingdomRank> ranks = new ArrayList<>(KingdomHandler.getRanks());
				
				if(kingdomPlayer.getKingdomRank() == 0) {
					sender.sendMessage(ChatColor.RED + "This player is already the lowest rank.");
					return true;
				}
				
				KingdomRank oldRank = ranks.get(kingdomPlayer.getKingdomRank());
				KingdomRank newRank = ranks.get(kingdomPlayer.getKingdomRank() - 1);

				kingdomPlayer.setKingdomRank(kingdomPlayer.getKingdomRank() - 1);

				if(target.isOnline() && target.getPlayer() != null) {
					target.getPlayer().sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.demote_target")
							.replaceAll("%oldrank%", ChatColor.stripColor(oldRank.getName()))
							.replaceAll("%newrank%", ChatColor.stripColor(newRank.getPrefix())));
				}
				
				sender.sendMessage(ChatColor.RED + target.getName() + " has been demoted from " + ChatColor.stripColor(oldRank.getPrefix()) + " to " + ChatColor.stripColor(newRank.getPrefix()));
				
				kingdomPlayer.save();
								
				break;
			}
			
			case "ally": {
				if(!(sender instanceof Player)) {
					return true;
				}
				
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <kingdom>");
					return true;
				}
				
				Player player = (Player) sender;
				String name = args[1];

				KingdomConstructor kingdomTarget = new KingdomConstructor(name);
				
				if(!kingdomTarget.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + name + "' does not exists.");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				KingdomConstructor playerKingdom = kingdomPlayer.getKingdom();
				
				boolean isAlly = KingdomHandler.isAllyWithKingdom(playerKingdom, kingdomTarget);
				boolean isAllyRequested = (playerKingdom.getAllies().stream().filter(k -> KingdomHandler.isSimiliarKingdom(k, playerKingdom)).findFirst().orElse(null)) != null;
				boolean isAboutToAcceptAlly = (kingdomTarget.getAllies().stream().filter(k -> KingdomHandler.isSimiliarKingdom(k, playerKingdom)).findFirst().orElse(null)) != null;

				if(isAllyRequested && !isAlly) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.ally.already_requested"));
					return true;
				}

				if(isAlly) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.ally.already_ally"));
					return true;
				}
				
				if(isAboutToAcceptAlly) { //Accepting Ally
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.ally.accepted_ally"));
					
					kingdomTarget.broadcast(true, ConfigUtils.getFormattedValue("messages.kingdom.ally.broadcast.ally_accepted")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
				} else {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.ally.ally_requested")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
					
					kingdomTarget.broadcast(true, ConfigUtils.getFormattedValue("messages.kingdom.ally.broadcast.requested_ally")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
				}
				
				playerKingdom.getAllies().add(kingdomTarget);
				
				playerKingdom.save();
				
				break;
			}
			
			default: {
				if(sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "/" + label + " createkingdom <kingdomName> <WoolData> <displayName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setspawn <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " spawn <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " tphere <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setkingdom <player> <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setdisplayname <kingdomName> <displayName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " promote <playerName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " demote <playermName>");
				}
				
				sender.sendMessage(ChatColor.RED + "/" + label + " top");

				break;
			}
		
		} 
		return false;
	}

}