package me.niko.kingdom.events.bountyhunters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.EventConstants;
import me.niko.kingdom.utils.ConfigUtils;

public class BountyHunters {
	@Getter private String name;
	
	@Getter @Setter private String[] cappingPlayers;
	@Getter @Setter private int[] times;
	@Getter private HashMap<String, KingdomConstructor> cappedZone;
	@Getter private ArrayList<String> zones;
	@Getter private int defaultCapTime = 60 * 7;
	@Getter private boolean active;
	@Getter private BukkitTask task;

	public BountyHunters(String name) {
		this.name = name;
		
		zones = new ArrayList<>();
		
		zones.add("one");
		zones.add("two");
		zones.add("three");
		zones.add("four");
		zones.add("five");
		zones.add("six");
	}
	
	public void start(int time) {
		this.defaultCapTime = time;
		cappedZone = new HashMap<>();
		active = true;
		
		cappingPlayers = new String[zones.size()];
		
		times = new int[zones.size()];
		
		for(int i = 0; i < zones.size(); i++) {
			cappingPlayers[i] = null;
			times[i] = defaultCapTime;
		}
		
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				for(String zone : zones) {
					if(getCapper(zone) != null) {
						Player player = Bukkit.getPlayer(getCapper(zone));
						KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
						KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

						if(player == null
								|| !isCapping(player, zone)
								|| player.isDead()) {
							setCapper(null, zone);
							setTime(defaultCapTime, zone);
						} else {
							if(getTime(zone) <= 0) {
								setTime(defaultCapTime, zone);
								
								player.sendMessage(ConfigUtils.getFormattedValue("messages.events.bounty_hunters.capped")
										.replaceAll("%zone%", zone.toUpperCase()));
								
								cappedZone.put(zone, kingdom);
								
								if(cappedZone.size() == zones.size()) {
									handleWinner(player);
								}
								
								setCapper(null, zone);
								
								continue;
							}
							
							setTime(getTime(zone) - 1, zone);
						}
					} else {
						ArrayList<Player> onCapPlayers = new ArrayList<>();
						
						for(Player player : Bukkit.getOnlinePlayers()) {
							KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
							KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
							
							if(isCapping(player, zone)
									&& !player.isDead()
									&& kingdom != null
									&& !(cappedZone.containsKey(zone))) {
								onCapPlayers.add(player);
							}
						}
						
						Collections.shuffle(onCapPlayers);
						
						if(onCapPlayers.size() != 0) {
							setCapper(onCapPlayers.get(0).getName(), zone);
							onCapPlayers.get(0).sendMessage(ConfigUtils.getFormattedValue("messages.events.bounty_hunters.capping")
									.replaceAll("%zone%", zone.toUpperCase()));
							setTime(defaultCapTime, zone);
						}
					}
				}
			}
		}.runTaskTimer(Kingdom.getInstance(), 20L, 20L);
		
		//Bukkit.broadcastMessage(ChatColor.GREEN + "BountyHunters event has been started.");
		
		ConfigUtils.getFormattedValueList("messages.events.bounty_hunters.started_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m)));
		
		//KingdomPlugin.r.attackManager = false;
		Kingdom.getInstance().getEventConstants().getActiveBountyHunters().add(this);
	}
	
	public void stop() {
		BountyHunters bountyHunters = Kingdom.getInstance().getEventConstants().getActiveBountyHunters().stream().filter(bh -> bh.getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);

		bountyHunters.active = false;
		bountyHunters.task.cancel();
		
		//KingdomPlugin.r.attackManager = true;
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Kingdom.getInstance().getEventConstants().getActiveBountyHunters().remove(bountyHunters);
			}
		}.runTaskLater(Kingdom.getInstance(), 20 * 3);
	}
	
	public void handleWinner(Player player) {
		int position = 0;

		for(String line : ConfigUtils.getFormattedValueList("messages.events.bounty_hunters.ended_broadcast")) {
			if(line.contains("%format%")) {
				for(Entry<String, KingdomConstructor> entry : cappedZone.entrySet()) {
					position++;
					String zone = entry.getKey();
					KingdomConstructor kingdom = entry.getValue();
					
					//Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + (position++) + ". " + kingdom.getDisplayName()));//KingdomPlugin.r.getPlayerKingdomWithColor(kingdom)));
					
					Bukkit.broadcastMessage(ConfigUtils.getFormattedValue("messages.events.bounty_hunters.winners_format")
							.replaceAll("%position%", position + "")
							.replaceAll("%kingdom%", kingdom.getDisplayName()));
				}
				
				continue;
			}
			
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		stop();
	}
	
	public boolean isCapping(Player player, String zone) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "bountyhunters.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("bountyhunters." + this.name + "." + zone + ".world"));
		
		if(player.getLocation().getWorld() != w) {
			return false;
		}
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".x1"),
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".y1"),
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".x2"),
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".y2"),
				yamlConfiguration.getDouble("bountyhunters." + this.name + "." + zone + ".z2"));
		
		CuboidSelection s = new CuboidSelection(w, locA, locB);
			
		Vector min = s.getNativeMinimumPoint();
		Vector max = s.getNativeMaximumPoint();
		
		return 	   min.getBlockX() <= player.getLocation().getBlockX()
				&& max.getBlockX() >= player.getLocation().getBlockX() && min.getBlockY() <= player.getLocation().getBlockY()
				&& max.getBlockY() >= player.getLocation().getBlockY() && min.getBlockZ() <= player.getLocation().getBlockZ()
				&& max.getBlockZ() >= player.getLocation().getBlockZ();
	}
	
	public void setCapper(String player, String zone) {
		this.cappingPlayers[zones.indexOf(zone)] = player;
	}
	
	public String getCapper(String zone) {
		return this.cappingPlayers[zones.indexOf(zone)];
	}
	
	public void setTime(int time, String zone) {
		this.times[zones.indexOf(zone)] = time;
	}
	
	public int getTime(String zone) {
		return this.times[zones.indexOf(zone)];
	}
}
