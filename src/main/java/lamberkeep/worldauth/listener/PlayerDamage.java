package lamberkeep.worldauth.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

import static lamberkeep.worldauth.WorldAuth.*;

public class PlayerDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = ((Player) event.getEntity()).getPlayer();

        assert player != null;
        if (player.getWorld() == Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.auth"))))) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(config.getString("worlds.auth")))).getSpawnLocation());
            }
            event.setCancelled(true);
        }
    }
}
