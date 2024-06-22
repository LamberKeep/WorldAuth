package lamberkeep.worldauth.data;

import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 * Authentications task storage class.
 *
 * <p>Contains the tasks of all current authorizations on the server.
 *
 * <p>More about content: {@link lamberkeep.worldauth.data.PlayerSessionData}.
 */
public class AuthData extends HashMap<String, PlayerSessionData> {

  @Override
  public PlayerSessionData remove(Object key) {
    get(key).stopAuth();
    return super.remove(key);
  }

  public void remove(Player player) {
    remove(player.getUniqueId());
  }
}