package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getLocale;
import static org.bukkit.Bukkit.getLogger;

import lamberkeep.worldauth.event.LogoutEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Event listener contains actions on the player after logout.
 *
 * <p>Look {@link lamberkeep.worldauth.event.LogoutEvent} to learn more about this event.
 */
public class LogoutListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLogout(LogoutEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    player.kickPlayer(getLocale().getPlaceholderLocale(player, "logout.success"));

    getLogger().info(player.getName() + " successful logged out.");
  }
}