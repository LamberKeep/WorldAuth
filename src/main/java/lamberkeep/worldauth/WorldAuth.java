package lamberkeep.worldauth;

import lamberkeep.worldauth.data.Data;
import lamberkeep.worldauth.data.Locale;
import lamberkeep.worldauth.data.Status;
import lamberkeep.worldauth.listener.PlayerChat;
import lamberkeep.worldauth.listener.PlayerDamage;
import lamberkeep.worldauth.listener.PlayerJoin;
import lamberkeep.worldauth.listener.PlayerQuit;
import lamberkeep.worldauth.task.Auth;
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
    }

}