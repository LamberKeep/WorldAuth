package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;
import static org.bukkit.Bukkit.getLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void onLeave(PlayerQuitEvent e) throws IOException {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();

    /*
     * XXX: Protect playerdata from server overwrite.
     * NOTE: File won't be replaced without REPLACE_EXISTING option.
     */
    if (player.getWorld() == Bukkit.getWorld(
        Objects.requireNonNull(getPlugin().getConfig().getString("worlds.auth")))
        && new File("world/playerdata/" + uuid + ".dat").exists()) {
      getLogger().info("Protecting " + player.getName() + "'s save file.");
      Files.move(
          Path.of("world/playerdata/" + uuid + ".dat"),
          Path.of("world/playerdata/" + uuid + ".dat_auth"));
    }

    player.resetTitle(); // Reset title to avoid client bugs.

    // Send messages.
    e.setQuitMessage(getLocale().getPlaceholderLocale(player, "system.leave"));
  }
}