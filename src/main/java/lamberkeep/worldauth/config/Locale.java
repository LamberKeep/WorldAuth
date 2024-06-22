package lamberkeep.worldauth.config;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import lamberkeep.worldauth.data.PlayerSessionData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Locale {

  private static final String FILENAME = "locale.yml";
  private FileConfiguration dataConfig = null;
  private File configFile = null;

  public Locale() {
    saveDefaultConfig(); // Save and initialize the config.
  }

  public void reloadConfig() {
    if (configFile == null) {
      configFile = new File(getPlugin().getDataFolder(), FILENAME);
    }
    dataConfig = YamlConfiguration.loadConfiguration(configFile);
    InputStream defaultStream = getPlugin().getResource(FILENAME);
    if (defaultStream != null) {
      YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
          new InputStreamReader(defaultStream));
      dataConfig.setDefaults(defaultConfig);
    }
  }

  public FileConfiguration getConfig() {
    if (dataConfig == null) {
      reloadConfig();
    }
    return dataConfig;
  }

  public void saveDefaultConfig() {
    if (configFile == null) {
      configFile = new File(getPlugin().getDataFolder(), FILENAME);
    }
    if (!configFile.exists()) {
      getPlugin().saveResource(FILENAME, false);
    }
  }

  public String getLocale(String path) {
    String message = Objects.requireNonNull(getConfig().getString(path));
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String getPlaceholderLocale(Player player, String path) {
    String message = Objects.requireNonNull(getConfig().getString(path));
    PlayerSessionData sessionData = getAuths().get(player.getUniqueId().toString());

    // All placeholders list.
    message = message.replace("%player%", player.getName());
    message = message.replace("%display-player%", player.getDisplayName());
    message = message.replace("%ip%", Objects.requireNonNull(player.getAddress()).getHostName());
    message = message.replace("%world%", player.getWorld().toString());
    message = message.replace("%online%",
        String.valueOf(player.getServer().getOnlinePlayers().size()));
    message = message.replace("%max-online%", String.valueOf(Bukkit.getServer().getMaxPlayers()));
    if (sessionData != null) {
      message = message.replace("%attempts-used%", Integer.toString(sessionData.getAttempt()));
    }
    message = message.replace("%attempts-max%",
        String.valueOf(getPlugin().getConfig().getInt("settings.join-attempts")));

    return ChatColor.translateAlternateColorCodes('&', message);
  }
}