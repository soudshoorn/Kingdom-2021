package me.niko.kingdom.listeners;

import java.util.ArrayList;
import java.util.UUID;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.guilds.Guild;
import me.niko.kingdom.guilds.GuildHandler;
import me.niko.kingdom.utils.ConfigUtils;

public class ChatListeners implements Listener {
	
	/*private double RANGED_CHAT_RADIUS = 200.0;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		if(!event.getMessage().startsWith("!!") && event.getMessage().startsWith("!")) { //Ranged Chat
			ArrayList<Player> players = getPlayersInRange(player, RANGED_CHAT_RADIUS);
			
			for(Player player2 : Kingdom.getInstance().getChat().getChatSpy()) {
				if(players.contains(player2)) {
					continue;
				}
				
				players.add(player2);
			}
			
			for(Player target : players) {
				
				if(Kingdom.getInstance().getChat().isMuted() && !target.hasPermission("kingdom.mutebypass")) {
					continue;
				}
				
				message(player, target, event.getMessage().substring(1).trim(), "ranged");
			}
			
			return;
		}
		
		for(Player target : event.getRecipients()) {			
			if(event.getMessage().startsWith("*")
					&& target.hasPermission("kingdom.staff")) {
				message(player, target, event.getMessage().substring(2).trim(), "staff");
				
				continue;
			}
			
			if(Kingdom.getInstance().getChat().isMuted() && !target.hasPermission("kingdom.mutebypass")) {
				continue;
			}
			
			KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
			
			if(event.getMessage().startsWith("!!")
					&& (target.hasPermission("kingdom.globalchat")
					|| Kingdom.getInstance().getChat().getChatSpy().contains(target))) { // Global Chat
				message(player, target, event.getMessage().substring(2).trim(), "global");
				
				continue;
			}
			
			if(kingdomPlayer.getKingdom().getName().equals(kingdomTarget.getKingdom().getName()) 
					|| Kingdom.getInstance().getChat().getChatSpy().contains(target)) { //By default they will chat in their Kingdom chat
				message(player, target, event.getMessage(), "kingdom");
				
				continue;
			}

			continue;
		}
	}*/
	
	private ArrayList<Player> getPlayersInRange(Player player, double range) {
		ArrayList<Player> players = new ArrayList<>();
		
		for(Entity entity : player.getNearbyEntities(range, range, range)) {
			if(!(entity instanceof Player)) {
				continue;
			}
			
			players.add(player);
		}
		
		return players;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		if(kingdomPlayer.getKingdom() == null && !player.isOp()) {
			player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.no_kingdom"));
			
			return;
		}
		
		String consoleOutput = "";
		
		for(Player target : event.getRecipients()) {
			KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);
			
			if(kingdomTarget == null && !target.isOp()) {
				continue;
			}
			
			if(event.getMessage().startsWith("@")) { // Guilds Chat
				if(kingdomPlayer.getGuild() == null) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.no_guild"));
					
					return;
				}
				
				Guild guild = kingdomPlayer.getGuild();
				
				if (GuildHandler.isSimiliarGuild(guild, kingdomTarget.getGuild()) || Kingdom.getInstance().getChat().getChatSpy().contains(target)) {
					message(player, target, event.getMessage().substring(1).trim(), "guilds", kingdomPlayer);
					consoleOutput = "[Guilds Chat] [" +  guild.getName() + "] [" + guild.getTag() + "] " + player.getName() + ": " + event.getMessage().substring(1).trim();
					
					continue;
				}
				
				continue;
			}
			
			if(event.getMessage().startsWith("!")) { // Roleplay Chat
				if (Kingdom.getInstance().getChat().isMuted() && !player.hasPermission("kingdom.mutechat.bypass")) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.muted"));
					
					return;
				}
				
				message(player, target, event.getMessage().substring(1).trim(), "roleplay", kingdomPlayer);
				consoleOutput = "[Roleplay Chat] " + player.getName() + ": " + event.getMessage().substring(1).trim();
				
