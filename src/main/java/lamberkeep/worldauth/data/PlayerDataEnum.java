package lamberkeep.worldauth.data;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Player data structure.
 */
public enum PlayerDataEnum {
  // field("field_name", Field.class)
  ip("ip", InetAddress.class),
  password("password", String.class),
  oldPassword("old_password", String.class),
  loginDate("login_date", Long.class),
  registerDate("register_date", Long.class),
  logout("logout", Boolean.class);

  public static final Map<String, Class<?>> FIELDS = new HashMap<>();

  static {
    for (PlayerDataEnum data : values()) {
      FIELDS.put(data.fieldName, data.aClass);
    }
  }

  private final String fieldName;
  private final Class<?> aClass;

  PlayerDataEnum(String fieldName, Class<?> aClass) {
    this.fieldName = fieldName;
    this.aClass = aClass;
  }
}
