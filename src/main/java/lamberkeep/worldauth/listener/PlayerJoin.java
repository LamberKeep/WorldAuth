package lamberkeep.worldauth.listener;

import lamberkeep.worldauth.data.Status;
import lamberkeep.worldauth.task.Auth;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.*;
public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String nick = player.getName();
        String ip = Objects.requireNonNull(player.getAddress()).getHostName();

        e.setJoinMessage(null);

        if (!storage.containsKey(player))
            storage.put(player, new Status(player));

        if (!Objects.equals(storage.get(player).getIp(), ip) || storage.get(player).getSession() <= System.currentTimeMillis()) {
            player.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.auth")))).getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            player.setExp(1);

            if (data.getConfig().getString(nick + ".password") == null)
                player.sendTitle(locale.getLocale(player, "titles.register"), locale.getLocale(player,"titles.sub-title"), 0, (int) (config.getLong("timer.register") * 1000), 0);
            else
                player.sendTitle(locale.getLocale(player, "titles.auth"), locale.getLocale(player,"titles.sub-title"), 0, (int) (config.getLong("timer.register") * 1000), 0);

            storage.get(player).setAuth(new Auth(player));
        } else {
            data.loadData(player);

            Bukkit.broadcastMessage(locale.getLocale(player, "system.join"));
        }

    }
}
