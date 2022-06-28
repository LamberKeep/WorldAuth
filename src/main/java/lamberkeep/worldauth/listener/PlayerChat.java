package lamberkeep.worldauth.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.*;

public class PlayerChat implements Listener {

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String nick = player.getName();
        String password = data.getConfig().getString(nick + ".password");

        if (!storage.get(player).isAuthed()) {
            if (password == null) { // register
                data.getConfig().set(nick + ".password", message);
                data.saveConfig();

                storage.get(player).setAttempt(0);

                storage.get(player).getAuth().task.cancel();
                storage.get(player).setAuth(null);

                player.resetTitle();
                player.setLevel(0);
                player.setExp(0);

                Objects.requireNonNull(player.getPlayer()).teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.hub-or-survival")))).getSpawnLocation());
                player.setGameMode(GameMode.SURVIVAL);

                player.sendMessage(locale.getLocale(player, "registed"));
                Bukkit.broadcastMessage(locale.getLocale(player, "system.join"));
            } else { // auth
                if (message.equals(data.getConfig().getString(nick + ".password"))) { // success
                    storage.get(player).setAttempt(0);

                    storage.get(player).getAuth().task.cancel();
                    storage.get(player).setAuth(null);

                    data.loadData(player);

                    player.resetTitle();

                    if (config.getBoolean("force-gamemode.enabled"))
                        player.setGameMode(GameMode.valueOf(config.getString("force-gamemode.gamemode")));
                    else
                        player.setGameMode(GameMode.valueOf(data.getConfig().getString(nick + ".gamemode")));

                    player.sendMessage(locale.getLocale(player, "auth.success"));
                    Bukkit.broadcastMessage(locale.getLocale(player, "system.join"));
                } else { // failed
                    storage.get(player).addAttempt();
                    if (storage.get(player).getAttempt() >= config.getInt("join-attempts")) {
                        storage.get(player).getAuth().task.cancel();
                        storage.get(player).setAuth(null);

                        player.kickPlayer(locale.getLocale(player, "auth.kick"));
                    } else
                        player.sendMessage(locale.getLocale(player, "auth.wrong"));
                }
            }
            event.setCancelled(true);
        }
    }
}
