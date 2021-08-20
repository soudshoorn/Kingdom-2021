package me.niko.kingdom.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.history.changeset.ArrayListHistory;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.LocationUtils;

public class KingdomConstructor {
	
	@Getter private String name;
	@Getter @Setter private byte woolData = 0;
	@Setter private String displayName = "";
	@Getter @Setter private Location spawnLocation = null;
	@Getter @Setter private Location boatExitLocation = null;
	@Getter @Setter private int points = 0;
	@Getter @Setter private ArrayList<KingdomConstructor> allies = new ArrayList<>();
	
	@Getter @Setter private boolean exists = false;
	@Getter @Setter private boolean creating = false;

	public KingdomConstructor(String name) {
		this.name = name;
		
		File file = new File(Kingdom.getInstance().getDataFolder() + "/kingdoms/", this.name.toLowerCase() + ".yml");

		if (file.exists()) {
			load();
			
			exists = true;
		}
	}
	
	public void create() {
		save();
	}
	
	public void load() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/kingdoms/", this.name.toLowerCase() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		this.name = yamlConfiguration.getString("name");
		
		for (String ally : yamlConfiguration.getStringList("allies")) {
			this.allies.add(new KingdomConstructor(ally));
		}
		
		this.woolData = (byte) yamlConfiguration.getInt("woolData");
		this.displayName = ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString("displayName"));
		this.spawnLocation = LocationUtils.fromStrToLocation(yamlConfiguration.getString("spawn_location"));
		this.points = yamlConfiguration.getInt("points");
		this.boatExitLocation = yamlConfiguration.get("boat_exit") == null ? null : LocationUtils.fromStrToLocation(yamlConfiguration.getString("boat_exit"));
	}
	
	public void save() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/kingdoms/", this.name.toLowerCase() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		
		yamlConfiguration.set("name", this.name);
		yamlConfiguration.set("woolData", this.woolData);
		yamlConfiguration.set("displayName", this.displayName);
		
		ArrayList<String> alliesStrings = new ArrayList<>();

		for(KingdomConstructor kingdomConstructor : this.allies) {
			alliesStrings.add(kingdomConstructor.getName());
		}
		
		yamlConfiguration.set("allies", alliesStrings);
		yamlConfiguration.set("spawn_location", LocationUtils.fromLocToString(this.spawnLocation));
		yamlConfiguration.set("points", this.points);
		yamlConfiguration.set("boat_exit", LocationUtils.fromLocToString(this.boatExitLocation));

		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getDisplayName() {
		return ChatColor.translateAlternateColorCodes('&', displayName);
	}
	
	public boolean doesExists() {
		return this.exists;
	}
	
	
	//
	public void broadcast(boolean toKing, String message) {
		ArrayList<Player> players = KingdomHandler.getOnlinePlayersMap().getOrDefault(this == null ? "null" : this.getName(), new ArrayList<Player>());
		
		for (Player player : players) {
			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
			
			if(toKing) {
				if(kingdomPlayer.isKing()) {
					player.sendMessage(message);
				}
			} else {
				player.sendMessage(message);
			}
		}
	}

}
