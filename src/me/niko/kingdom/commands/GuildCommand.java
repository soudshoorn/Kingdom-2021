package me.niko.kingdom.commands;

import java.text.Format;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import me.niko.kingdom.guilds.Guild;
import me.niko.kingdom.guilds.GuildHandler;
import me.niko.kingdom.guilds.menu.GuildMenu;
import me.niko.kingdom.utils.ConfigUtils;

public class GuildCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0) {
			
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

			if(kingdomPlayer.getGuild() == null) {
				for(String line : ConfigUtils.getFormattedValueList("messages.guild.help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				
				/*sender.sendMessage(ChatColor.GRAY + "You don't have a guild, heres some help about them!");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name> <TAG>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " invite <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " uninvite <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " join <guildName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " kick <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " deposit <amount>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " withdraw <amount>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " leave");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " info <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " invites");

				sender.sendMessage(ChatColor.GRAY + "/" + label + " disband");*/
			} else {
				new GuildMenu(kingdomPlayer.getGuild()).openMenu(player);
			}

			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "create": {
				if(!player.hasPermission("guild.create")) {
					player.sendMessage(ChatColor.RED + "No permission.");
					return true;
				}
				
				if(args.length < 3) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <name> <TAG>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() != null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.already_in_guild"));
					return true;
				}
				
				if(args[2].length() != 3) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.three_char"));
					return true;
				}
				
				Guild guild = new Guild(args[1]);
				
				if(guild.doesExists()) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.guild_taken"));
					return true;
				}
				
				Guild checkTag = GuildHandler.getGuildByTag(args[2]);
				
