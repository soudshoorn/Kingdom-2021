package me.niko.kingdom.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;

public class ListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("kingdom.list")) {
			sender.sendMessage(ChatColor.RED + "No permission");
			return true;
		}

		ArrayList<KingdomConstructor> kingdoms = new ArrayList<>(KingdomHandler.getKingdoms());
		
		kingdoms.add(0, null);
		
		for(KingdomConstructor kingdomConstructor : kingdoms) {
			if(kingdomConstructor == null) {
				continue;
			}
			
			List<String> players = KingdomHandler.onlinePlayersMap.getOrDefault(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), new ArrayList<Player>()).stream().map(player -> player.getName()).collect(Collectors.toList());

			sender.sendMessage((kingdomConstructor == null ? "No Kingdom" : kingdomConstructor.getDisplayName()) + ChatColor.WHITE + " [" + players.size() + "]: " + (players.size() == 0 ? "None" : StringUtils.join(players, ChatColor.GRAY + ", " + ChatColor.WHITE)) + ChatColor.GRAY + ".");
		}
		
		/*KingdomHandler.onlinePlayersMap.forEach((kingdomConstructor, players2) -> {
			List<String> players = players2.stream().map(player -> player.getName()).collect(Collectors.toList());

			sender.sendMessage(kingdomConstructor.equals("null") ? "No Kingdom" : new KingdomConstructor(kingdomConstructor).getDisplayName() + ChatColor.WHITE + " [" + players.size() + "]: " + StringUtils.join(players, ChatColor.GRAY + ", " + ChatColor.WHITE) + ChatColor.GRAY + ".");
		});*/
		
		return false;
	}
	
	

}
