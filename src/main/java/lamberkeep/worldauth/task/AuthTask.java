package lamberkeep.worldauth.task;

import static java.lang.Math.round;
import static lamberkeep.worldauth.WorldAuth.getLocale;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Authentication task class.
 */
public class AuthTask extends BukkitRunnable {

  private final long timer = System.currentTimeMillis()
      + getPlugin().getConfig().getLong("timer.register") * 1000;
  private final Player player;

  public AuthTask(Player player) {
    this.player = player;
  }

  @Override
  public void run() {
    float timeLeft = (float) (timer - System.currentTimeMillis()) / 1000;
    if (timeLeft > 0) {
      player.setLevel(round(timeLeft));
      player.setExp(timeLeft / getPlugin().getConfig().getLong("timer.register"));
    } else {
      player.kickPlayer(getLocale().getPlaceholderLocale(player, "system.time-is-up"));
      this.cancel();
    }
  }
}