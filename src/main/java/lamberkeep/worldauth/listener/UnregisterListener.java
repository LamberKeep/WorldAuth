package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getLocale;

import lamberkeep.worldauth.event.UnregisterEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Event listener contains actions on the player after deleting account.
 *
 * <p>Look {@link lamberkeep.worldauth.event.UnregisterEvent} to learn more about this event.
 */
public class UnregisterListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void onUnreginster(UnregisterEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    player.resetTitle(); // Reset title to avoid client bugs.

    player.kickPlayer(getLocale().getPlaceholderLocale(player, "unregister.success-self"));
  }
}