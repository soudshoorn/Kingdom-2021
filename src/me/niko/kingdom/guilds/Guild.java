package me.niko.kingdom.guilds;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;

public class Guild {
	
	@Getter private String name;
	@Getter @Setter private String tag = "";
	@Getter @Setter private UUID leader = UUID.randomUUID();
	
	@Getter private ArrayList<UUID> members = new ArrayList<>(); 
	@Getter private ArrayList<UUID> invites = new ArrayList<>(); 

	@Getter @Setter private int bank = 0;
	@Getter @Setter private int kills = 0;
	@Getter @Setter private int deaths = 0;
	
	@Getter @Setter private KingdomConstructor kingdom = null;

	@Getter private boolean exists = false;
	
	public Guild(String name) {
		this.name = name;
		
		File file = new File(Kingdom.getInstance().getDataFolder() + "/guilds/", this.name.toLowerCase() + ".yml");

		if (file.exists()) {
			load();
			
			exists = true;
		} else {
			save();
		}
	}
	
	public void load() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/guilds/", this.name.toLowerCase() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		this.name = yamlConfiguration.getString("name");
		this.tag = yamlConfiguration.getString("tag");
		this.leader = UUID.fromString(yamlConfiguration.getString("leader"));
		
		for (String memberUuid : yamlConfiguration.getStringList("members")) {
			this.members.add(UUID.fromString(memberUuid));
		}
		
		for (String invite : yamlConfiguration.getStringList("invites")) {
			this.invites.add(UUID.fromString(invite));
		}
		
		this.kingdom = yamlConfiguration.getString("kingdom").equals("null") ? null : new KingdomConstructor(yamlConfiguration.getString("kingdom"));
		
		this.bank = yamlConfiguration.getInt("bank");
		this.kills = yamlConfiguration.getInt("kills");
		this.deaths = yamlConfiguration.getInt("deaths");
	}
	
	public void save() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/guilds/", this.name.toLowerCase() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		
		yamlConfiguration.set("name", this.name);
		yamlConfiguration.set("tag", this.tag);
		yamlConfiguration.set("leader", this.leader.toString());
		
		ArrayList<String> membersList = new ArrayList<>();
		for(UUID member : this.members) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
			
			if(offlinePlayer.isOnline()) {
				KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(offlinePlayer.getUniqueId());
				
				kingdomPlayer.setGuild(this);
			}
			
			membersList.add(member.toString());
		}
		
		yamlConfiguration.set("members", membersList);
		
		ArrayList<String> invitesList = new ArrayList<>();
		for(UUID invite : this.invites) {
			invitesList.add(invite.toString());
		}
		
		yamlConfiguration.set("invites", invitesList);
		yamlConfiguration.set("kingdom", this.kingdom == null ? "null" : this.kingdom.getName());
		
		yamlConfiguration.set("bank", this.bank);
		yamlConfiguration.set("kills", this.kills);
		yamlConfiguration.set("deaths", this.deaths);
		
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void broadcast(String message) {
		for(UUID uuid : this.members) {
			Player player = Bukkit.getPlayer(uuid);
			
			if(player != null && player.isOnline()) {
				player.sendMessage(message);
			}
		}
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> players = new ArrayList<>();
		
		for(UUID uuid : this.members) {
			Player player = Bukkit.getPlayer(uuid);
			
			if(player != null && player.isOnline()) {
				players.add(player);
			}
		}
		
		return players;
	}
	
	public String formatPlayer(UUID uuid) {
		String color = ChatColor.GRAY.toString();
		
		Player player = Bukkit.getPlayer(uuid);
		
		if(player == null) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			
			if(offlinePlayer != null) {
				color = ChatColor.GRAY.toString() + offlinePlayer.getName();
			}
		}
		
		if(player != null && player.isOnline()) {
			color = ChatColor.GREEN.toString() + player.getName();
		}
		
		return color;
	}
	
	public void disband() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/guilds/", this.name.toLowerCase() + ".yml");
		
		for(UUID uuid : this.members) {
			Player player = Bukkit.getPlayer(uuid);
			
			KingdomPlayer kingdomPlayer;
			
			if(player != null && player.isOnline()) {
				kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
				
				player.sendMessage(ConfigUtils.getFormattedValue("messages.guild.guild_disbanded")
						.replaceAll("%guild_name%", this.name)
						.replaceAll("%tag%", this.tag));
				
			} else {
				kingdomPlayer = new KingdomPlayer(uuid);
			}
			
			kingdomPlayer.setGuild(null);
			kingdomPlayer.save();
		}
		
		file.delete();
	}
	
	public double getKdr() {
		
		double kdr = 0;
		
		if(this.kills == 0 || this.deaths == 0) {
			return kdr;
		}
		
		kdr = this.kills / this.deaths;
		
		return kdr;
	}
	
	public String getKdrFormat() {
		double kdr = getKdr();
		DecimalFormat df = new DecimalFormat("####0.00");
		
		return df.format(kdr);
	}
	
	public boolean doesExists() {
		return this.exists;
	}

}
