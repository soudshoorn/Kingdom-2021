package me.niko.kingdom.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;

public class StatsCommand implements CommandExecutor { 
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "/" + label + " <playerName>");
				
				return true;
			}
			
			Player player = (Player) sender;
			
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
						
			statsMessage(player, player);

			return true;
		} else {
			Player target = Bukkit.getPlayer(args[0]);
			
			if(target == null) {
				sender.sendMessage(ChatColor.RED + "Player named '" + args[0] + "' not found.");
				
				return true;
			}
			
			statsMessage(sender, target);
		}
		
		
		return false;
	}
	
	public void statsMessage(CommandSender sender, Player target) {
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(target);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

		for (String line : ConfigUtils.getFormattedValueList("messages.stats")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line
					.replaceAll("%player%", target.getName())
					.replaceAll("%kills%", kingdomPlayer.getKills() + "")
					.replaceAll("%deaths%", kingdomPlayer.getDeaths() + "")
					.replaceAll("%kdr%", kingdomPlayer.getKdrFormat())
					.replaceAll("%influence%", kingdomPlayer.getInfluence() + "")
					.replaceAll("%kingdom%", kingdom == null ? "None" : kingdom.getDisplayName())));
		}
	}

}
