package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;
import static org.bukkit.Bukkit.getLogger;

import java.io.File;
import java.sql.SQLException;
import lamberkeep.worldauth.event.UnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnregisterCommand implements CommandExecutor {

  @Override
  @Deprecated
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    OfflinePlayer player = null;

    if (args.length > 1) {
      sender.sendMessage(getLocale().getLocale("unregister.usage"));
      return false;
    }

    if (args.length == 0) {
      if (sender instanceof ConsoleCommandSender) {
        sender.sendMessage(getLocale().getLocale("getPlugin().console"));
        return false;
      }

      player = (OfflinePlayer) sender;
    }

    if (args.length == 1 && sender.hasPermission("worldauth.admin")) {
      player = Bukkit.getOfflinePlayer(args[0]);
    }

    if (player == null) {
      sender.sendMessage(getLocale().getLocale("unregister.not-exists"));
      return false;
    }

    try {
      getDatabase().getWorldAuthTable().deleteById(player.getUniqueId().toString());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    if (player.isOnline()) {
      Bukkit.getPluginManager().callEvent(new UnregisterEvent((Player) player));
    }

    if (getPlugin().getConfig().getBoolean("settings.purge-account")) {
      for (String format : new String[]{".dat", ".dat_old", ".dat_auth"}) {
        File playerData = new File("world/playerdata/" + player.getUniqueId() + format);
        getLogger().info("Purge " + player.getName() + " player data.");
        if (playerData.delete()) {
          getLogger().info(playerData.getName() + " was deleted.");
        }
      }
    }

    sender.sendMessage(getLocale().getLocale("unregister.success"));

    return true;
  }
}