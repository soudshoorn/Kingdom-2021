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

import com.avaje.ebeaninternal.server.el.ElSetValue;
import com.sk89q.worldguard.blacklist.target.Target;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.data.players.rank.KingdomRank;
import me.niko.kingdom.data.sort.KingdomPointsSort;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.ItemStackUtils;

public class KingdomCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {		
		if(args.length == 0) {
			
			if(sender.isOp()) {
				
				for(String line : ConfigUtils.getFormattedValueList("messages.kingdom.help_op")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				
				/*sender.sendMessage(ChatColor.RED + "/" + label + " createkingdom <kingdomName> <WoolData> <displayName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setspawn <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " spawn <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " tphere <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setkingdom <player> <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " setdisplayname <kingdomName> <displayName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " promote <playerName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " demote <playermName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " ally <kingdomName>");
				sender.sendMessage(ChatColor.RED + "/" + label + " unally <kingdomName>");*/
			} else {
				for(String line : ConfigUtils.getFormattedValueList("messages.kingdom.help_no_op")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
			}
			
			//sender.sendMessage(ChatColor.RED + "/" + label + " top");

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
				KingdomConstructor oldKingom = KingdomHandler.getKingdom(kingdomPlayer);

				KingdomHandler.removeOnlinePlayer(target, oldKingom);
				
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomName);
				
				if(kingdomName.equals("null")) {
					kingdom = null;
				} else {
					if(!kingdom.doesExists()) {
						sender.sendMessage(ChatColor.RED + "Kingdom named '" + kingdomName + "' does not exists.");
						return true;
					}
				}
								
				if (kingdom == null) {
					kingdomPlayer.setKingdom(null);
					target.getInventory().setItem(4, ItemStackUtils.SELECTOR);

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + target.getName());
					
					kingdomPlayer.save();
					
					target.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.set.target")
							.replaceAll("%old_kingdom%", oldKingom == null ? "None" : oldKingom.getDisplayName())
							.replaceAll("%new_kingdom%", kingdom == null ? "None" : kingdom.getDisplayName()));
					
					Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.kingdom.set.broadcast")
							.replaceAll("%player%", target.getName())
							.replaceAll("%old_kingdom%", oldKingom == null ? "None" : oldKingom.getDisplayName())
							.replaceAll("%new_kingdom%", kingdom == null ? "None" : kingdom.getDisplayName()));
					
					KingdomHandler.addOnlinePlayer(target, KingdomHandler.getKingdom(kingdomPlayer));

