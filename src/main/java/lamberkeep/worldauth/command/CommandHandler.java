package lamberkeep.worldauth.command;

import static lamberkeep.worldauth.WorldAuth.getPlugin;

import java.util.Objects;
import lamberkeep.worldauth.command.commands.ChangepasswordCommand;
import lamberkeep.worldauth.command.commands.LoginCommand;
import lamberkeep.worldauth.command.commands.LogoutCommand;
import lamberkeep.worldauth.command.commands.RegisterCommand;
import lamberkeep.worldauth.command.commands.UnregisterCommand;
import lamberkeep.worldauth.command.commands.WorldauthCommand;
import lamberkeep.worldauth.command.tabcomplete.TabComplete;

public class CommandHandler {

  public CommandHandler() {
    //  Commands.
    Objects.requireNonNull(getPlugin().getCommand("changepassword"))
        .setExecutor(new ChangepasswordCommand());
    Objects.requireNonNull(getPlugin().getCommand("login")).setExecutor(new LoginCommand());
    Objects.requireNonNull(getPlugin().getCommand("logout")).setExecutor(new LogoutCommand());
    Objects.requireNonNull(getPlugin().getCommand("register")).setExecutor(new RegisterCommand());
    Objects.requireNonNull(getPlugin().getCommand("unregister"))
        .setExecutor(new UnregisterCommand());
    Objects.requireNonNull(getPlugin().getCommand("worldauth")).setExecutor(new WorldauthCommand());

    //  Tab completes.
    TabComplete tc = new TabComplete();
    Objects.requireNonNull(getPlugin().getCommand("changepassword")).setTabCompleter(tc);
    Objects.requireNonNull(getPlugin().getCommand("login")).setTabCompleter(tc);
    Objects.requireNonNull(getPlugin().getCommand("logout")).setTabCompleter(tc);
    Objects.requireNonNull(getPlugin().getCommand("register")).setTabCompleter(tc);
    Objects.requireNonNull(getPlugin().getCommand("worldauth")).setTabCompleter(tc);
  }
}