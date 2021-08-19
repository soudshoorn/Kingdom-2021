package me.niko.kingdom.events.breakthecore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.LocationUtils;

public class BreakTheCore {
	
	@Getter @Setter private Location blockLocation;
	@Getter @Setter private HashMap<KingdomConstructor, Integer> breaks;
	@Getter @Setter private int health;
	@Getter @Setter private boolean active;

	@Getter private File file = new File(Kingdom.getInstance().getDataFolder(), "btc_locations.yml");
	
	public BreakTheCore() {
		if(!file.exists()) {
			YamlConfiguration yamlConfiguration = new YamlConfiguration();
			
			this.blockLocation = null;
			this.active = false;
			
			yamlConfiguration.set("block_location", LocationUtils.fromLocToString(this.blockLocation));
			yamlConfiguration.set("active", this.active);
			yamlConfiguration.set("last_winner_kingdom", "null");
			yamlConfiguration.set("last_block_breaken_player", "null");

			try {
				yamlConfiguration.save(this.file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
			
			this.blockLocation = LocationUtils.fromStrToLocation(yamlConfiguration.getString("block_location"));
			this.active = yamlConfiguration.getBoolean("active");
		}
	}
	
	public void start() {
		if(this.blockLocation == null) {
			return;
		}
		
		if(Kingdom.getInstance().getEventConstants().getActiveBTCs().size() != 0) {
			return;
		}
		
		this.active = true;
		
		breaks = new HashMap<>();
		
		for(KingdomConstructor kingdom : KingdomHandler.getKingdoms()) {
			breaks.put(kingdom, this.health);
		}
		
		ConfigUtils.getFormattedValueList("messages.events.break_the_core.started_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m)));
		
		Kingdom.getInstance().getEventConstants().getActiveBTCs().add(this);
	}
	
	public void stop() {
		this.active = false;
		
		Kingdom.getInstance().getEventConstants().getActiveBTCs().clear();
	}
	
	public void save() {
		YamlConfiguration yamlConfiguration = new YamlConfiguration();

		yamlConfiguration.set("block_location", LocationUtils.fromLocToString(this.blockLocation));
		yamlConfiguration.set("active", this.active);
		
		try {
			yamlConfiguration.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleWinner(Player player, KingdomConstructor kingdom) {
		ConfigUtils.getFormattedValueList("messages.events.break_the_core.ended_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m
						.replaceAll("%kingdom%", kingdom.getDisplayName())
						.replaceAll("%player%", player.getName()))));
				
		this.stop();
		this.save();
	}
}
