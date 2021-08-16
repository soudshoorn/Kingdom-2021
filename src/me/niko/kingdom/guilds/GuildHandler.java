package me.niko.kingdom.guilds;

import java.io.File;
import java.util.ArrayList;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;

public class GuildHandler {
	
	public static int STORAGE = Kingdom.getInstance().getConfig().getInt("guild_settings.storage");
	public static int MAX_MEMBERS = Kingdom.getInstance().getConfig().getInt("guild_settings.max_members");
	
	public static ArrayList<Guild> getGuilds() {
		ArrayList<Guild> guilds = new ArrayList<>();
		
		File dir = new File(Kingdom.getInstance().getDataFolder() + "/guilds/");
		
		if(!dir.exists()) {
			return guilds;
		}
		
		File[] directoryListing = dir.listFiles();
				
		if (directoryListing != null) {
			for (File child : directoryListing) {
				String file = child.getName().replaceAll(".yml", "");

				Guild guild = new Guild(file);
				
				if(guild.doesExists()) {
					guilds.add(guild);
				}
			}
		}
		
		return guilds;
	}
	
	public static Guild getGuildByTag(String tag) {
		Guild guildByTag = null;
		
		for(Guild guild : getGuilds()) {
			if(guild.getTag().toLowerCase().equals(tag.toLowerCase())) {
				guildByTag = guild;
				
				break;
			}
		}
		
		return guildByTag;
	}
	
	public static boolean isSimiliarGuild(Guild g1, Guild g2) {		
		if(g1 == null || g2 == null) {
			return false;
		}

		return g1.getName().toLowerCase().equals(g2.getName().toLowerCase());
	}

}
