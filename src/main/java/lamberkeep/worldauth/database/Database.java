package lamberkeep.worldauth.database;

import static java.util.Objects.requireNonNull;
import static lamberkeep.worldauth.WorldAuth.getPlugin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.HashMap;
import lamberkeep.worldauth.database.table.AbstractTable;
import lamberkeep.worldauth.database.table.tables.WorldAuthTable;
import org.reflections.Reflections;

public class Database {

  private final JdbcPooledConnectionSource connectionSource;
  private final HashMap<String, Object> daoList = new HashMap<>();

  public Database() throws SQLException {

    // create a connection source to our database
    connectionSource = new JdbcPooledConnectionSource(
        "jdbc:"
            + requireNonNull(getPlugin().getConfig().getString("storage.type"))
            + "://"
            + requireNonNull(getPlugin().getConfig().getString("storage.host"))
            + ':'
            + requireNonNull(getPlugin().getConfig().getString("storage.port"))
            + '/'
            + requireNonNull(getPlugin().getConfig().getString("storage.database")),
        requireNonNull(getPlugin().getConfig().getString("storage.user")),
        requireNonNull(getPlugin().getConfig().getString("storage.password")));

    for (Class<? extends AbstractTable> table : new Reflections(
        "io.github.lamberkeep.essentials.database.table.tables").getSubTypesOf(
        AbstractTable.class)) {
      TableUtils.createTable(connectionSource, table);
      daoList.put(table.getSimpleName(), DaoManager.createDao(connectionSource, table));
    }
  }

  public JdbcPooledConnectionSource getConnectionSource() {
    return connectionSource;
  }

  @SuppressWarnings("unchecked")
  public Dao<WorldAuthTable, String> getWorldAuthTable() {
    return (Dao<WorldAuthTable, String>) daoList.get("WorldAuthTable");
  }
}
