package lamberkeep.worldauth.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is logs in to the server successfully.
 */
public class LoginEvent extends PlayerEvent implements Cancellable {

  private static final HandlerList HANDLERS = new HandlerList();
  private Boolean isCancelled = false;

  public LoginEvent(Player who) {
    super(who);
  }

  @SuppressWarnings("unused")
  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  @NotNull
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  /**
   * Gets the cancellation state of this event. A cancelled event will not be executed in the
   * server, but will still pass to other plugins.
   *
   * @return true if this event is cancelled.
   */
  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  /**
   * Sets the cancellation state of this event. A cancelled event will not be executed in the
   * server, but will still pass to other plugins.
   *
   * @param cancel true if you wish to cancel this event.
   */
  @Override
  public void setCancelled(boolean cancel) {
    this.isCancelled = cancel;
  }
}