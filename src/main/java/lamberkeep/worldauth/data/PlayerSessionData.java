package lamberkeep.worldauth.data;

import static lamberkeep.worldauth.WorldAuth.getPlugin;

import lamberkeep.worldauth.task.AuthTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Authentication data class.
 */
public class PlayerSessionData {

  private final int taskId;
  private int attempt = 0;

  public PlayerSessionData(Player player) {
    taskId = new AuthTask(player).runTaskTimer(getPlugin(), 0, 1).getTaskId();
  }

  public int getAttempt() {
    return attempt;
  }

  @Deprecated
  public void setAttempt(Integer attempt) {
    this.attempt = attempt;
  }

  public void addAttempt() {
    this.attempt += 1;
  }

  @Deprecated
  public boolean isAuthing() {
    return Bukkit.getScheduler().isQueued(taskId);
  }

  public void stopAuth() {
    Bukkit.getScheduler().cancelTask(taskId);
  }
}