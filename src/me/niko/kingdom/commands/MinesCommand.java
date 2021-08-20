package me.niko.kingdom.commands;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.niko.kingdom.Kingdom;

public class MinesCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a player.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(!player.isOp()) {
			player.sendMessage(ChatColor.RED + "No permission.");
			
			return true;
		}
		
		if(args.length == 0) {
			player.sendMessage(ChatColor.RED + "/" + label + " reset");
			return true;
		}
		
		player.sendMessage(ChatColor.YELLOW + "Regenerating " + Kingdom.getInstance().getBrokenBlocks().size() + " blocks.");
		
		for(Entry<Block, Entry<Material, Byte>> entry : Kingdom.getInstance().getBrokenBlocks().entrySet()) {
		    Block key = entry.getKey();
		    Entry<Material, Byte> value = entry.getValue();

		    key.setType(value.getKey());
		    key.setData(value.getValue());
		}
		
		for(BukkitTask task : Kingdom.getInstance().getTasks()) {
			task.cancel();
		}
		
		Kingdom.getInstance().getBrokenBlocks().clear();
		Kingdom.getInstance().getBrokenBlocksCD().clear();
		Kingdom.getInstance().getTasks().clear();

		player.sendMessage(ChatColor.GREEN + "All blocks got regenerated.");
		
		return false;
	}

}

