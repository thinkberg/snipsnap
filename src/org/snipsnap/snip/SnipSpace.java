package com.neotis.snip;

import com.neotis.app.Application;
import com.neotis.snip.filter.LinkTester;
import com.neotis.util.ConnectionManager;
import com.neotis.util.Queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnipSpace implements LinkTester {
  private Connection connection;
  private Map cache;
  private Map missing;
  private Queue changed;

  private static SnipSpace instance;

  public static synchronized SnipSpace getInstance() {
    if (null == instance) {
      instance = new SnipSpace();
    }
    return instance;
  }

  private SnipSpace() {
    missing = new HashMap();
    changed = new Queue(10);
    cache = new HashMap();
    connection = ConnectionManager.getConnection();
    changed.fill(storageByRecent(10));
  }

  public List getChanged() {
    return changed.get();
  }

  public List getComments(Snip snip) {
    return storageByComments(snip);
  }

  public List getByUser(String login) {
    return storageByUser(login);
  }

  public Collection getChildren(Snip snip) {
    return storageByParent(snip);
  }

  public boolean exists(String name) {
    if (missing.containsKey(name)) {
      return false;
    }
    if (null == load(name)) {
      missing.put(name, new Integer(0));
      return false;
    } else {
      return true;
    }
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

  public void store(Snip snip, Application app) {
    snip.setMUser(app.getUser());
    store(snip);
    return;
  }

  public void store(Snip snip) {
    changed.add(snip);
    snip.setMTime(new Timestamp(new Date().getTime()));
    storageStore(snip);
    return;
  }


  public Snip create(String name, String content, Application app) {
    Snip snip = storageCreate(name, content, app);
    cache.put(name, snip);
    if (missing.containsKey(name)) {
      missing.remove(name);
    }
    changed.add(snip);
    return snip;
  }

  public void remove(Snip snip) {
    cache.remove(snip.getName());
    changed.remove(snip);
    storageRemove(snip);
    return;
  }

  // Storage System dependend Methods

  private Snip createSnip(ResultSet result) throws SQLException {
    String name = result.getString("name");
    String content = result.getString("content");

    Snip snip = new Snip(name, content);
    snip.setCTime(result.getTimestamp("cTime"));
    snip.setMTime(result.getTimestamp("mTime"));
    snip.setCUser(result.getString("cUser"));
    snip.setMUser(result.getString("mUser"));
    String commentString = result.getString("commentSnip");
    if (!result.wasNull()) {
      snip.setComment(load(commentString));
    }
    String parentString = result.getString("parentSnip");
    if (!result.wasNull()) {
      snip.setParent(load(parentString));
    }
    return snip;
  }

  private Snip cacheLoad(ResultSet result) throws SQLException {
    Snip snip = null;
    String name = result.getString("name");
    if (cache.containsKey(name)) {
      snip = (Snip) cache.get(name);
    } else {
      snip = createSnip(result);
      cache.put(name, snip);
    }
    return snip;
  }

  private List storageByRecent(int size) {
    PreparedStatement statement = null;
    ResultSet result = null;
    List snips = new ArrayList();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip FROM Snip ORDER by mTime DESC");

      result = statement.executeQuery();
      Snip snip = null;
      while (result.next() && --size > 0) {
        snip = cacheLoad(result);
        snips.add(snip);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(result);
    }
    return snips;
  }


  private List storageByUser(String login) {
    PreparedStatement statement = null;
    ResultSet result = null;
    List snips = new ArrayList();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip FROM Snip WHERE mUser=?");
      statement.setString(1, login);

      result = statement.executeQuery();
      Snip snip = null;
      while (result.next()) {
        snip = cacheLoad(result);
        snips.add(snip);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(result);
    }
    return snips;
  }


  private List storageByComments(Snip parent) {
    PreparedStatement statement = null;
    ResultSet result = null;
    List comments = new ArrayList();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip FROM Snip WHERE commentSnip=?");
      statement.setString(1, parent.getName());

      result = statement.executeQuery();
      Snip snip = null;
      while (result.next()) {
        snip = cacheLoad(result);
        comments.add(snip);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(result);
    }
    return comments;
  }

  private Collection storageByParent(Snip parent) {
    PreparedStatement statement = null;
    ResultSet result = null;
    Collection children = new ArrayList();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip FROM Snip WHERE parentSnip=?");
      statement.setString(1, parent.getName());

      result = statement.executeQuery();
      Snip snip = null;
      while (result.next()) {
        snip = cacheLoad(result);
        children.add(snip);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(result);
    }
    return children;
  }

  private void storageStore(Snip snip) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("UPDATE Snip " +
                                              "SET name=?, content=?, cTime=?, mTime=?, cUser=?, mUser=?, parentSnip=?, commentSnip=? WHERE name=?");
      statement.setString(1, snip.getName());
      statement.setString(2, snip.getContent());
      statement.setTimestamp(3, snip.getCTime());
      statement.setTimestamp(4, snip.getMTime());
      statement.setString(5, snip.getCUser());
      statement.setString(6, snip.getMUser());
      Snip parent = snip.getParent();
      if (null == parent) {
        statement.setNull(7, Types.VARCHAR);
      } else {
        statement.setString(7, parent.getName());
      }
      Snip comment = snip.getComment();
      if (null == comment) {
        statement.setNull(8, Types.VARCHAR);
      } else {
        statement.setString(8, comment.getName());
      }
      statement.setString(9, snip.getName());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private Snip storageCreate(String name, String content, Application app) {
    PreparedStatement statement = null;
    ResultSet result = null;

    String login = app.getUser().getLogin();
    Snip snip = new Snip(name, content);
    Timestamp cTime = new Timestamp(new Date().getTime());
    Timestamp mTime = (Timestamp) cTime.clone();
    snip.setCTime(cTime);
    snip.setMTime(mTime);
    snip.setCUser(login);
    snip.setMUser(login);

    try {
      statement = connection.prepareStatement("INSERT INTO Snip (name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip) VALUES (?,?,?,?,?,?,?,?)");
      statement.setString(1, name);
      statement.setString(2, content);
      statement.setTimestamp(3, cTime);
      statement.setTimestamp(4, mTime);
      statement.setString(5, login);
      statement.setString(6, login);
      Snip parent = snip.getParent();
      if (null == parent) {
        statement.setNull(7, Types.VARCHAR);
      } else {
        statement.setString(7, parent.getName());
      }
      Snip comment = snip.getComment();
      if (null == comment) {
        statement.setNull(8, Types.VARCHAR);
      } else {
        statement.setString(8, comment.getName());
      }

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
    }

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
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip FROM Snip WHERE name=?");
      statement.setString(1, name);

      result = statement.executeQuery();
      if (result.next()) {
        snip = createSnip(result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
    }
    return snip;
  }
}