				continue;
			}
			
			if(event.getMessage().startsWith("$")) { // Trade Chat
				if (Kingdom.getInstance().getChat().isMuted() && !player.hasPermission("kingdom.mutechat.bypass")) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.muted"));
					
					return;
				}
				
				message(player, target, event.getMessage().substring(1).trim(), "trade", kingdomPlayer);
				consoleOutput = "[Trade Chat] " + player.getName() + ": " + event.getMessage().substring(1).trim();

				continue;
			}
			
			if(event.getMessage().startsWith("%")) {
				if (Kingdom.getInstance().getChat().isMuted() && !player.hasPermission("kingdom.mutechat.bypass")) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.muted"));
					
					return;
				}
				
				if(target.hasPermission("kingdom.globalchat") || Kingdom.getInstance().getChat().getChatSpy().contains(target)) {
					message(player, target, event.getMessage().substring(1).trim(), "others", kingdomPlayer);
					consoleOutput = "[Restricted Chat] " + player.getName() + ": " + event.getMessage().substring(1).trim();

					continue;
				}
				
				continue;
			}
			
			KingdomConstructor kingdomConstructor = kingdomPlayer.getKingdom();
			
			if(KingdomHandler.isSimiliarKingdom(kingdomPlayer.getKingdom(), kingdomTarget.getKingdom())
					|| Kingdom.getInstance().getChat().getChatSpy().contains(target)) { //By default they will chat in their Kingdom chat
				if (Kingdom.getInstance().getChat().isMuted()) {
					player.sendMessage(ConfigUtils.getFormattedValue("messages.chat.muted"));
					
					return;
				}
				
				message(player, target, event.getMessage(), "kingdom", kingdomPlayer);
				consoleOutput = "[Kingdom Chat] [" + kingdomConstructor.getName() + "] " + player.getName() + ": " + event.getMessage().substring(1).trim();

				continue;
			}

			continue;
		}
		
		if(!consoleOutput.isEmpty()) {
			Bukkit.getLogger().info(ChatColor.stripColor(consoleOutput));
		}
	}
	
	public static String message(Player sender, Player viewer, String message, String type, KingdomPlayer kingdomPlayer) {
		String prefix = "";
		String suffix = "";
		
		//prefix = ChatColor.GRAY + "[" + type.toUpperCase().charAt(0) + type.substring(1).trim() + "] ";
		
		if(Kingdom.getInstance().getServer().getPluginManager().isPluginEnabled("Vault")) {			
			prefix = Kingdom.getVaultChat().getPlayerPrefix(sender);
			
			if(!Kingdom.getVaultChat().getPlayerSuffix(sender).isEmpty()) {
				suffix = Kingdom.getVaultChat().getPlayerSuffix(sender);
			}
		}
		
		//prefix = ChatColor.translateAlternateColorCodes('&', prefix);
		//suffix = ChatColor.translateAlternateColorCodes('&', suffix);

		//String format = prefix + sender.getName() + suffix + ChatColor.GRAY + ": " + (sender.isOp() ? ChatColor.translateAlternateColorCodes('&', message) : ChatColor.WHITE + message);

		Guild playerGuild = kingdomPlayer.getGuild();
		
		String format = ChatColor.translateAlternateColorCodes('&', ConfigUtils.getFormattedValue("messages.chat.formats." + type.toLowerCase() + "_chat")
				.replaceAll("%player%", sender.getName())
				.replaceAll("refix%", prefix)
				.replaceAll("%suffix%", suffix)
				.replaceAll("%guild_name%", playerGuild == null ? "None" : playerGuild.getName())
				.replaceAll("%guild_tag%", playerGuild == null ? "None" : playerGuild.getTag())
				.replaceAll("%kingdom_name%", kingdomPlayer.getKingdom().getDisplayName())
				.replaceAll("%kingdom_rank%", KingdomHandler.getRanks().get(kingdomPlayer.getKingdomRank()).getPrefix())
				.replaceAll("%message%", (sender.isOp() ? ChatColor.translateAlternateColorCodes('&', message) : ChatColor.WHITE + message)));
		
		viewer.sendMessage(format);
		
		return format;
	}

}
