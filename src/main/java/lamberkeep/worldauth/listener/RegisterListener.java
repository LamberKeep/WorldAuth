package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;
import static org.bukkit.Bukkit.getLogger;

import java.util.Objects;
import lamberkeep.worldauth.event.RegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Event listener contains actions on the player after registration.
 *
 * <p>Look {@link lamberkeep.worldauth.event.RegisterEvent} to learn more about this event.
 */
public class RegisterListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRegister(RegisterEvent event) {
    if (event.isCancelled()) {
      return;
    }

    Player player = event.getPlayer();

    // Reset player.
    player.resetTitle();
    player.setLevel(0);
    player.setExp(0);
    player.teleport(Objects.requireNonNull(
            Bukkit.getWorld(getPlugin().getConfig().getString("worlds.hub-or-survival", "world")))
        .getSpawnLocation());

    // Set gamemode.
    if (getPlugin().getConfig().getBoolean("gamemode.default")) {
      player.setGameMode(Bukkit.getDefaultGameMode());
    } else {
      player.setGameMode(
          GameMode.valueOf(getPlugin().getConfig().getString("gamemode.non-default")));
    }

    // Send messages.
    getLogger().info(player.getName() + " successfully registered.");
    player.sendMessage(getLocale().getPlaceholderLocale(player, "register.success"));
    Bukkit.broadcastMessage(getLocale().getPlaceholderLocale(player, "system.join"));
  }
}