package lamberkeep.worldauth.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static java.lang.Math.round;
import static lamberkeep.worldauth.WorldAuth.*;

public class Auth implements Runnable {
    public BukkitTask task;
    private final Player player;
    private final long timer;

    public Auth(Player player) {
        this.player = player;
        timer = System.currentTimeMillis() + config.getLong("timer.register") * 1000;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 1);
    }

    @Override
    public void run() {
        float timeLeft = (float) (timer - System.currentTimeMillis()) / 1000;
        if (timeLeft > 0) {
            player.setLevel(round(timeLeft));
            player.setExp(timeLeft / config.getLong("timer.register"));
        } else {
            player.kickPlayer(locale.getLocale(player, "system.time-is-up"));
            task.cancel();
        }
    }
}