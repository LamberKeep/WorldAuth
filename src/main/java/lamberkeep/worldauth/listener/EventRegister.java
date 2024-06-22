package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getPlugin;

import org.bukkit.plugin.PluginManager;

public class EventRegister {

  public EventRegister() {
    PluginManager pluginManager = getPlugin().getServer().getPluginManager();
    pluginManager.registerEvents(new LoginListener(), getPlugin());
    pluginManager.registerEvents(new LogoutListener(), getPlugin());
    if (getPlugin().getConfig().getBoolean("settings.use-chat")) {
      pluginManager.registerEvents(new AsyncPlayerChatListener(), getPlugin());
    }
    pluginManager.registerEvents(new PlayerCommandListener(), getPlugin());
    pluginManager.registerEvents(new PlayerDamageListener(), getPlugin());
    pluginManager.registerEvents(new PlayerJoinListener(), getPlugin());
    pluginManager.registerEvents(new PlayerQuitListener(), getPlugin());
    pluginManager.registerEvents(new RegisterListener(), getPlugin());
    pluginManager.registerEvents(new UnregisterListener(), getPlugin());
  }
}