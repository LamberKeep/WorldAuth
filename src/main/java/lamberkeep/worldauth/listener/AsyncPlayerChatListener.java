package lamberkeep.worldauth.listener;

import static lamberkeep.worldauth.WorldAuth.getAuths;
import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.sql.SQLException;
import lamberkeep.worldauth.callable.LoginCallable;
import lamberkeep.worldauth.callable.RegisterCallable;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncPlayerChatEvent event) throws SQLException {
    Player player = event.getPlayer();
    String message = event.getMessage();

    WorldAuthTable query = getDatabase().getWorldAuthTable()
        .queryForId(player.getUniqueId().toString());

    // Player is authed.
    if (!getAuths().containsKey(player.getUniqueId().toString())) {
      return;
    }

    event.setCancelled(true);

    // Player is registered.
    if (query.isRegistered()) {
      Bukkit.getScheduler().callSyncMethod(getPlugin(), new LoginCallable(player, query, message));
    } else {
      Bukkit.getScheduler()
          .callSyncMethod(getPlugin(), new RegisterCallable(player, query, message));
    }
  }
}