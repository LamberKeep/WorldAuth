package lamberkeep.worldauth;

import lamberkeep.worldauth.data.*;
import lamberkeep.worldauth.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;

public final class WorldAuth extends JavaPlugin implements Listener {

    public static WorldAuth plugin;

    public static Data data;
    public static Locale locale;
    public static FileConfiguration config;

    public static HashMap<Player, Status> storage = new HashMap<>(); // sessions

    @Override
    public void onEnable() {
        plugin = this;
        data = new Data(this);
        locale = new Locale(this);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerChat(), this);
        pluginManager.registerEvents(new PlayerDamage(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PlayerQuit(), this);

        new WorldCreator(Objects.requireNonNull(getConfig().getString("worlds.auth"))).createWorld();
        new Commands(this);

        saveDefaultConfig();
        config = getConfig();

        // on server restart set all online players as authed
        // but if data file was deleted they still need to register
        for (Player player: Bukkit.getOnlinePlayers())
            storage.put(player, new Status(player));
    }

}