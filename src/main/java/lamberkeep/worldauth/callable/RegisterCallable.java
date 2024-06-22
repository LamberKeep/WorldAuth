package lamberkeep.worldauth.callable;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.Callable;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import lamberkeep.worldauth.event.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A class that stores registration process actions.
 */
public class RegisterCallable implements Callable<Boolean> {

  private final Player player;
  private final WorldAuthTable data;
  private final String password;

  public RegisterCallable(Player player, WorldAuthTable data, String password) {
    this.player = player;
    this.data = data;
    this.password = password;
  }

  /**
   * Main class method.
   *
   * <p>Returns {@code false} if the player can't be registered in for some reason.
   *
   * @return {@code true} if player is registered.
   * @throws SQLException SQL query error
   */
  @Override
  public Boolean call()
      throws SQLException {
    String uuid = player.getUniqueId().toString();

    // Player is authed.
    if (!getAuths().containsKey(uuid)) {
      player.sendMessage(getLocale().getPlaceholderLocale(player, "login.already"));
      return false;
    }

    // Player is registered.
    if (!registerAccount()) {
      player.sendMessage(getLocale().getPlaceholderLocale(player, "register.already"));
      return false;
    }

    // Stop auth.
    getAuths().remove(uuid);

    Bukkit.getPluginManager().callEvent(new RegisterEvent(player));

    return true;
  }

  public boolean registerAccount()
      throws SQLException {

    // Player is already registered.
    if (data.isRegistered()) {
      return false;
    }

    WorldAuthTable query = new WorldAuthTable();
    query.setUuid(player.getUniqueId().toString());
    query.setPassword(password);
    query.setRegisterDate(Instant.now().getEpochSecond());

    // Saving password to Database().
    getDatabase().getWorldAuthTable().create(query);

    return true;
  }
}