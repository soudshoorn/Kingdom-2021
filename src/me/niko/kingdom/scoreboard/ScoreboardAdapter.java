package me.niko.kingdom.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.avaje.ebean.annotation.UpdatedTimestamp;

import me.clip.placeholderapi.PlaceholderAPI;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.bountyhunters.BountyHunters;
import me.niko.kingdom.events.breakthecore.BreakTheCore;
import me.niko.kingdom.events.conquest.Conquest;
import me.niko.kingdom.events.koth.Koth;
import me.niko.kingdom.events.war.WarHandler;
import me.niko.kingdom.staffmode.StaffModeHandler;
import me.niko.kingdom.utils.ConfigUtils;

public class ScoreboardAdapter implements AssembleAdapter {

	private int ONLINE_PLAYERS_COUNT = 0;
	private long updated = System.currentTimeMillis();
	private HashMap<UUID, KingdomPlayer> players = new HashMap<>();
	
	@Override
	public String getTitle(Player player) {
		return ConfigUtils.getFormattedValue("scoreboard_settings.title");
	}

	@Override
	public List<String> getLines(Player player) {
		List<String> lines = new ArrayList<>();

		// Delay between updates on Online Players & KingdomPlayers class because it might cause performance lag.
		if(3000 <= (System.currentTimeMillis() - updated)) {
			ONLINE_PLAYERS_COUNT = Bukkit.getOnlinePlayers().size();
			updated = System.currentTimeMillis();

			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
			players.put(player.getUniqueId(), kingdomPlayer);
		}
		
		KingdomPlayer kingdomPlayer = players.getOrDefault(player.getUniqueId(), KingdomHandler.getKingdomPlayer(player));
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

		KingdomConstructor locationKingdom = KingdomHandler.getKingdomByLocation(player.getLocation());
		
		if(StaffModeHandler.isInStaffMode(player)) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.staffmode_lines")) {
				lines.add(format(line, kingdomPlayer, locationKingdom)
						.replaceAll("%vanish%", (StaffModeHandler.isVanished(player) ? "Enabled" : "Disabled")));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(Kingdom.getInstance().getEventConstants().getActiveBountyHunters().size() != 0) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.bountyhunters_lines")) {
				BountyHunters bountyHunters = Kingdom.getInstance().getEventConstants().getActiveBountyHunters().get(0);
				
				int position = 1;
				
				for (String zone : bountyHunters.getZones()) {
					String cappedBy = "&fNiemand";
					String time = "&f" + secondsToMinutes(bountyHunters.getTime(zone));
					
					if(bountyHunters.getCappedZone().get(zone) != null) {
						KingdomConstructor kingdomConstructor = bountyHunters.getCappedZone().get(zone);
						
						cappedBy = kingdomConstructor.getDisplayName();
						time = "&a&l✓";
					}
					
					line = line
							.replaceAll("%time_" + position + "%", time)
							.replaceAll("%cappedBy_" + position + "%", cappedBy);
					
					position++;
				}
				
				lines.add(format(line, kingdomPlayer, locationKingdom));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(Kingdom.getInstance().getEventConstants().getActiveConquests().size() != 0) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.conquest_lines")) {
				Conquest conquest = Kingdom.getInstance().getEventConstants().getActiveConquests().get(0);
				
				if(conquest.getPoints().size() != 0) {
					Set<Entry<KingdomConstructor, Integer>> set = conquest.getPoints().entrySet();
			        List<Entry<KingdomConstructor, Integer>> list = new ArrayList<>(set);
			        
			        Collections.sort(list, new Comparator<Map.Entry<KingdomConstructor, Integer>>() {
			            public int compare(Map.Entry<KingdomConstructor, Integer> o1, Map.Entry<KingdomConstructor, Integer> o2) {
			                return o2.getValue().compareTo(o1.getValue());
			            }
			        });
			        
			        int i = 0;
			        for(Entry<KingdomConstructor, Integer> entry : list) {
			        	if(i == 3) {
			        		break;
			        	}
			        	
						KingdomConstructor kingdomConstructor = entry.getKey();
			        	
			        	i++;
			        	
			        	line = line
			        			.replaceAll("%kingdom_" + i + "%", kingdomConstructor.getDisplayName())
			        			.replaceAll("%points_" + i + "%", entry.getValue() + "");
			        }
				}
				
				if(line.contains("%kingdom_")) {
					continue;
				}
				
				lines.add(format(line, kingdomPlayer, locationKingdom)
						.replaceAll("%time_green%", secondsToMinutes(conquest.getTime("green")))
						.replaceAll("%time_blue%", secondsToMinutes(conquest.getTime("blue")))
						.replaceAll("%time_red%", secondsToMinutes(conquest.getTime("red")))
						.replaceAll("%time_yellow%", secondsToMinutes(conquest.getTime("yellow"))));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(Kingdom.getInstance().getEventConstants().getActiveBTCs().size() != 0) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.btc_lines")) {
				BreakTheCore breakTheCore = Kingdom.getInstance().getEventConstants().getActiveBTCs().get(0);
				
				Set<Entry<KingdomConstructor, Integer>> set = breakTheCore.getBreaks().entrySet();
		        ArrayList<Entry<KingdomConstructor, Integer>> list = new ArrayList<>(set);
		        
		        Collections.sort(list, new Comparator<Map.Entry<KingdomConstructor, Integer>>() {
		            public int compare(Map.Entry<KingdomConstructor, Integer> o1, Map.Entry<KingdomConstructor, Integer> o2) {
		                return o1.getValue().compareTo(o2.getValue());
		            }
		        });
				
				int i = 0;
		        
		        for (Entry<KingdomConstructor, Integer> entry : list) {
		        	if(i == 3) {
		        		break;
		        	}
		        	
					KingdomConstructor kingdomConstructor = entry.getKey();
		        	
		        	i++;
		        	
		        	line = line
		        			.replaceAll("%breaks_" + i + "%", entry.getValue() + "")
		        			.replaceAll("%kingdom_" + i + "%", kingdomConstructor.getDisplayName());
		        }
		        
		        if(line.contains("%kingdom_")) {
					continue;
				}
				
				lines.add(format(line, kingdomPlayer, locationKingdom));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(Kingdom.getInstance().getEventConstants().getActiveKoths().size() != 0) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.koth_lines")) {
				Koth koth = Kingdom.getInstance().getEventConstants().getActiveKoths().get(0);
				
				lines.add(format(line, kingdomPlayer, locationKingdom)
						.replaceAll("%koth_name%", koth.getName())
						.replaceAll("%koth_time%", secondsToMinutes(koth.getTime()))
						.replaceAll("%koth_coords%", (koth.getRegion().getMaximumPoint().getBlockX() + " | " + koth.getRegion().getMaximumPoint().getBlockZ()))
						.replaceAll("%koth_capping%", (koth.getCapper() == null ? "Niemand" : kingdom.getDisplayName())));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(WarHandler.isEnabled()) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.war_lines")) {
				int totalDeaths = 0;
				
				for(int deaths : WarHandler.getWarKills().values()) {
					totalDeaths += deaths;
				}
				
				Set<Entry<String, Integer>> set = WarHandler.getWarKills().entrySet();
		        List<Entry<String, Integer>> list = new ArrayList<>(set);
		        
		        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
		            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		                return o2.getValue().compareTo(o1.getValue());
		            }
		        });
				
		        int i = 0;
		        
		        for (Entry<String, Integer> entry : list) {
		        	if(i == 3) {
		        		break;
		        	}
		        	
					KingdomConstructor kingdomConstructor = new KingdomConstructor(entry.getKey());
		        	
		        	i++;
		        	
		        	line = line
		        			.replaceAll("%kills_" + i + "%", entry.getValue() + "")
		        			.replaceAll("%kingdom_" + i + "%", kingdomConstructor.getDisplayName());
		        }
		        
		        if(line.contains("%kingdom_")) {
					continue;
				}
				
				lines.add(format(line, kingdomPlayer, locationKingdom)
						.replaceAll("%total_deaths%", totalDeaths + "")
						.replaceAll("%time%", secondsToMinutes(WarHandler.TIME_IN_SECONDS)));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(kingdom == null) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.no_kingdom_lines")) {
				lines.add(format(line, kingdomPlayer, locationKingdom));
			}
			
			addFooter(lines);
			
			return lines;
		} else if(kingdom != null) {
			for(String line : Kingdom.getInstance().getConfig().getStringList("scoreboard_settings.has_kingdom_lines")) {
				lines.add(format(line, kingdomPlayer, locationKingdom));
			}
			
			addFooter(lines);
			
			return lines;
		}
		
		addFooter(lines);
		
		return lines;
	}
	
	private static void addFooter(List<String> lines) {
		if(!lines.isEmpty()) {
			if(Kingdom.getInstance().getConfig().getBoolean("scoreboard_settings.always_lines")) {
				lines.add(0, "&7&m------------------------------");
				
				if(Kingdom.getInstance().getConfig().getBoolean("scoreboard_settings.always_store")) {
					lines.add(lines.size(), ConfigUtils.getFormattedValue("scoreboard_settings.store"));
				}
				
				lines.add(lines.size(), "&7&m------------------------------");
			}
		}
	}
	
	private String format(String line, KingdomPlayer kingdomPlayer, KingdomConstructor locationKingdom) {
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
		
		return PlaceholderAPI.setPlaceholders(kingdomPlayer.getPlayer(), line
				.replaceAll("%influence%", kingdomPlayer.getInfluence() + "")
				.replaceAll("%online_players%", ONLINE_PLAYERS_COUNT + "")
				.replaceAll("%kingdom%", kingdom == null ? "None" : kingdom.getDisplayName())
				.replaceAll("%location_kingdom%", locationKingdom == null ? "Onbekend" : locationKingdom.getDisplayName()));
	}
	
	private static String secondsToMinutes(int s) {
		int hours = s / 3600;
		int minutes = (s % 3600) / 60;
		int seconds = s % 60;

		String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		
		if(hours <= 0) {
			timeString = String.format("%02d:%02d", minutes, seconds);
		}
		
		return timeString;
		
        //return String.format("%02d:%02d", s % 3600 / 60, s % 60);
	}
}
