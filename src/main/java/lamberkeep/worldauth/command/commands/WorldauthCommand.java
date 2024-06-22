package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldauthCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label,
      String[] args) {
    if (!sender.hasPermission("worldauth.admin")) {
      sender.sendMessage(getLocale().getLocale("getPlugin().permission"));
      return false;
    }

    if (args.length == 0) {
      sender.sendMessage(getLocale().getLocale("getPlugin().usage"));
    } else {
      if (args[0].equalsIgnoreCase("reload")) {
        getPlugin().reloadConfig();
        getLocale().reloadConfig();
        sender.sendMessage(getLocale().getLocale("getPlugin().reloaded"));
        return true;
      }

      if (args[0].equalsIgnoreCase("goto")) {
        Player player = (Player) sender;

        if (args.length == 1) {
          sender.sendMessage(getLocale().getLocale("goto.usage"));
          return false;
        }

        if (args[1].equalsIgnoreCase("hub") || args[1].equalsIgnoreCase("survival")) {
          player.teleport(
              Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(
                      getPlugin().getConfig().getString("worlds.hub-or-survival"))))
                  .getSpawnLocation());
          return true;
        }

        if (args[1].equalsIgnoreCase("auth")) {
          player.teleport(
              Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(
                  getPlugin().getConfig().getString("worlds.auth")))).getSpawnLocation()
          );
          return true;
        }
      }
    }
    return false;
  }
}