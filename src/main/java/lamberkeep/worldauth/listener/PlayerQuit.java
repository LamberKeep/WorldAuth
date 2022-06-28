package lamberkeep.worldauth.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.*;
public class PlayerQuit implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (storage.get(player).isAuthed()) {
            storage.get(player).updateSession();
            storage.get(player).updateIp();

            storage.get(player).setAttempt(0);

            data.getConfig().set(player.getName() + ".gamemode", player.getGameMode().toString());
            data.saveData(player);

            e.setQuitMessage(locale.getLocale(player, "system.leave"));
        }
        player.resetTitle();
    }
}
