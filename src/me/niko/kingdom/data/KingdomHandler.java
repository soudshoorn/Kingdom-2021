package me.niko.kingdom.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import lombok.Getter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.data.players.rank.KingdomRank;
import me.niko.kingdom.utils.ConfigUtils;

public class KingdomHandler {
		
	@Getter public static HashMap<String, ArrayList<Player>> onlinePlayersMap = new HashMap<>();
	@Getter public static HashMap<UUID, LCWaypoint> waypointsMap = new HashMap<>();
	@Getter public static List<KingdomConstructor> kingdoms = new ArrayList<>();

	public static void saveKingdoms() {
		for (KingdomConstructor kingdom : kingdoms) {
			kingdom.save();
		}
	}
	
	public static void updateKingdoms() {
		saveKingdoms();
		kingdoms.clear();
		
		File dir = new File(Kingdom.getInstance().getDataFolder() + "/kingdoms/");
				
		if(!dir.exists()) {
			return;
		}
		
		File[] directoryListing = dir.listFiles();
				
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String file = child.getName().replaceAll(".yml", "");
				KingdomConstructor kingdom = new KingdomConstructor(file);
				
				
				if(kingdom.doesExists()) {
					kingdoms.add(kingdom);
				}
			}
		}
		
		return;
	}
	
	public static KingdomConstructor getKingdom(Player player) {
		
		KingdomPlayer kingdomPlayer = getKingdomPlayer(player);
		
		if(kingdomPlayer.getKingdom() == null) {
			return null;
		}
		
		KingdomConstructor found = kingdoms.stream().filter(kgs -> kgs.getName().equals(kingdomPlayer.getKingdom().getName())).findFirst().orElse(null);
		
		if(found == null) {
			return null;
		}

		return kingdoms.get(kingdoms.indexOf(found));
	}
	
	public static KingdomConstructor getKingdom(KingdomPlayer kingdomPlayer) {
		if(kingdomPlayer.getKingdom() == null) {
			return null;
		}
		
		KingdomConstructor found = kingdoms.stream().filter(kgs -> kgs.getName().equals(kingdomPlayer.getKingdom().getName())).findFirst().orElse(null);
		
		if(found == null) {
			return null;
		}

		return kingdoms.get(kingdoms.indexOf(found));
	}
	
	public static KingdomConstructor getKingdom(String name) {
		KingdomConstructor found = kingdoms.stream().filter(kgs -> kgs.getName().toLowerCase().equals(name.toLowerCase())).findFirst().orElse(null);
		
		if(found == null) {
			return null;
		}

		return kingdoms.get(kingdoms.indexOf(found));
	}

	/* public static ArrayList<KingdomConstructor> getKingdoms() {
		ArrayList<KingdomConstructor> kingdoms = new ArrayList<>();
		
		File dir = new File(Kingdom.getInstance().getDataFolder() + "/kingdoms/");
				
		if(!dir.exists()) {
			return kingdoms;
		}
		
		File[] directoryListing = dir.listFiles();
				
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String file = child.getName().replaceAll(".yml", "");
				KingdomConstructor kingdom = new KingdomConstructor(file);
				
				
				if(kingdom.doesExists()) {
					kingdoms.add(kingdom);
				}
			}
		}
		
		return kingdoms;
	}*/
	
	public static KingdomConstructor getKingdomByLocation(Location location) {
		if(!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
			return null;
		}
		
		KingdomConstructor kingdom = null;
		
		Location downLocation = location.clone();
		downLocation.setY(2.0D);
		location = downLocation;
				
		for (KingdomConstructor kingdomConstructor : getKingdoms()) {
						
			byte data = kingdomConstructor.getWoolData();
			
			if(location.getBlock() == null
					|| location.getBlock().getType() == Material.AIR) {
				return null;
			}
			
			if(location.getBlock().getType() == Material.WOOL
					&& location.getBlock().getData() == data) {
				kingdom = kingdomConstructor;
				
				break;
			}
		}
		
		return kingdom;
	}
	
	public static KingdomPlayer getKingdomPlayer(Player player) {
		
		KingdomPlayer kingdomPlayer;
		
		if(!Kingdom.getInstance().getPlayersMap().containsKey(player.getUniqueId())) {
			System.out.println("Let me know if you see this in the console (nikch0)");

			kingdomPlayer = new KingdomPlayer(player);
			Kingdom.getInstance().getPlayersMap().put(player.getUniqueId(), kingdomPlayer);
			
			return kingdomPlayer;
		}
		
		kingdomPlayer = Kingdom.getInstance().getPlayersMap().get(player.getUniqueId());
		
		return kingdomPlayer;
	}
	
	public static KingdomPlayer getKingdomPlayer(UUID uuid) {
		
		KingdomPlayer kingdomPlayer;
		
		if(!Kingdom.getInstance().getPlayersMap().containsKey(uuid)) {
			System.out.println("Let me know if you see this in the console (nikch0)");

			kingdomPlayer = new KingdomPlayer(uuid);
			Kingdom.getInstance().getPlayersMap().put(uuid, kingdomPlayer);
			
			return kingdomPlayer;
		}
		
		kingdomPlayer = Kingdom.getInstance().getPlayersMap().get(uuid);
		
		return kingdomPlayer;
	}
	
	public static boolean isSimiliarKingdom(KingdomConstructor k1, KingdomConstructor k2) {
		if((k1 == null && k2 != null) || (k1 != null && k2 == null)) {
			return false;
		}
		
		if(k1 == null && k2 == null) {
			return true;
		}
		
		if(!k1.doesExists() || !k2.doesExists()) {
			return false;
		}
		
		return k1.getName().toLowerCase().equalsIgnoreCase(k2.getName().toLowerCase());
	}
	
	public static boolean isAllyWithKingdom(KingdomConstructor k1, KingdomConstructor k2) {
		if(k1 == null || k2 == null) {
			return false;
		}
		
		if(!k1.doesExists() || !k2.doesExists()) {
			return false;
		}
		
		boolean found = false;
		
		//No need to loop if its non l0l
		if(k1.getAllies().size() == 0 || k2.getAllies().size() == 0) {
			return found;
		}
		
		for(KingdomConstructor kingdomConstructor : k1.getAllies()) {
			if(isSimiliarKingdom(kingdomConstructor, k2)) {
				KingdomConstructor v = k2.getAllies().stream().filter(k -> k.getName().equals(k1.getName())).findFirst().orElse(null);
				
				if(v != null) {
					found = true;
				}
			}
		}
		
		return found;
	}
	
	public static void addOnlinePlayer(Player player, KingdomConstructor kingdomConstructor) {
		ArrayList<Player> players = onlinePlayersMap.getOrDefault(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), new ArrayList<Player>());
		
		if(!players.contains(player)) {
			players.add(player);
			
			onlinePlayersMap.put(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), players);
		}		
	}
	
	public static void removeOnlinePlayer(Player player, KingdomConstructor kingdomConstructor) {
		ArrayList<Player> players = onlinePlayersMap.getOrDefault(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), new ArrayList<Player>());
		
		if(players.contains(player)) {
			players.remove(player);
			
			onlinePlayersMap.put(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), players);
		}
	}
	
	public static int getOnlinePlayers(KingdomConstructor kingdomConstructor) {
		ArrayList<Player> players = onlinePlayersMap.getOrDefault(kingdomConstructor == null ? "null" : kingdomConstructor.getName(), new ArrayList<Player>());
		
		return players.size();
	}
	
	public static boolean influenceCheck(Player player, int build, Location location, Block block) {
        List<String> rgs = WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegionsIDs(new Vector(location.getX(), location.getY(), location.getZ()));

        for(String rg : rgs) {
        	if(rg.startsWith("influence_")) {
        		return false;
        	}
        }
        
        KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
        KingdomConstructor inKingdom = getKingdomByLocation(location);
        
        if(inKingdom != null && !inKingdom.getName().equals(kingdomPlayer.getKingdom().getName()) && player.getGameMode() != GameMode.CREATIVE) {
        	int price = build == 0 ? Kingdom.getInstance().getConfig().getInt("influence_settings.place_cost") : (build == 1 ? Kingdom.getInstance().getConfig().getInt("influence_settings.break_cost") : Kingdom.getInstance().getConfig().getInt("influence_settings.use_cost"));
        	int balance = kingdomPlayer.getInfluence();
        	
        	if (block.getType() == Material.GRASS && build == 1)
                return false;

        	if (block.getType() == Material.LONG_GRASS && build == 1)
                return false;

        	if (block.getType() == Material.DOUBLE_PLANT && build == 1)
                return false;
        	
        	if(balance >= price) {
        		kingdomPlayer.setInfluence(kingdomPlayer.getInfluence() - price);
        		kingdomPlayer.save();
        		
        		String type = build == 0 ? "placing" : build == 1 ? "breaking" : "interacting";
        		
        		player.sendMessage(ConfigUtils.getFormattedValue("messages.influence.type." + type + "")
        				.replaceAll("%influence%", price + "")
        				.replaceAll("%new_influence%", kingdomPlayer.getInfluence() + ""));
        		
        		//player.sendMessage(ChatColor.RED + "" + price + " has been taken from your account for " + type + ". New influence " + kingdomPlayer.getInfluence());
        		return false;
        	}
        	
        	player.sendMessage(ConfigUtils.getFormattedValue("messages.influence.not_enough")
    				.replaceAll("%price%", (price - balance) + ""));
        	
        	return true;
        } else {
        	return false;
        }
	}
	
	public static ArrayList<KingdomRank> getRanks() {
		ArrayList<KingdomRank> ranks = new ArrayList<>();
		
		for (String category : Kingdom.getInstance().getConfig().getConfigurationSection("settings.kingdom_ranks").getKeys(false)) {
			ConfigurationSection section = Kingdom.getInstance().getConfig().getConfigurationSection("settings.kingdom_ranks." + category);
			
			ranks.add(new KingdomRank(category, section.getString("name"), section.getString("prefix")));
		}
		
		return ranks;
	}
}
