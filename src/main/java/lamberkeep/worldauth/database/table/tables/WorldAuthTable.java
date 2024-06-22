package lamberkeep.worldauth.database.table.tables;

import static com.j256.ormlite.field.DataType.BOOLEAN;
import static com.j256.ormlite.field.DataType.LONG;
import static com.j256.ormlite.field.DataType.STRING;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lamberkeep.worldauth.database.table.AbstractTable;

/**
 * Class that contains all player data.
 */
@DatabaseTable(tableName = "worldauth")
public class WorldAuthTable extends AbstractTable {

  @DatabaseField(columnName = "uuid", id = true, dataType = STRING, width = 36)
  private String uuid;

  @DatabaseField(columnName = "ip", dataType = STRING, width = 15)
  private String ip;

  @DatabaseField(columnName = "password", dataType = STRING, width = 36)
  private String password;

  @DatabaseField(columnName = "old_password", dataType = STRING, width = 36)
  private String oldPassword;

  @DatabaseField(columnName = "login_date", dataType = LONG, width = 11)
  private Long loginDate;

  @DatabaseField(columnName = "register_date", dataType = LONG, width = 11)
  private Long registerDate;

  @DatabaseField(columnName = "logout", dataType = BOOLEAN)
  private Boolean logout;

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean isRegistered() {
    return password != null;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public void setLoginDate(Long loginDate) {
    this.loginDate = loginDate;
  }

  public void setRegisterDate(Long registerDate) {
    this.registerDate = registerDate;
  }

  public Boolean getLogout() {
    return logout;
  }

  public void setLogout(Boolean logout) {
    this.logout = logout;
  }
}
