package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;
import static org.bukkit.Bukkit.getLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Objects;
import lamberkeep.worldauth.data.PlayerSessionData;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void onJoin(PlayerJoinEvent e) throws SQLException, IOException {
    Player player = e.getPlayer();
    String uuid = player.getUniqueId().toString();
    String ip = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
    WorldAuthTable query = getDatabase().getWorldAuthTable().queryForId(uuid);

    e.setJoinMessage(null);

    // XXX: Check protected playerdata and release it.
    if (new File("world/playerdata/" + uuid + ".dat_auth").exists()) {
      getLogger().info("Releasing " + player.getName() + "'s save file.");
      Files.move(
          Path.of("world/playerdata/" + uuid + ".dat_auth"),
          Path.of("world/playerdata/" + uuid + ".dat"),
          StandardCopyOption.REPLACE_EXISTING);
    }

    // Player has a valid session, skip authorization.
    if (query.isRegistered()
        && ip.equals(query.getIp())
        && player.getLastPlayed() + getPlugin().getConfig().getLong("timer.session") * 1000
        >= System.currentTimeMillis()
        && !query.getLogout()
    ) {
      getLogger().info(player.getName() + " has valid session, skip authorization.");
      Bukkit.broadcastMessage(getLocale().getPlaceholderLocale(player, "system.join"));
      return;
    }

    // Reset player.
    player.setGameMode(GameMode.ADVENTURE);
    player.teleport(
        Objects.requireNonNull(Bukkit.getWorld(
                Objects.requireNonNull(getPlugin().getConfig().getString("worlds.auth"))))
            .getSpawnLocation()
    );

    // Start new authentication.
    getAuths().put(uuid, new PlayerSessionData(player));

    // Send player title.
    if (query.getPassword() == null) {
      player.sendTitle(
          getLocale().getPlaceholderLocale(player, "titles.register.title"),
          getLocale().getPlaceholderLocale(player, "titles.register.sub-title"),
          0,
          (int) (getPlugin().getConfig().getLong("timer.register") * 1000),
          0
      );
    } else {
      player.sendTitle(
          getLocale().getPlaceholderLocale(player, "titles.auth.title"),
          getLocale().getPlaceholderLocale(player, "titles.auth.sub-title"),
          0,
          (int) (getPlugin().getConfig().getLong("timer.register") * 1000),
          0
      );
    }
  }
}