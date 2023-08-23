package me.niko.kingdom.tablist;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomColor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.tablist.ziggurat.ZigguratAdapter;
import me.niko.kingdom.tablist.ziggurat.ZigguratCommons;
import me.niko.kingdom.tablist.ziggurat.utils.BufferedTabObject;
import me.niko.kingdom.tablist.ziggurat.utils.TabColumn;
import net.luckperms.api.model.group.Group;

public class TablistAdapter implements ZigguratAdapter {
	
	private long updated = System.currentTimeMillis();
	private int ONLINE_PLAYERS_COUNT = 0;
	private Group GROUP;

	public Set<BufferedTabObject> getSlots(Player player) {
		Set<BufferedTabObject> toReturn = new HashSet<>();
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		if(5000 <= (System.currentTimeMillis() - updated)) {
			ONLINE_PLAYERS_COUNT = Bukkit.getOnlinePlayers().size();
			GROUP = Kingdom.getInstance().getLuckPermsAPI().getPlayerGroup(player.getUniqueId());
			updated = System.currentTimeMillis();
		}
		
		// Top Left hand corner
		/*toReturn.add(new BufferedTabObject()
				// Text
				.text("&7&l&m------------------------------------")
				// Column
				.column(TabColumn.LEFT)
				// Slot
				.slot(1)
				// Ping (little buggy with 1.7 clients but defaults to 0 if thats the case
				.ping(0)
				// Texture (need to get skin sig and key
				.skin(ZigguratCommons.defaultTexture));
*/
		if(!player.hasMetadata("tablist_toggle")) {
			BufferedTabObject leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			leftObjects.slot(6).text("&c&lKingdoms");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("atlan").getWoolData())));
			
			//leftObjects.slot(7).text("&4A&8damantium &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("adamantium")) + ")");
			leftObjects.slot(7).text(KingdomHandler.getKingdom("atlan").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("atlan")) + ")");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("centuria").getWoolData())));
			
			//leftObjects.slot(8).text("&2Tilifia &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("tilifia")) + ")");
			leftObjects.slot(8).text(KingdomHandler.getKingdom("centuria").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("centuria")) + ")");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("morthir").getWoolData())));
			
			//leftObjects.slot(9).text("&eMalzan &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("malzan")) + ")");
			leftObjects.slot(9).text(KingdomHandler.getKingdom("morthir").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("morthir")) + ")");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("elyon").getWoolData())));
			
			//leftObjects.slot(10).text("&bEredon &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("eredon")) + ")");
			leftObjects.slot(10).text(KingdomHandler.getKingdom("elyon").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("elyon")) + ")");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("blodwhyn").getWoolData())));
			
			//leftObjects.slot(11).text("&5Dok &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("dok")) + ")");
			leftObjects.slot(11).text(KingdomHandler.getKingdom("blodwhyn").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("blodwhyn")) + ")");
			toReturn.add(leftObjects);
			
			leftObjects = new BufferedTabObject().column(TabColumn.LEFT).ping(0).skin(ZigguratCommons.getDot(KingdomColor.fromWoolToChatColor(KingdomHandler.getKingdom("argios").getWoolData())));
			
			//leftObjects.slot(12).text("&6Hyvar &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("hyvar")) + ")");
			leftObjects.slot(12).text(KingdomHandler.getKingdom("argios").getDisplayName() + " &7(" + KingdomHandler.getOnlinePlayers(KingdomHandler.getKingdom("argios")) + ")");
			toReturn.add(leftObjects);
			
			BufferedTabObject middleObjects = new BufferedTabObject().column(TabColumn.MIDDLE).ping(0).skin(ZigguratCommons.defaultTexture);
	
			middleObjects.slot(6).text("&c&lWebiste");
			toReturn.add(middleObjects);
			
			middleObjects = new BufferedTabObject().column(TabColumn.MIDDLE).ping(0).skin(ZigguratCommons.defaultTexture);
			
			middleObjects.slot(7).text("&7reforgedmc.nl");
			toReturn.add(middleObjects);
			
			middleObjects = new BufferedTabObject().column(TabColumn.MIDDLE).ping(0).skin(ZigguratCommons.defaultTexture);
			
			middleObjects.slot(11).text("&c&lOnline Spelers");
			toReturn.add(middleObjects);
			
			middleObjects = new BufferedTabObject().column(TabColumn.MIDDLE).ping(0).skin(ZigguratCommons.defaultTexture);
			
			middleObjects.slot(12).text("&7(" + ONLINE_PLAYERS_COUNT + "/" + Bukkit.getMaxPlayers() + ")");
			toReturn.add(middleObjects);
			
			BufferedTabObject rightObjects = new BufferedTabObject().column(TabColumn.MIDDLE).ping(0).skin(ZigguratCommons.defaultTexture);
	
			rightObjects = new BufferedTabObject().column(TabColumn.RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			rightObjects.slot(6).text("&c&lDiscord");
			toReturn.add(rightObjects);
			
			rightObjects = new BufferedTabObject().column(TabColumn.RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			rightObjects.slot(7).text("&7reforgedmc.nl/discord");
			toReturn.add(rightObjects);
			
			rightObjects = new BufferedTabObject().column(TabColumn.RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			rightObjects.slot(11).text("&c&lRank");
			toReturn.add(rightObjects);
			
			rightObjects = new BufferedTabObject().column(TabColumn.RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
						
			rightObjects.slot(12).text(GROUP.getDisplayName());
			toReturn.add(rightObjects);
			
			BufferedTabObject farRightObjects = new BufferedTabObject().column(TabColumn.FAR_RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
	
			
			farRightObjects.slot(6).text("&c&lLocatie");
			toReturn.add(farRightObjects);
			
			farRightObjects = new BufferedTabObject().column(TabColumn.FAR_RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			//farRightObjects.slot(7).text(KingdomPlugin.r.getPlayerKingdomWithColor(KingdomPlugin.r.getPlayerKingdom(player.getLocation()).replace("-", "Onbekend")));
			farRightObjects.slot(7).text(KingdomHandler.getKingdomByLocation(player.getLocation()) == null ? "Onbekend" : KingdomHandler.getKingdomByLocation(player.getLocation()).getDisplayName());
			toReturn.add(farRightObjects);
			
			farRightObjects = new BufferedTabObject().column(TabColumn.FAR_RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			farRightObjects.slot(11).text("&c&lInfluence");
			toReturn.add(farRightObjects);
			
			farRightObjects = new BufferedTabObject().column(TabColumn.FAR_RIGHT).ping(0).skin(ZigguratCommons.defaultTexture);
			
			farRightObjects.slot(12).text("&7" + kingdomPlayer.getInfluence());
			toReturn.add(farRightObjects);
		} else {
			
			int slot = 1;
			int i = 1;
			
			for(Player target : Bukkit.getOnlinePlayers()) {
				
				if(!player.canSee(target) && target != player) {
					continue;
				}
				
				if(slot > (60 + i)) {
					i++;
					slot = i;
				}
				
				TabColumn tabColumn = TabColumn.getFromSlot(player, slot);
				
	            if (tabColumn == null) {
	                continue;
	            }
				
				BufferedTabObject objects = new BufferedTabObject()
						//.text(KingdomPlugin.r.getColor(target) + target.getName() + StringUtils.repeat(" ", 15))
						.text(KingdomColor.fromWoolToChatColor(kingdomPlayer.getKingdom().getWoolData()) + target.getName() + StringUtils.repeat(" ", 15))
						.slot(i)
						.column(tabColumn)
						.ping(((CraftPlayer) target).getHandle().ping)
						//.skin(KingdomPlugin.r.getKingdomSkin(KingdomPlugin.r.getPlayerKingdom(target)));
						.skin(ZigguratCommons.getByPlayer(target));
				
				toReturn.add(objects);
				
				slot += 20;
			}
		}
		
		return toReturn;
	}

	@Override
	public String getFooter() {
		return "";
	}

	@Override
	public String getHeader() {
		return "";
	}

}
