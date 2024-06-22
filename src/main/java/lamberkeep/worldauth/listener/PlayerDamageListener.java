package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = ((Player) event.getEntity()).getPlayer();

    assert player != null;
    if (getAuths().containsKey(player.getUniqueId().toString())) {
      if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
        player.teleport(
            Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(
                getPlugin().getConfig().getString("worlds.auth")))).getSpawnLocation());
      }
      event.setCancelled(true);
    }
  }
}