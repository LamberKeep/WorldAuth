package lamberkeep.worldauth.callable;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Callable;
import lamberkeep.worldauth.data.PlayerSessionData;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import lamberkeep.worldauth.event.LoginEvent;
import lamberkeep.worldauth.security.Security;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A class that stores login process actions.
 */
public class LoginCallable implements Callable<Boolean> {

  private final int maxAttempt = getPlugin().getConfig().getInt("settings.join-attempts");
  private final Player player;
  private final WorldAuthTable data;
  private final String password;

  public LoginCallable(Player player, WorldAuthTable data, String password) {
    this.player = player;
    this.data = data;
    this.password = password;
  }

  /**
   * Main class method.
   *
   * <p>Returns {@code false} if the player can't be logged in for some reason.
   *
   * @return {@code true} if player is logged in.
   * @throws SQLException SQL query error
   */
  @Override
  public Boolean call()
      throws SQLException, SocketException, UnknownHostException, NoSuchAlgorithmException {
    String uuid = player.getUniqueId().toString();
    PlayerSessionData ps = getAuths().get(uuid);

    // Player is authed.
    if (!getAuths().containsKey(uuid)) {
      player.sendMessage(getLocale().getPlaceholderLocale(player, "login.already"));
      return false;
    }

    // Player is not registered.
    if (!data.isRegistered()) {
      player.sendMessage(getLocale().getPlaceholderLocale(player, "login.not-registered"));
      return false;
    }

    // Wrong password.
    if (Security.checkPassword(password, data.getPassword())) {
      ps.addAttempt();

      getPlugin().getLogger()
          .info(player.getName() + " entered the wrong password. (" + ps.getAttempt() + "/"
              + maxAttempt + ")");

      if (ps.getAttempt() >= maxAttempt) {
        getAuths().remove(player);
        player.kickPlayer(getLocale().getPlaceholderLocale(player, "login.kick"));
      } else {
        player.sendMessage(getLocale().getPlaceholderLocale(player, "login.wrong"));
      }

      return false;
    }

    // Stop authentication.
    getAuths().remove(uuid);

    // Update database.
    WorldAuthTable query = new WorldAuthTable();
    query.setUuid(player.getUniqueId().toString());
    query.setIp(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
    query.setLogout(false);
    query.setLoginDate(Instant.now().getEpochSecond());

    getDatabase().getWorldAuthTable().update(query);

    Bukkit.getPluginManager().callEvent(new LoginEvent(player));

    return true;
  }
}