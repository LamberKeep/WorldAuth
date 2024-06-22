package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.sql.SQLException;
import lamberkeep.worldauth.callable.LoginCallable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class LoginCommand extends PlayerCommand {

  @Override
  public boolean onCommand(Player player, Command command, String label, String[] args)
      throws SQLException {
    if (args.length != 1) {
      player.sendMessage(getLocale().getLocale("login.usage"));
      return false;
    }

    Bukkit.getScheduler().callSyncMethod(getPlugin(),
        new LoginCallable(player,
            getDatabase().getWorldAuthTable().queryForId(player.getUniqueId().toString()),
            args[0]));

    return true;
  }
}