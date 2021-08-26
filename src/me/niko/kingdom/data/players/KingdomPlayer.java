package me.niko.kingdom.data.players;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.guilds.Guild;
import me.niko.kingdom.utils.InventoryUtils;

public class KingdomPlayer {
	
	@Getter private Player player;
	@Getter private UUID uuid;
	@Getter @Setter private KingdomConstructor kingdom = null;
	@Getter @Setter private Guild guild = null;
	@Getter @Setter private int kingdomRank = 0;
	@Getter @Setter private int influence = 0;
	@Getter @Setter private int kills = 0;
	@Getter @Setter private int deaths = 0;
	@Getter @Setter private HashMap<Integer, ItemStack> enderchestItems = new HashMap<>();
	
	public KingdomPlayer(Player player) {
		this.player = player;
		
		this.uuid = this.player.getUniqueId();
		
		File file = new File(Kingdom.getInstance().getDataFolder() + "/players/", this.uuid.toString() + ".yml");
		
		if(file.exists()) {
			load();
		} else {
			save();
		}
	}
	
	public KingdomPlayer(UUID uuid) {
		this.uuid = uuid;
		
		File file = new File(Kingdom.getInstance().getDataFolder() + "/players/", this.uuid.toString() + ".yml");
		
		if(file.exists()) {
			load();
		} else {
			save();
		}
	}
	
	public void load() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/players/", this.uuid.toString() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		String kingdomName = yamlConfiguration.getString("kingdom");
		
		this.kingdom = kingdomName.equals("null") ? null : KingdomHandler.getKingdom(kingdomName);
		this.guild = yamlConfiguration.getString("guild").equals("null") ? null : new Guild(yamlConfiguration.getString("guild"));
		this.kingdomRank = yamlConfiguration.getInt("rank");
		this.influence = yamlConfiguration.getInt("influence");
		this.kills = yamlConfiguration.getInt("kills");
		this.deaths = yamlConfiguration.getInt("deaths");
		
		try {
			if(yamlConfiguration.get("enderchest") != null) {
				for (String integer : yamlConfiguration.getConfigurationSection("enderchest").getKeys(false)) {
					this.enderchestItems.put(Integer.parseInt(integer), InventoryUtils.fromBase64(yamlConfiguration.getString("enderchest." + integer)));
				}
			}
		} catch (Exception ex) {}
	}
	
	public void save() {
		File file = new File(Kingdom.getInstance().getDataFolder() + "/players/", this.uuid.toString() + ".yml");
		
		YamlConfiguration yamlConfiguration = new YamlConfiguration();
		
		yamlConfiguration.set("kingdom", this.kingdom == null ? "null" : this.kingdom.getName());
		yamlConfiguration.set("guild", this.guild == null ? "null" : this.guild.getName());
		yamlConfiguration.set("rank", this.kingdomRank);
		
		if(this.influence < 0) {
			this.influence = 0;
		}
		
		yamlConfiguration.set("influence", this.influence);
		yamlConfiguration.set("kills", this.kills);
		yamlConfiguration.set("deaths", this.deaths);

		//yamlConfiguration.set("enderchest", InventoryUtils.itemStackArrayToBase64(this.enderChestItems));
		
		for(Entry<Integer, ItemStack> entry : this.enderchestItems.entrySet()) {
			try {
				yamlConfiguration.set("enderchest." + entry.getKey(), InventoryUtils.toBase64(entry.getValue()));
			} catch (IOException e) { }
		}
		
		try {
			yamlConfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public boolean isHertog() {
		return this.kingdomRank != 0 && this.kingdomRank == (KingdomHandler.getRanks().size() - 2); //Always the -2 is the hertog ig
	}
	
	public boolean isKing() {
		return this.kingdomRank != 0 && this.kingdomRank == (KingdomHandler.getRanks().size() - 1); //Always the last one is the King
	}

}
