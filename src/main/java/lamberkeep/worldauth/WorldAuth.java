package lamberkeep.worldauth;

import java.sql.SQLException;
import java.util.Objects;
import lamberkeep.worldauth.command.CommandHandler;
import lamberkeep.worldauth.config.Locale;
import lamberkeep.worldauth.data.AuthData;
import lamberkeep.worldauth.database.Database;
import lamberkeep.worldauth.listener.EventRegister;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldAuth extends JavaPlugin {

  private static final AuthData auths = new AuthData();
  private static WorldAuth plugin;
  private static Locale locale;
  private static Database database;

  public static WorldAuth getPlugin() {
    return plugin;
  }

  public static Locale getLocale() {
    return locale;
  }

  public static Database getDatabase() {
    return database;
  }

  public static AuthData getAuths() {
    return auths;
  }

  @Override
  public void onEnable() {
    plugin = this;

    saveDefaultConfig();

    try {
      database = new Database();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    locale = new Locale();

    new EventRegister();
    new CommandHandler();
    new WorldCreator(Objects.requireNonNull(getConfig().getString("worlds.auth"))).createWorld();
  }

  @Override
  public void onDisable() {
    try {
      getDatabase().getConnectionSource().close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    super.onDisable();
  }
}