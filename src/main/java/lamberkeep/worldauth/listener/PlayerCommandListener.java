package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getLocale;

import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommand(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    String label = event.getMessage().split(" ")[0];

    // Authorization commands whitelist.
    if (getAuths().containsKey(player.getUniqueId().toString())
        && !label.equalsIgnoreCase("/login")
        && !label.equalsIgnoreCase("/l")
        && !label.equalsIgnoreCase("/register")
        && !label.equalsIgnoreCase("/reg")
    ) {
      player.sendMessage(
          Objects.requireNonNull(getLocale().getPlaceholderLocale(player, "system.command")));
      event.setCancelled(true);
    }
  }
}