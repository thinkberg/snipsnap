package com.neotis.snip;

import com.neotis.util.ConnectionManager;

import java.sql.*;

public class SnipSpace {
  private Connection connection;

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

  public void store(Snip snip) {
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

  public Snip create(String name, String content) {
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

  public void remove(Snip snip) {
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
}
