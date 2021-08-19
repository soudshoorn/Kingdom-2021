package me.niko.kingdom.events.koth;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
import me.niko.kingdom.utils.ConfigUtils;

public class Koth {
	@Getter private String name;
	@Getter @Setter private String capper;
	@Getter @Setter private int time;
	@Getter private int defaultCapTime = 60 * 15;
	@Getter private boolean active;
	@Getter private BukkitTask task;
	@Getter private CuboidSelection region;

	public Koth(String name) {
		this.name = name;
		
		File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("koth." + this.name + ".cap.world"));
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("koth." + this.name + ".cap.x1"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.y1"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("koth." + this.name + ".cap.x2"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.y2"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.z2"));
		
		this.region = new CuboidSelection(w, locA, locB);
	}
	
	public void start(int seconds) {
		this.defaultCapTime = seconds;
		this.active = true;
		
		this.time = seconds;
		
		this.task = new BukkitRunnable() {
			
			@Override
			public void run() {										
				if(getCapper() != null) {
					Player player = Bukkit.getPlayer(getCapper());
					KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

					if(player == null
							|| !isCapping(player)
							|| player.isDead()) {
						setCapper(null);
						setTime(defaultCapTime);
					} else {
						if(getTime() <= 0) {
							setTime(defaultCapTime);
							
							handleWinner(player, kingdomPlayer.getKingdom());
																							
							setCapper(null);
							
							return;
						}
							
						setTime(getTime() - 1);
					}
				} else {
					ArrayList<Player> onCapPlayers = new ArrayList<>();
						
					for(Player player : Bukkit.getOnlinePlayers()) {
						KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
							
						if(isCapping(player)
								&& !player.isDead()
								&& kingdomPlayer.getKingdom() != null) {
							onCapPlayers.add(player);
						}
					}
						
					Collections.shuffle(onCapPlayers);
						
					if(onCapPlayers.size() != 0) {
						Player capper = onCapPlayers.get(0);
						
						setCapper(capper.getName());
						capper.sendMessage(ConfigUtils.getFormattedValue("messages.events.koth.capping").replaceAll("%koth%", name.toUpperCase()));
						
						setTime(defaultCapTime);
					}
				}
			}
		}.runTaskTimer(Kingdom.getInstance(), 20L, 20L);
		
		ConfigUtils.getFormattedValueList("messages.events.koth.started_broadcast")
			.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m.replaceAll("%koth%", this.name.toUpperCase()))));
		
		
		Kingdom.getInstance().getEventConstants().getActiveKoths().add(this);
	}
	
	public void stop() {
		Koth koth = Kingdom.getInstance().getEventConstants().getActiveKoths().stream().filter(c -> c.getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
		
		koth.active = false;
		koth.task.cancel();
		
		Kingdom.getInstance().getEventConstants().getActiveKoths().remove(koth);
	}
	
	public void handleWinner(Player player, KingdomConstructor kingdom) {
		ConfigUtils.getFormattedValueList("messages.events.koth.ended_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m
						.replaceAll("%kingdom%", kingdom.getDisplayName())
						.replaceAll("%player%", player.getName()))));
		
		stop();
	}
	
	public boolean isCapping(Player player) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "koth.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("koth." + this.name + ".cap.world"));
		
		if(player.getLocation().getWorld() != w) {
			return false;
		}
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("koth." + this.name + ".cap.x1"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.y1"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("koth." + this.name + ".cap.x2"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.y2"),
				yamlConfiguration.getDouble("koth." + this.name + ".cap.z2"));
		
		CuboidSelection s = new CuboidSelection(w, locA, locB);
			
		Vector min = s.getNativeMinimumPoint();
		Vector max = s.getNativeMaximumPoint();
		
		return 	   min.getBlockX() <= player.getLocation().getBlockX()
				&& max.getBlockX() >= player.getLocation().getBlockX() && min.getBlockY() <= player.getLocation().getBlockY()
				&& max.getBlockY() >= player.getLocation().getBlockY() && min.getBlockZ() <= player.getLocation().getBlockZ()
				&& max.getBlockZ() >= player.getLocation().getBlockZ();
	}
}
