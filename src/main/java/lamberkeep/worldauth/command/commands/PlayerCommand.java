package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getLocale;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, String[] args) {
    if (sender instanceof ConsoleCommandSender) {
      sender.sendMessage(
          Objects.requireNonNull(getLocale().getConfig().getString("getPlugin().console")));
      return false;
    }

    try {
      return onCommand((Player) sender, command, label, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unused")
  protected abstract boolean onCommand(Player player, Command cmd, String label, String[] args)
      throws UnknownHostException, SQLException, SocketException, NoSuchAlgorithmException;
}