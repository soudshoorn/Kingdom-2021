package me.niko.kingdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import me.niko.kingdom.chat.ChatHandler;
import me.niko.kingdom.commands.AutoSmeltCommand;
import me.niko.kingdom.commands.BTCCommand;
import me.niko.kingdom.commands.BountyHuntersCommand;
import me.niko.kingdom.commands.ChatCommand;
import me.niko.kingdom.commands.ConfigReloadCommand;
import me.niko.kingdom.commands.ConquestCommand;
import me.niko.kingdom.commands.FreezeCommand;
import me.niko.kingdom.commands.GuildCommand;
import me.niko.kingdom.commands.InfluenceCommand;
import me.niko.kingdom.commands.KingdomCommand;
import me.niko.kingdom.commands.KothCommand;
import me.niko.kingdom.commands.ListCommand;
import me.niko.kingdom.commands.MinesCommand;
import me.niko.kingdom.commands.MountCommand;
import me.niko.kingdom.commands.SetEndExitCommand;
import me.niko.kingdom.commands.StaffModeCommand;
import me.niko.kingdom.commands.StatsCommand;
import me.niko.kingdom.commands.TellLocationCommand;
import me.niko.kingdom.commands.WarCommand;
import me.niko.kingdom.data.KingdomConstructor;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.events.EventConstants;
import me.niko.kingdom.events.EventsListener;
import me.niko.kingdom.listeners.ArrowCleanerListener;
import me.niko.kingdom.listeners.BuildListeners;
import me.niko.kingdom.listeners.ChatListeners;
import me.niko.kingdom.listeners.CombatLoggerListener;
import me.niko.kingdom.listeners.DamageListeners;
import me.niko.kingdom.listeners.EndExitListener;
import me.niko.kingdom.listeners.HorseMountListener;
import me.niko.kingdom.listeners.MinesListeners;
import me.niko.kingdom.listeners.PlayerListeners;
import me.niko.kingdom.listeners.VisibilityListener;
import me.niko.kingdom.listeners.WorldListener;
import me.niko.kingdom.mount.HorseHandler;
import me.niko.kingdom.nametags.NametagHandler;
import me.niko.kingdom.nametags.Nametags;
import me.niko.kingdom.scoreboard.Assemble;
import me.niko.kingdom.scoreboard.ScoreboardAdapter;
import me.niko.kingdom.staffmode.StaffModeHandler;
import me.niko.kingdom.staffmode.StaffModeListener;
import me.niko.kingdom.utils.menu.menu.ButtonListener;
import me.niko.kingdom.visibility.VisibilityManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.minelink.ctplus.CombatTagPlus;

public class Kingdom extends JavaPlugin {
	
	@Getter public static Kingdom instance;
	
	@Getter public static WorldEditPlugin worldEdit;
	@Getter public static CombatTagPlus combatTagPlus;
	@Getter public static Assemble assemble;
	@Getter public static NametagHandler nametags;

	@Getter public static EventConstants eventConstants;
	@Getter public static ChatHandler chat;

	@Getter public static HashMap<UUID, KingdomPlayer> playersMap;
	@Getter public static LunarClientAPI lunarClientAPI;

	@Getter public static boolean beta;
    @Getter private static Chat vaultChat = null;
    @Getter private static Permission perms = null;
    
    @Getter public static HashMap<Block, Entry<Material, Byte>> brokenBlocks;
    @Getter public static HashMap<Block, Long> brokenBlocksCD;
    @Getter public static ArrayList<UUID> autoSmelting;
	@Getter public static ArrayList<BukkitTask> tasks;
	
    @Getter private static Random random;

