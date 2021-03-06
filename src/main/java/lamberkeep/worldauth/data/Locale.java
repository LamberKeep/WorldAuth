package lamberkeep.worldauth.data;

import lamberkeep.worldauth.WorldAuth;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.storage;

public class Locale {

    private final WorldAuth plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public Locale(WorldAuth plugin) {
        this.plugin = plugin;
        // saves/initializes the config
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) configFile = new File(plugin.getDataFolder(), "locale.yml");
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = plugin.getResource("locale.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) reloadConfig();
        return dataConfig;
    }

    public void saveDefaultConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "locale.yml");
        if (!configFile.exists())
            plugin.saveResource("locale.yml", false);
    }

    public String getLocale(Player player, String path) {
        String message = Objects.requireNonNull(getConfig().getString(path));

        // Placeholders
        message = message.replace("%player%", player.getName());
        message = message.replace("%display-player%", player.getDisplayName());
        message = message.replace("%ip%", Objects.requireNonNull(player.getAddress()).getHostName());
        message = message.replace("%world%", player.getWorld().toString());
        message = message.replace("%online%", String.valueOf(player.getServer().getOnlinePlayers().size()));
        message = message.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
        message = message.replace("%attempts-used%", String.valueOf(storage.get(player).getAttempt()));
        message = message.replace("%attempts-max%", String.valueOf(plugin.getConfig().getInt("join-attempts")));

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}