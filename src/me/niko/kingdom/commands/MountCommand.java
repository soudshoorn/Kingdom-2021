package me.niko.kingdom.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.mount.HorseHandler;
import net.minelink.ctplus.TagManager;

public class MountCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player for this one.");
			return true;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("kingdom.mount")) {
			player.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}

		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		if (kingdomPlayer.getKingdom() == null) {
			player.sendMessage(ChatColor.RED + "You need to have a kingdom in order to spawn a horse.");
			return true;
		}

		if (player.isInsideVehicle() && player.getVehicle() != null && player.getVehicle() instanceof Horse) {
			Horse entity = (Horse) player.getVehicle();
			if (HorseHandler.getHorseSpawned().containsValue(entity)) {
				entity.remove();
				player.sendMessage(ChatColor.GRAY + "You unmounted!");

				if (HorseHandler.getHorseSpawned().containsKey(player)) {
					HorseHandler.getHorseSpawned().remove(player);
				}

				player.playSound(player.getLocation(), Sound.DIG_GRASS, 5.0F, 1.0F);
				return false;
			}
		}

		if (Kingdom.getInstance().getCombatTagPlus() != null) {
			TagManager tagManager = Kingdom.getInstance().getCombatTagPlus().getTagManager();
			boolean isTagged = tagManager.isTagged(player.getUniqueId());
			
			if (isTagged) {
				player.sendMessage(ChatColor.GRAY + "You can't mount up in combat!");
				player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);
				
				return false;
			}
		}

		HorseHandler horseHandler = new HorseHandler();
		
		if (horseHandler.isEnemiesNearby(player)) {
			player.sendMessage(ChatColor.GRAY + "You can't mount while enemy players are nearby!");
			player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);
			
			return true;
		}
		
		
		horseHandler.mountCountdown(player);
		return true;
	}
}
