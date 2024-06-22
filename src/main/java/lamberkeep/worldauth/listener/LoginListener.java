package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;
import static org.bukkit.Bukkit.getLogger;

import java.util.Objects;
import lamberkeep.worldauth.event.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Event listener contains actions on the player after login.
 *
 * <p>Look {@link lamberkeep.worldauth.event.LoginEvent} to learn more about this event.
 */
public class LoginListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLogin(LoginEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    // Restore player.
    player.resetTitle();
    player.loadData();

    /*
     * FIXME: Gamemode doesn't load server-side with player.loadData() method (I don't know why).
     *        Gamemode data contains in .dat file: world/playerdata/<uuid>.dat/playerGameType
     * XXX: Force respawn package to update client-side data.
     */
    Location loc = player.getLocation();
    player.teleport(
        Objects.requireNonNull(Bukkit.getWorld(
                Objects.requireNonNull(getPlugin().getConfig().getString("worlds.auth"))))
            .getSpawnLocation());
    player.teleport(loc);

    // Set gamemode.
    if (getPlugin().getConfig().getBoolean("gamemode.default")) {
      player.setGameMode(Bukkit.getDefaultGameMode());
    } else {
      player.setGameMode(
          GameMode.valueOf(getPlugin().getConfig().getString("gamemode.non-default")));
    }

    // Send messages.
    getLogger().info(player.getDisplayName() + " successful logged in.");
    player.sendMessage(getLocale().getPlaceholderLocale(player, "login.success"));
    Bukkit.broadcastMessage(getLocale().getPlaceholderLocale(player, "system.join"));
  }
}