package me.niko.kingdom.tablist.ziggurat;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.tablist.utils.playerversion.PlayerVersionHandler;
import me.niko.kingdom.tablist.utils.serverversion.ServerVersionHandler;
import me.niko.kingdom.tablist.ziggurat.utils.IZigguratHelper;
import me.niko.kingdom.tablist.ziggurat.utils.impl.ProtocolLibTabImpl;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Ziggurat {

    //Instance
    @Getter private static Ziggurat instance;

    private JavaPlugin plugin;
    private ZigguratAdapter adapter;
    private Map<UUID, ZigguratTablist> tablists;
    private ZigguratThread thread;
    private IZigguratHelper implementation;
    private ZigguratListeners listeners;

    //Tablist Ticks
    @Setter private long ticks = 20;
    @Setter private boolean hook = false;

    public Ziggurat(JavaPlugin plugin, ZigguratAdapter adapter) {
        if (instance != null) {
            throw new RuntimeException("Ziggurat has already been instatiated!");
        }

        if (plugin == null) {
            throw new RuntimeException("Ziggurat can not be instantiated without a plugin instance!");
        }

        instance = this;

        this.plugin = plugin;
        this.adapter = adapter;
        this.tablists = new ConcurrentHashMap<>();

        new ServerVersionHandler();
        new PlayerVersionHandler();

        this.registerImplementation();

        this.setup();
    }

    private void registerImplementation() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            this.implementation = new ProtocolLibTabImpl();
            plugin.getLogger().info("Registered Implementation with ProtocolLib");
            return;
        }

        plugin.getLogger().info("Unable to register Ziggurat with a proper implementation");
    }

    private void setup() {
        listeners = new ZigguratListeners();
        //Register Events
        this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

        //Ensure that the thread has stopped running
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        //Start Thread
        this.thread = new ZigguratThread(this);
    }

    public void disable() {
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        if (listeners != null) {
            HandlerList.unregisterAll(listeners);
            listeners = null;
        }
    }
}