					return true;
				}
				
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
				
				KingdomHandler.addOnlinePlayer(target, KingdomHandler.getKingdom(kingdomPlayer));

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
				
				KingdomConstructor kingdom = KingdomHandler.getKingdom(name);
				
				if(!kingdom.doesExists()) {
					sender.sendMessage(ChatColor.RED + "Kingdom named '" + name + "' does not exists.");
					return true;
				}
				
				kingdom.setSpawnLocation(player.getLocation());
				kingdom.save();
				
				player.sendMessage(ChatColor.GREEN + "Kingdom spawn has been saved.");
				
				break;
			}
			
			case "setboatexit": {
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
				
				kingdom.setBoatExitLocation(player.getLocation());
				kingdom.save();
				
				player.sendMessage(ChatColor.GREEN + "Kingdom Boat Exit has been saved.");
				
				break;
			}
			
			case "boatexit": {
				if(!sender.isOp()) {
					sender.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + args[0] + " <target>");
					return true;
				}
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
					return true;
				}
				
				Player player = (Player) sender;
				Player target = Bukkit.getPlayer(args[1]);
				
				if (target == null) {
					player.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(target);
				
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
				
				if(kingdom == null) {
					player.sendMessage(ChatColor.RED + "This player does not have a kingdom.");
					return true;
				}
				
				if (kingdom.getBoatExitLocation() != null) {
					player.teleport(kingdom.getBoatExitLocation());
					player.sendMessage(ChatColor.GREEN + "Teleported " + target.getName() + " to the boat exit spawn " + kingdom.getDisplayName() + ".");
				} else {
					player.sendMessage(ChatColor.RED + "This kingdom does not have a boat exit set.");
				}
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
					KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomTarget);

					if(!kingdom.getName().toLowerCase().equals(name.toLowerCase())) {
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
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

				if (kingdom == null) {
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
				
				sender.sendMessage(ChatColor.RED + target.getName() + " has been promoted from " + oldRank.getName() + " to " + newRank.getName());
				
				kingdomPlayer.save();
				
				if(kingdomPlayer.isKing()) {
					for(String line : ConfigUtils.getFormattedValueList("settings.hertog_perms")) {
						if (Kingdom.getInstance().getPerms().has(target.getPlayer(), line))
							Kingdom.getInstance().getPerms().playerRemove(target.getPlayer(), line);
					}
					
					for(String line : ConfigUtils.getFormattedValueList("settings.king_perms")) {
						Kingdom.getInstance().getPerms().playerAdd(target.getPlayer(), line);
					}
				} else if(kingdomPlayer.isHertog()) {
					for(String line : ConfigUtils.getFormattedValueList("settings.hertog_perms")) {
						Kingdom.getInstance().getPerms().playerAdd(target.getPlayer(), line);
					}
				}
								
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
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

				if (kingdom == null) {
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
				
				sender.sendMessage(ChatColor.RED + target.getName() + " has been demoted from " + oldRank.getName() + " to " + newRank.getName());
				
				kingdomPlayer.save();
				
				if(kingdomPlayer.isHertog()) {
					for(String line : ConfigUtils.getFormattedValueList("settings.king_perms")) {
						if (Kingdom.getInstance().getPerms().has(target.getPlayer(), line))
							Kingdom.getInstance().getPerms().playerRemove(target.getPlayer(), line);
					}
					
					for(String line : ConfigUtils.getFormattedValueList("settings.hertog_perms")) {
						Kingdom.getInstance().getPerms().playerAdd(target.getPlayer(), line);
					}
				}
				
				if(!(kingdomPlayer.isHertog() && kingdomPlayer.isKing())) {
					for(String line : ConfigUtils.getFormattedValueList("settings.hertog_perms")) {
						if (Kingdom.getInstance().getPerms().has(target.getPlayer(), line))
							Kingdom.getInstance().getPerms().playerRemove(target.getPlayer(), line);
					}
				}
								
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
				KingdomConstructor playerKingdom = KingdomHandler.getKingdom(kingdomPlayer);
				
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
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.ally.accepted_ally")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
					
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
			
			case "unally": {
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
				KingdomConstructor playerKingdom = KingdomHandler.getKingdom(kingdomPlayer);
				
				boolean isAlly = KingdomHandler.isAllyWithKingdom(playerKingdom, kingdomTarget);
				boolean isAllyRequested = (playerKingdom.getAllies().stream().filter(k -> KingdomHandler.isSimiliarKingdom(k, kingdomTarget)).findFirst().orElse(null)) != null;
				
				if(isAllyRequested && !isAlly) {
					playerKingdom.getAllies().remove(kingdomTarget);
					playerKingdom.save();
					
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.unally.remove_request")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
					return true;
				}

				if(!isAlly) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.kingdom.unally.not_ally")
							.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));
					return true;
				}
				
				kingdomTarget.broadcast(false, ConfigUtils.getFormattedValue("messages.kingdom.unally.broadcast.ally_removed")
						.replaceAll("%kingdom%", playerKingdom.getDisplayName()));
				playerKingdom.broadcast(false, ConfigUtils.getFormattedValue("messages.kingdom.unally.broadcast.ally_removed")
						.replaceAll("%kingdom%", kingdomTarget.getDisplayName()));

				playerKingdom.getAllies().remove(kingdomTarget);
				playerKingdom.save();
				
				break;
			}
			
			default: {
				if(sender.isOp()) {
					
					for(String line : ConfigUtils.getFormattedValueList("messages.kingdom.help_op")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
					}
					
					/*sender.sendMessage(ChatColor.RED + "/" + label + " createkingdom <kingdomName> <WoolData> <displayName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setspawn <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " spawn <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " tphere <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setkingdom <player> <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " setdisplayname <kingdomName> <displayName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " promote <playerName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " demote <playermName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " ally <kingdomName>");
					sender.sendMessage(ChatColor.RED + "/" + label + " unally <kingdomName>");*/
				} else {
					for(String line : ConfigUtils.getFormattedValueList("messages.kingdom.help_no_op")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
					}
				}

				break;
			}
		
		} 
		return false;
	}

}