package lamberkeep.worldauth.command.tabcomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

public class TabComplete implements TabCompleter {

  final List<String> results = new ArrayList<>();

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, Command command,
      @NotNull String alias, String[] args) {
    if (command.getName().equalsIgnoreCase("worldauth")
        || command.getName().equalsIgnoreCase("wa")) {
      if (sender.hasPermission("worldauth.admin")) {
        results.clear();

        if (args.length == 1) {
          results.add("reload");
          results.add("goto");
        }

        if (args.length == 2) {
          if (Objects.equals(args[0], "goto")) {
            results.add("auth");
            results.add("hub");
            results.add("survival");
          }
        }

        Collections.sort(results);
        return results;
      }
    }

    if (command.getName().equalsIgnoreCase("login")
        || command.getName().equalsIgnoreCase("l")) {
      results.clear();
      results.add("<password>");
      return results;
    }

    if (command.getName().equalsIgnoreCase("register")
        || command.getName().equalsIgnoreCase("reg")) {
      results.clear();
      results.add("<password>");
      return results;
    }

    if (command.getName().equalsIgnoreCase("unregister")
        && (sender.hasPermission("worldauth.unregister"))) {
      results.clear();
      results.add("<player/uuid>");
      return results;
    }

    if (command.getName().equalsIgnoreCase("changepassword")
        || command.getName().equalsIgnoreCase("changepass")) {
      results.clear();
      if (args.length == 1) {
        results.add("<old_password>");
      }
      if (args.length == 2) {
        results.add("<new_password>");
      }
      return results;
    }

    return null;
  }

}