	public void onEnable() {
		instance = this;

		lunarClientAPI = new LunarClientAPI(this);
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getLogger().warning("NO VAULT IS INSTALLED. KINGDOM PLUGIN NEEDS IT!");
			getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		assemble = new Assemble(this, new ScoreboardAdapter());
		nametags = new NametagHandler(this, new Nametags());
		
		nametags.setTicks(20);
		
		eventConstants = new EventConstants();
		
		combatTagPlus = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");
		worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		
		setupChat();
		
		chat = new ChatHandler();
			
		playersMap = new HashMap<>();
		
		brokenBlocks = new HashMap<>();
		brokenBlocksCD = new HashMap<>();
		autoSmelting = new ArrayList<>();
		tasks = new ArrayList<>();
		random = new Random();
		
		KingdomHandler.updateKingdoms();
		
		beta = getConfig().getBoolean("settings.beta_mode");
		
		//Let's not fuck the whole plugin if theres a /reload ^_^
		for(Player player : Bukkit.getOnlinePlayers()) {
			
			KingdomPlayer kingdomPlayer = new KingdomPlayer(player);
			KingdomConstructor kingdom = KingdomHandler.getKingdom(kingdomPlayer);

			KingdomHandler.addOnlinePlayer(player, kingdom);
			playersMap.put(player.getUniqueId(), kingdomPlayer);
		}
				
		getCommand("kingdom").setExecutor(new KingdomCommand());
		getCommand("bountyhunters").setExecutor(new BountyHuntersCommand());
		getCommand("btc").setExecutor(new BTCCommand());
		getCommand("conquest").setExecutor(new ConquestCommand());
		getCommand("koth").setExecutor(new KothCommand());
		getCommand("freeze").setExecutor(new FreezeCommand());
		getCommand("staffmode").setExecutor(new StaffModeCommand());
		getCommand("chat").setExecutor(new ChatCommand());
		getCommand("koth").setExecutor(new KothCommand());
		getCommand("war").setExecutor(new WarCommand());
		getCommand("mount").setExecutor(new MountCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("guild").setExecutor(new GuildCommand());
		getCommand("list").setExecutor(new ListCommand());
		getCommand("telllocation").setExecutor(new TellLocationCommand());
		getCommand("influence").setExecutor(new InfluenceCommand());
		getCommand("setendexit").setExecutor(new SetEndExitCommand());
		getCommand("autosmelt").setExecutor(new AutoSmeltCommand());
		getCommand("mines").setExecutor(new MinesCommand());
		getCommand("creload").setExecutor(new ConfigReloadCommand());

		getServer().getPluginManager().registerEvents(new ArrowCleanerListener(), this);
		getServer().getPluginManager().registerEvents(new BuildListeners(), this);
		getServer().getPluginManager().registerEvents(new ChatListeners(), this);
		getServer().getPluginManager().registerEvents(new DamageListeners(), this);
		getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
		getServer().getPluginManager().registerEvents(new StaffModeListener(), this);
		getServer().getPluginManager().registerEvents(new EventsListener(), this);
		getServer().getPluginManager().registerEvents(new HorseMountListener(), this);
		getServer().getPluginManager().registerEvents(new CombatLoggerListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new EndExitListener(), this);
		getServer().getPluginManager().registerEvents(new MinesListeners(), this);
		getServer().getPluginManager().registerEvents(new VisibilityListener(), this);

        getServer().getPluginManager().registerEvents(new ButtonListener(), this);
		        
		long saveInterval = ((10 * 60) * 20); // 20 Minutes?
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Entry<UUID, KingdomPlayer> players : playersMap.entrySet()) {
					long started = System.currentTimeMillis();
					
					System.out.println("[1] Saving all the kingdoms data...");
					
					int influence = players.getValue().getInfluence();
					
					int set = influence + 10;
					
					if ((set) > 1000) {
						set = 1000; 
					}
					
					players.getValue().setInfluence(set);
					
					players.getValue().save();
					
					System.out.println("[1] Data saved! Took " + (System.currentTimeMillis() - started) + "ms");
				}
				
				for (KingdomConstructor kingdomConstructor : KingdomHandler.getKingdoms()) {
					long started = System.currentTimeMillis();
					System.out.println("[2] Saving all the kingdoms...");

					kingdomConstructor.save();
					System.out.println("[2] Kingdoms saved! Took " + (System.currentTimeMillis() - started) + "ms");
				}
			}
		}.runTaskTimerAsynchronously(this, saveInterval, saveInterval);
		
	}
	
	@Override
	public void onDisable() {
		eventConstants.stopAll();
		
		assemble.cleanup();
		nametags.cleanup();
		
		HorseHandler.saveAndRemove();
		
		for(Entry<UUID, KingdomPlayer> players : this.playersMap.entrySet()) {
			players.getValue().save();
		}
				
		for(Player player : StaffModeHandler.getInStaffMode()) {
        	StaffModeHandler.setStaffMode(player, false);
        }
		
		instance = null;
	}
	
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        vaultChat = rsp.getProvider();
        return vaultChat != null;
    }
}