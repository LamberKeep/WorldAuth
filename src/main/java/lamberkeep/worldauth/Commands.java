package lamberkeep.worldauth;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.*;

public class Commands implements CommandExecutor, TabCompleter {

    public Commands(WorldAuth plugin) {
//      commands
        Objects.requireNonNull(plugin.getCommand("worldauth")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("logout")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("changepassword")).setExecutor(this);
//      tab completes
        Objects.requireNonNull(plugin.getCommand("worldauth")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Objects.requireNonNull(locale.getConfig().getString("plugin.console")));
            return false;
        }

        Player player = (Player) sender;
        String nick = player.getName();

        if (player.getWorld() == Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.auth")))) {
            player.sendMessage(Objects.requireNonNull(locale.getLocale(player, "auth.command")));
            return false;
        }

        if (label.equalsIgnoreCase("worldauth") || label.equalsIgnoreCase("wa")) {
            if (!player.hasPermission("doublejump.admin")) {
                player.sendMessage(locale.getLocale(player, "plugin.permission"));
                return true;
            }

            if (args.length == 0)
                player.sendMessage(locale.getLocale(player, "plugin.usage"));
            else {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadConfig();
                    data.reloadConfig();
                    locale.reloadConfig();
                    config = plugin.getConfig();
                    player.sendMessage(locale.getLocale(player, "plugin.reloaded"));
                }
            }
        }

        if (label.equalsIgnoreCase("logout") || label.equalsIgnoreCase("q")) {
            player.kickPlayer(locale.getLocale(player,"logout"));
            storage.get(player).setSession(0L);
        }

        if (label.equalsIgnoreCase("changepassword") || label.equalsIgnoreCase("changepass")) {
            if (!(player.getWorld() == Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("worlds.auth"))))) {
                if (args.length == 2) {
                    if (args[0].equals(data.getConfig().getString(nick + ".password"))) {
                        data.getConfig().set(nick + ".password", args[1]);
                        data.saveConfig();
                        sender.sendMessage(locale.getLocale(player, "changepass.success"));
                    } else
                        sender.sendMessage(locale.getLocale(player, "changepass.wrong"));
                } else
                    sender.sendMessage(locale.getLocale(player, "changepass.usage"));
            }
        }
        return false;
    }

    List<String> results = new ArrayList <> ();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if (command.getName().equalsIgnoreCase("worldauth") || command.getName().equalsIgnoreCase("wa")) {
                if (sender.hasPermission("doublejump.admin")) {

                    results.clear();
                    results.add("reload");

                    return results;
                }
            }
        }
        return null;
    }

}
