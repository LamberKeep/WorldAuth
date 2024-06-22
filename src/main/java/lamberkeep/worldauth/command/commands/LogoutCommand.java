package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getDatabase;

import java.sql.SQLException;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import lamberkeep.worldauth.event.LogoutEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LogoutCommand extends PlayerCommand {

  @Override
  public boolean onCommand(Player player, Command cmd, String label, String[] args)
      throws SQLException {

    WorldAuthTable query = new WorldAuthTable();
    query.setUuid(player.getUniqueId().toString());
    query.setLogout(true);

    getDatabase().getWorldAuthTable().update(query);

    Bukkit.getPluginManager().callEvent(new LogoutEvent(player));

    return true;
  }
}