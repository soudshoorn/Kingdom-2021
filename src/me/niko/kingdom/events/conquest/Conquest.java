package me.niko.kingdom.events.conquest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class Conquest {

	@Getter private String name;
	@Getter @Setter private String[] cappingPlayers;
	@Getter @Setter private int[] times;
	@Getter private HashMap<KingdomConstructor, Integer> points;
	@Getter private HashMap<String, String> cappedZoneByKingdom;
	@Getter private HashMap<String, ArrayList<Integer>> capBlockPerSecond;
	@Getter private ArrayList<String> zones;
	@Getter private int maxPoints;
	@Getter private int defaultCapTime = 60;
	@Getter private boolean active;
	@Getter private BukkitTask task;

	public Conquest(String name) {
		this.name = name;
		
		zones = new ArrayList<>();
		
		zones.add("green");
		zones.add("yellow");
		zones.add("red");
		zones.add("blue");
	}
	
	public ArrayList<Block> getCapZoneBlocks(String zone) {
		ArrayList<Block> blocks = new ArrayList<>();
		
		File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("conquest." + this.name + "." + zone + ".world"));
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z2"));
		
		for(int x = locA.getBlockX(); x <= locB.getBlockX(); x++) {
			for(int z = locA.getBlockZ(); z <= locB.getBlockZ(); z++) {
				Block block = w.getBlockAt(x, locA.getBlockY(), z);
				
				blocks.add(block);
			}
		}
		
		return blocks;
	}
	
	public void start(int maxPoints) {
		points = new HashMap<>();
		capBlockPerSecond = new HashMap<>();
		cappedZoneByKingdom = new HashMap<>();
		
		this.maxPoints = maxPoints;
		
		active = true;
		
		cappingPlayers = new String[] {null, null, null, null};
		
		times = new int[] {defaultCapTime, defaultCapTime, defaultCapTime, defaultCapTime};
		
		//Bukkit.broadcastMessage(ChatColor.YELLOW + "Starting a conquest");
		
		/*for(String zone : zones) {
			cappedZoneByKingdom.put(zone, "-");
			
			resetBlocks(zone);
			
			ArrayList<Integer> blockAtTime = new ArrayList<Integer>();
			ArrayList<Block> zoneBlocks = getCapZoneBlocks(zone);
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					double ostatuk = 0;
					int blocks = (int) (zoneBlocks.stream().filter(b -> b.getType() == Material.STAINED_CLAY 
							&& b.getData() == (byte) 0).count());
					
					for(int i = defaultCapTime; i > 0; i--) {
						if(blocks == 0) {
							blockAtTime.add(0);
							
							continue;
						}
						
						double blockspersec = ((double)blocks / (double)i);
						
						if(blockspersec % 1 == 0) {
							int a = (int) Math.floor(blockspersec);
							blocks -= a;
							
							blockAtTime.add(a);					
							continue;
						}
						
						if(blockspersec < 1.00) {
							ostatuk += blockspersec; // - 0.50;
						}
						
						int finalBlocks = 0;
						
						if(ostatuk >= 1.00) {
							double nextOstatuk = ostatuk - (int) Math.floor(ostatuk);
							finalBlocks = (int) Math.floor(ostatuk);
							
							ostatuk = nextOstatuk;
						}
						
						blocks -= finalBlocks;
						blockAtTime.add(finalBlocks);
					}
					
					capBlockPerSecond.put(zone, blockAtTime);
				}
			}.runTaskAsynchronously(KingdomPlugin.r);
		}*/
		
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				for(String zone : zones) {
										
					if(getCapper(zone) != null) {
						Player player = Bukkit.getPlayer(getCapper(zone));
						
						if(player == null
								|| !isCapping(player, zone)
								|| player.isDead()) {
							setCapper(null, zone);
							setTime(defaultCapTime, zone);
						} else {
							
							if(getTime(zone) <= 0) {
								setTime(defaultCapTime, zone);
								
								addPoints(player, zone, 1);
								
								player.sendMessage(ConfigUtils.getFormattedValue("messages.events.conquest.capped")
										.replaceAll("%zone%", zone.toUpperCase()));
								
								//resetBlocks(zone);
								
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
									&& !(cappedZoneByKingdom.containsKey(zone) && kingdom.getName().equalsIgnoreCase(cappedZoneByKingdom.get(zone)))) {
								onCapPlayers.add(player);
							}
						}
						
						Collections.shuffle(onCapPlayers);
						
						if(onCapPlayers.size() != 0) {
							setCapper(onCapPlayers.get(0).getName(), zone);
							onCapPlayers.get(0).sendMessage(ConfigUtils.getFormattedValue("messages.events.conquest.capping")
									.replaceAll("%zone%", zone.toUpperCase()));
							setTime(defaultCapTime, zone);
						}
					}
					
					/*ArrayList<Block> blocks = getCapZoneBlocks(zone);
					
					times[index] -= 1;

					if(cappingPlayers[index] == null) {
						times[index] = defaultCapTime;
						
						continue;
					}
					
					if(!isCapping(cappingPlayers[index], zone)) {
						cappingPlayers[index] = null;
						times[index] = defaultCapTime;
						//resetBlocks(zone);
						
						continue;
					}
					
					if(times[index] == 0) {
						times[index] = defaultCapTime;
						
						for(Block block : blocks) {
							if(block.getType() == Material.STAINED_GLASS) {
								block.setData((byte) KingdomPlugin.r.getKingdomData(KingdomPlugin.r.getPlayerKingdomNoColor(cappingPlayers[index])));
							}
						}
						
						cappingPlayers[index].sendMessage(ChatColor.GREEN + "You have captured an point on cap " + zone.toUpperCase() + " go to another one to get another point.");
						cappingPlayers[index].sendMessage(ChatColor.RED + "You can't capture one cap twice in a row.");
						
						addPoints(cappingPlayers[index], zone, 1);
						
						cappingPlayers[index] = null;
						
						continue;
					}
					
					if(!active) {
						resetBlocks(zone);
						//reset blocks
						cancel();
						
						return;
					}*/
					
					/*for(int i = 1; i <= getCapBlockPerSecond().get(zone).get(times[index]); i++) {
						Block blocktoChange = (Block) blocks.stream()
								.filter(b-> b.getType() == Material.STAINED_CLAY 
								&& b.getData() == getLastCapperKingdomDataByZone(zone))
								.toArray()[KingdomPlugin.r.random.nextInt((int) blocks.stream()
									.filter(b-> b.getType() == Material.STAINED_CLAY 
									&& b.getData() == getLastCapperKingdomDataByZone(zone)).count())];
						
						blocktoChange.setData((byte) 
								KingdomPlugin.r.getKingdomData(KingdomPlugin.r.getPlayerKingdomNoColor(cappingPlayers[index])));
					}*/
				}
			}
		}.runTaskTimer(Kingdom.getInstance(), 20L, 20L);
		
		ConfigUtils.getFormattedValueList("messages.events.conquest.started_broadcast")
			.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m)));
		Kingdom.getInstance().getEventConstants().getActiveConquests().add(this);
	}
	
	public void stop() {
		Conquest conquest = Kingdom.getInstance().getEventConstants().getActiveConquests().stream().filter(c -> c.getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
		
		conquest.active = false;
		conquest.task.cancel();
		
		Kingdom.getInstance().getEventConstants().getActiveConquests().remove(conquest);
		
		for(String zone : zones) {
			cappedZoneByKingdom.put(zone, "-");
			
			//resetBlocks(zone);
		}
	}
	
	public void addPoints(Player player, String zone, int points2) {		
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
		KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);
		
		int kingdomPoints = points.getOrDefault(kingdom, 0);
		
		if(kingdom == null) {
			return;
		}
		
		int addPoints = kingdomPoints + points2;
		
		points.put(kingdom, addPoints);
		//limitCap.put(kingdom, zone);
		cappedZoneByKingdom.put(zone, kingdom.getName());
		
		if(addPoints >= this.maxPoints) {
			handleWinner(player, kingdom);
		}
	}
	
	public void handleWinner(Player player, KingdomConstructor kingdom) {
		ConfigUtils.getFormattedValueList("messages.events.conquest.ended_broadcast")
				.forEach(m -> Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m
						.replaceAll("%kingdom%", kingdom.getDisplayName())
						.replaceAll("%player%", player.getName()))));
		
		stop();
	}
	
	public boolean isCapping(Player player, String zone) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("conquest." + this.name + "." + zone + ".world"));
		
		if(player.getLocation().getWorld() != w) {
			return false;
		}
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z2"));
		
		CuboidSelection s = new CuboidSelection(w, locA, locB);
			
		Vector min = s.getNativeMinimumPoint();
		Vector max = s.getNativeMaximumPoint();
		
		return 	   min.getBlockX() <= player.getLocation().getBlockX()
				&& max.getBlockX() >= player.getLocation().getBlockX() && min.getBlockY() <= player.getLocation().getBlockY()
				&& max.getBlockY() >= player.getLocation().getBlockY() && min.getBlockZ() <= player.getLocation().getBlockZ()
				&& max.getBlockZ() >= player.getLocation().getBlockZ();
	}
	
	public Location getCenter(String zone) {
		File file = new File(Kingdom.getInstance().getDataFolder(), "conquest.yml");
		YamlConfiguration yamlConfiguration = new YamlConfiguration().loadConfiguration(file);
		
		World w = Bukkit.getWorld(yamlConfiguration.getString("conquest." + this.name + "." + zone + ".world"));
		
		Location locA = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y1"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z1"));
		
		Location locB = new Location(w,
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".x2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".y2"),
				yamlConfiguration.getDouble("conquest." + this.name + "." + zone + ".z2"));
		
		Location center = new Location(w, 
				(locA.getBlockX() + locB.getBlockX()) / 2, 
				(locA.getBlockY()),
				(locA.getBlockZ() + locB.getBlockZ()) / 2);
		
		return center;
	}
	
	public byte getLastCapperKingdomDataByZone(String zone) {
		return (byte) (cappedZoneByKingdom.get(zone).equals("-") ? 0 : new KingdomConstructor(cappedZoneByKingdom.get(zone)).getWoolData()); //KingdomPlugin.r.getKingdomData(cappedZoneByKingdom.get(zone)));
	}
	
	public void resetBlocks(String zone) {
	
		for(Block block : getCapZoneBlocks(zone)) {
			byte data = getLastCapperKingdomDataByZone(zone);
			
			//if(block.getType() == Material.STAINED_CLAY) {
				//block.setData(data);
			//}
			
			if(block.getType() == Material.STAINED_GLASS) {
				block.setData(data);
			}
			
			/*Block glass = getCenter(zone).getWorld().getBlockAt(getCenter(zone));
			
			glass.setType(Material.STAINED_GLASS);
			glass.setData(data);*/
		}
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
