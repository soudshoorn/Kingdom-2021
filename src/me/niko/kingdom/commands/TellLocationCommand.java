package me.niko.kingdom.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lunarclient.bukkitapi.object.LCWaypoint;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.BaseComponentSerializer;
import net.minecraft.server.v1_8_R3.ChatMessage;

public class TellLocationCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
			return true;
		}
		
		Player player = (Player) sender;
		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		
		if(kingdomPlayer.getKingdom() == null) {
			player.sendMessage(ChatColor.RED + "You are not in a kingdom.");
			
			return true;
		}
		
		KingdomConstructor kingdomConstructor = kingdomPlayer.getKingdom();
		
		ArrayList<Player> onlinePlayers = new ArrayList<>(KingdomHandler.getOnlinePlayersMap().getOrDefault(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), new ArrayList<Player>()));
		
		Location playerLocation = player.getLocation();
		
		for (Player target : onlinePlayers) {
			String tellLocation = ConfigUtils.getFormattedValue("messages.tell_location.format")
						.replaceAll("%kingdom%", kingdomConstructor.getDisplayName())
						.replaceAll("%player%", player.getName())
						.replaceAll("%x%", playerLocation.getBlockX() + ".0")
						.replaceAll("%y%", playerLocation.getBlockY() + ".0")
						.replaceAll("%z%", playerLocation.getBlockZ() + ".0");
			
			TextComponent message = new TextComponent(tellLocation);
			
			if(Kingdom.getLunarClientAPI().getInstance().isRunningLunarClient(target)) {
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/set_waypoint_lc_lol_ " + player.getName()));				
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent("§7Click to set a waypoint at his location.")}));
				
				player.spigot().sendMessage(message);
			} else {
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{ new TextComponent("§cYou need to be on §7Lunar-Client §cto set a waypoint!")}));
				
				player.spigot().sendMessage(message);
			}
		}
		
		return false;
	}

}
