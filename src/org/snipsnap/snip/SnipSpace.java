package com.neotis.snip;

import com.neotis.util.ConnectionManager;
import com.neotis.snip.filter.LinkTester;

import java.sql.*;
import java.util.Map;

public class SnipSpace implements LinkTester {
  private Connection connection;
  private Map cache;

  private static SnipSpace instance;

  public static synchronized SnipSpace getInstance() {
    if (null == instance) {
      instance = new SnipSpace();
    }
    return instance;
  }

  private SnipSpace() {
    connection = ConnectionManager.getConnection();
  }

  public boolean exists(String name) {
    return (null != load(name));
  }

  public Snip load(String name) {
    Snip snip = null;
    if (cache.containsKey(name)) {
      snip = (Snip) cache.get(name);
    } else {
      snip = storageLoad(name);
      if (null != snip) {
        cache.put(name, snip);
      }
    }
    return snip;
  }

  public void store(Snip snip) {
    storageStore(snip);
    return;
  }

  public Snip create(String name, String content) {
    Snip snip = storageCreate(name, content);
    cache.put(name, snip);
  }

  public void remove(Snip snip) {
    cache.remove(snip.getName());
    storageRemove(snip);
    return;
  }

  private void storageStore(Snip snip) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("UPDATE Snip SET name=?, content=? WHERE name=?");
      statement.setString(1, snip.getName());
      statement.setString(2, snip.getContent());
      statement.setString(3, snip.getName());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private Snip storageCreate(String name, String content) {
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      statement = connection.prepareStatement("INSERT INTO Snip (name, content) VALUES (?,?)");
      statement.setString(1, name);
      statement.setString(2, content);

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(result);
    }

    Snip snip = new Snip(name, content);
    return snip;
  }


  private void storageRemove(Snip snip) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("DELETE FROM Snip WHERE name=?");
      statement.setString(1, snip.getName());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private Snip storageLoad(String name) {
    Snip snip = null;
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      statement = connection.prepareStatement("SELECT name, content FROM Snip WHERE name=?");
      statement.setString(1, name);

      result = statement.executeQuery();
      if (result.next()) {
        snip = new Snip(result.getString("name"), result.getString("content"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return snip;
  }

}