				if(checkTag != null && checkTag.doesExists()) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.tag_taken"));
					return true;
				}
				
				
				guild.setTag(args[2].toUpperCase());
				guild.setLeader(player.getUniqueId());
				guild.getMembers().add(player.getUniqueId());
				
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
				
				guild.setKingdom(kingdom);
				
				guild.save();
				
				kingdomPlayer.setGuild(guild);
				kingdomPlayer.save();
				
				player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.created")
						.replaceAll("%guild_name%", guild.getName())
						.replaceAll("%tag%", guild.getTag()));
				
				Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.guild.created_broadcast")
						.replaceAll("%guild_name%", guild.getName())
						.replaceAll("%tag%", guild.getTag())
						.replaceAll("%player%", player.getName()));
				
				break;
			}
			
			case "invite": {				
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(!guild.getLeader().equals(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.only_leaders"));
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(target == null) {
					player.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomTarget);

				if(kingdom == null || !KingdomHandler.isSimiliarKingdom(kingdom, guild.getKingdom())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_same_kingdom"));
					return true;
				}
				
				if (guild.getInvites().contains(target.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.already_invited"));
					return true;
				}
				
				guild.getInvites().add(target.getUniqueId());
				guild.save();
				
				//target.sendMessage(ChatColor.GREEN + "You have been invited to " + guild.getName() + " [" + guild.getTag() + "] by " + player.getName() + ".");
				//target.sendMessage(ChatColor.GREEN + "Do /" + label + " join " + guild.getName() + " to join.");
				
				target.sendMessage(ConfigUtils.getFormattedValue("messages.guild.have_been_invited")
						.replaceAll("%guild_name%", guild.getName())
						.replaceAll("%tag%", guild.getTag())
						.replaceAll("%player%", player.getName()));
				
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_invited")
						.replaceAll("%player%", player.getName())
						.replaceAll("%invited%", target.getName()));
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if (guild.getInvites().contains(target.getUniqueId())) {
							player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.expired_invite")
									.replaceAll("%guild_name%", guild.getName())
									.replaceAll("%tag%", guild.getTag()));
							
							guild.getInvites().remove(target.getUniqueId());
							guild.save();
						}
					}
				}.runTaskLater(Kingdom.getInstance(), 20 * 120);
				
				break;
			}
			
			case "uninvite": {				
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(!guild.getLeader().equals(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.only_leaders"));
					return true;
				}
				
				Player target = Bukkit.getPlayer(args[1]);
				
				if(target == null) {
					player.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' not found.");
					return true;
				}
				
				if (!guild.getInvites().contains(target.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.not_invited"));
					return true;
				}
				
				guild.getInvites().remove(target.getUniqueId());
				guild.save();
								
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_uninvited")
						.replaceAll("%player%", player.getName())
						.replaceAll("%uninvited%", target.getName()));
				
				break;
			}
			
			case "join": {				
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <guildName>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() != null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = new Guild(args[1]);
				
				if(!guild.doesExists()) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.guild_not_found"));
					return true;
				}
				
				if(!guild.getInvites().contains(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_invite"));
					return true;
				}
				
				KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
				
				if(kingdom == null || !KingdomHandler.isSimiliarKingdom(kingdom, guild.getKingdom())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_same_kingdom"));
					return true;
				}
				
				guild.getInvites().remove(player.getUniqueId());
				guild.getMembers().add(player.getUniqueId());
				guild.save();
				
				kingdomPlayer.setGuild(guild);
				kingdomPlayer.save();
				
				player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.joined")
						.replaceAll("%guild_name%", guild.getName())
						.replaceAll("%tag%", guild.getTag()));
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_joined")
						.replaceAll("%player%", player.getName()));
				
				break;
			}

			case "kick": {
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <playerName>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
				
				if(offlinePlayer == null) {
					player.sendMessage(ChatColor.RED + "Player named '" + args[1] + "' never joined the server.");
					return true;
				}
				
				KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(offlinePlayer.getUniqueId());
				
				if(kingdomTarget.getGuild() == null || !kingdomPlayer.getGuild().getName().toLowerCase().equals(kingdomTarget.getGuild().getName().toLowerCase())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.not_from_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(!guild.getLeader().equals(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.only_leaders"));
					return true;
				}
				
				if(offlinePlayer.getUniqueId().equals(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.cannot_kick_yourself"));
					return true;
				}
				
				guild.getMembers().remove(offlinePlayer.getUniqueId());
				guild.save();
				
				kingdomTarget.setGuild(null);
				kingdomTarget.save();
				
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_kicked")
						.replaceAll("%kicked%", offlinePlayer.getName())
						.replaceAll("%player%", player.getName()));
				
				if(offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
					offlinePlayer.getPlayer().sendMessage(ConfigUtils.getFormattedValue("messages.guild.kicked")
							.replaceAll("%guild_name%", guild.getName())
							.replaceAll("%tag%", guild.getTag())
							.replaceAll("%player%", player.getName()));
				}
				
				break;
			}
			
			case "deposit": {
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <amount>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();

				if(guild.getBank() == GuildHandler.STORAGE) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.storage_full"));
					return true;
				}
				
				if (!args[1].matches("(0|[1-9]\\d*)")) {
					player.sendMessage(ChatColor.GRAY + "The number " + args[1] + " was not a valid number!");
					return true;
				}
				
				int depositAmount = Integer.parseInt(args[1]);
				
				if(kingdomPlayer.getInfluence() < depositAmount) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.dont_have_influence"));
					return true;
				}
				
				int influenceTransfered = depositAmount;
				
				if((guild.getBank() + influenceTransfered) > GuildHandler.STORAGE) {
					influenceTransfered = GuildHandler.STORAGE - guild.getBank();
					
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.deposit_filled").replaceAll("%deposit%", influenceTransfered + ""));
					
					guild.setBank(guild.getBank() + influenceTransfered);
					guild.save();
				} else {
					guild.setBank(guild.getBank() + influenceTransfered);
					guild.save();
					
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.deposit_added")
							.replaceAll("%deposit%", influenceTransfered + "")
							.replaceAll("%bank%", guild.getBank() + ""));
				}
				
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_deposited")
						.replaceAll("%player%", player.getName())
						.replaceAll("%deposit%", influenceTransfered + "")
						.replaceAll("%bank%", guild.getBank() + ""));
				
				kingdomPlayer.setInfluence(kingdomPlayer.getInfluence() - influenceTransfered);
				kingdomPlayer.save();
				
				break;
			}
			
			case "withdraw": {
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <amount>");
					return true;
				}
				
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();

				if(guild.getBank() < 1) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.storage_empty"));
					return true;
				}
				
				if (!args[1].matches("(0|[1-9]\\d*)")) {
					player.sendMessage(ChatColor.GRAY + "The number " + args[1] + " was not a valid number!");
					return true;
				}
				
				int withdrawAmount = Integer.parseInt(args[1]);
				int influenceTransfered = withdrawAmount;
				
				if((guild.getBank() - influenceTransfered) < 1) {					
					influenceTransfered = guild.getBank();
					
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.withdraw_emptied")
							.replaceAll("%withdraw%", influenceTransfered + ""));
					
					guild.setBank(guild.getBank() - influenceTransfered);
					guild.save();
				} else {
					guild.setBank(guild.getBank() - influenceTransfered);
					guild.save();
					
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.withdraw_added")
							.replaceAll("%withdraw%", influenceTransfered + "")
							.replaceAll("%bank%", guild.getBank() + ""));
				}
				
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_withdrawed")
						.replaceAll("%player%", player.getName())
						.replaceAll("%withdraw%", influenceTransfered + "")
						.replaceAll("%bank%", guild.getBank() + ""));
				
				kingdomPlayer.setInfluence(kingdomPlayer.getInfluence() - influenceTransfered);
				kingdomPlayer.save();
				
				break;
			}
			
			case "leave": {
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(guild.getLeader().equals(player.getUniqueId())) {
					player.sendMessage(ChatColor.RED + "You are the leader, use /" + label + " disband if you want to leave.");
					return true;
				}
								
				guild.getMembers().remove(player.getUniqueId());				
				guild.save();
				
				kingdomPlayer.setGuild(null);
				kingdomPlayer.save();
				
				guild.broadcast(ConfigUtils.getFormattedValue("messages.guild.guild_broadcast.player_left")
						.replaceAll("%player%", player.getName()));
				player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.guild_leave")
						.replaceAll("%guild_name%", guild.getName())
						.replaceAll("%tag%", guild.getTag()));
				
				break;
			}
			
			case "info": {
				if(args.length < 2) {
					player.sendMessage(ChatColor.RED + "/" + label + " " + args[0] + " <guildName>");
					return true;
				}
				
				Guild guild = new Guild(args[1]);
				
				if(!guild.doesExists()) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.guild_not_found"));
					return true;
				}
				
				player.sendMessage(ChatColor.GREEN + guild.getName() + " [" + guild.getTag() + "] [" + guild.getOnlinePlayers().size() + "/" + guild.getMembers().size() + "] ");
				player.sendMessage(ChatColor.YELLOW + "Leader: " + guild.formatPlayer(guild.getLeader()));
				
				String members = "";
								
				for(UUID uuid : guild.getMembers()) {
					members += guild.formatPlayer(uuid) + ChatColor.GRAY + ", ";
				}
				
				player.sendMessage(ChatColor.YELLOW + "Members: " + members);
				player.sendMessage(ChatColor.YELLOW + "Influence Bank: " + guild.getBank());
				player.sendMessage(ChatColor.YELLOW + "Total Kills: " + guild.getKills());
				player.sendMessage(ChatColor.YELLOW + "Total Deaths: " + guild.getDeaths());
				player.sendMessage(ChatColor.YELLOW + "Total KDR: " + guild.getKdrFormat());
				player.sendMessage(ChatColor.YELLOW + "Kingdom: " + guild.getKingdom().getDisplayName());
				
				for (String message : ConfigUtils.getFormattedValueList("messages.guild.info")) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', message
							.replaceAll("%guild_name%", guild.getName())
							.replaceAll("%tag%", guild.getTag())
							.replaceAll("%online%", guild.getOnlinePlayers().size() + "")
							.replaceAll("%max%", guild.getMembers().size() + "")
							.replaceAll("%leader%", guild.formatPlayer(guild.getLeader()))
							.replaceAll("%members%", members))
							.replaceAll("%bank%", guild.getBank() + "")
							.replaceAll("%kills%", guild.getKills() + "")
							.replaceAll("%deaths%", guild.getDeaths() + "")
							.replaceAll("%kdr%", guild.getKdrFormat() + "")
							.replaceAll("%kingdom%", guild.getKingdom().getDisplayName()));
				}

				break;
			}
			
			case "disband": {
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(!guild.getLeader().equals(player.getUniqueId())) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.only_leaders"));
					return true;
				}
				
				guild.disband();
				
				player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.disband"));
				
				break;
			}
			
			case "invites": {
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.no_guild"));
					return true;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if(guild.getInvites().size() == 0) {
					player.sendMessage(ChatColor.RED + "No one is invited to your guild.");
					return true;
				}
				
				player.sendMessage(ChatColor.GRAY + "All players invited to your guild:");
				player.sendMessage("");
				for (UUID uuid : guild.getInvites()) {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
					
					player.sendMessage(ChatColor.GREEN + offlinePlayer.getName());
				}
				
				break;
			}
			
			default: {
				for(String line : ConfigUtils.getFormattedValueList("messages.guild.help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
				}
				
				/*sender.sendMessage(ChatColor.GRAY + "You don't have a guild, heres some help about them!");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " create <name> <TAG>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " invite <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " uninvite <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " join <guildName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " kick <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " deposit <amount>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " withdraw <amount>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " leave");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " info <playerName>");
				sender.sendMessage(ChatColor.GRAY + "/" + label + " invites");

				sender.sendMessage(ChatColor.GRAY + "/" + label + " disband");*/

				break;
			}
		}
		
		return false;
	}

}
