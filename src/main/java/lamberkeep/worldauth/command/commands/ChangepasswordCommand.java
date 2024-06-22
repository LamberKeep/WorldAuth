package lamberkeep.worldauth.command.commands;

import static lamberkeep.worldauth.WorldAuth.getDatabase;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import lamberkeep.worldauth.security.Security;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class ChangepasswordCommand extends PlayerCommand {

  @Override
  public boolean onCommand(Player player, Command cmd, String label, String[] args)
      throws SocketException, UnknownHostException, NoSuchAlgorithmException, SQLException {
    String uuid = player.getUniqueId().toString();

    if (args.length != 2) {
      player.sendMessage(getLocale().getPlaceholderLocale(player, "changepass.usage"));
      return false;
    }

    WorldAuthTable data;

    data = getDatabase().getWorldAuthTable().queryForId(uuid);

    if (Security.checkPassword(args[0], data.getPassword())) {
      getPlugin().getLogger().info(player.getDisplayName() + " entered the wrong password.");
      player.sendMessage(getLocale().getPlaceholderLocale(player, "changepass.wrong"));
      return false;
    }

    data.setIp(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
    data.setPassword(args[1]);
    data.setOldPassword(data.getPassword());

    getDatabase().getWorldAuthTable().update(data);

    getPlugin().getLogger().info(player.getName() + " changed password successfully.");
    player.sendMessage(getLocale().getPlaceholderLocale(player, "changepass.success"));

    return true;
  }
}