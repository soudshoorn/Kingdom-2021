package me.niko.kingdom.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.utils.ConfigUtils;

public class ChatCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.hasPermission("kingdom.chat.manage")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		
		if(args.length == 0) {
			
			sender.sendMessage(ChatColor.RED + "/" + label + " mute");
			sender.sendMessage(ChatColor.RED + "/" + label + " clear");
			sender.sendMessage(ChatColor.RED + "/" + label + " spy");

			return true;
		}
		
		switch (args[0].toLowerCase()) {
			case "mute":
				String type = Kingdom.getInstance().getChat().isMuted() ? "unmuted" : "muted";
				Kingdom.getInstance().getChat().setMuted(!Kingdom.getInstance().getChat().isMuted());

				Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.chat.has_been_" + type)
						.replaceAll("%player%", sender.getName()));
				
				break;
			case "clear":
				
				for(Player target : Bukkit.getOnlinePlayers()) {
					
					if(target.hasPermission("kingdom.clear.bypass")) {
						target.sendMessage(ConfigUtils.getFormattedValue("messages.chat.bypass_clear"));
						
						continue;
					}
					
					target.sendMessage(new String[101]);
				}
				
				Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.chat.chat_clear")
						.replaceAll("%player%", sender.getName()));
				
				break;
			case "spy":
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
					return true;
				}
				
				Player player = (Player) sender;
				
				if(Kingdom.getInstance().getChat().getChatSpy().contains(player.getUniqueId())) {
					Kingdom.getInstance().getChat().getChatSpy().remove(player.getUniqueId());
					player.sendMessage(ChatColor.RED + "You are no longer spying the chats.");
				} else {
					Kingdom.getInstance().getChat().getChatSpy().add(player.getUniqueId());
					player.sendMessage(ChatColor.GREEN + "You are now spying the chats.");
				}
				
				break;
			default:
				sender.sendMessage(ChatColor.RED + "/" + label + " mute");
				sender.sendMessage(ChatColor.RED + "/" + label + " clear");
				sender.sendMessage(ChatColor.RED + "/" + label + " spy");
				
				break;
		}
		
		return false;
	}

}
