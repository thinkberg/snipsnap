package com.neotis.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import com.neotis.util.ConnectionManager;

import javax.servlet.http.HttpSession;

public class UserManager {
  private static UserManager instance;

  public static synchronized UserManager getInstance() {
    if (null == instance) {
      instance = new UserManager();
    }
    return instance;
  }

  private Connection connection;

  private UserManager() {
    connection = ConnectionManager.getConnection();
  }

  public User getUser(HttpSession session) {
    return new User("Guest","Guest");
  }

  public User authenticate(String login, String passwd) {
	  User user = storageLoad(login);
	  if (null!=user 
					  && user.getPasswd().equals(passwd)) {
			 return user;
		} else {
			 return null;
		}
  }

  public User create(String login, String passwd) {
		return storageCreate(login, passwd);
  }

  public void store(User user) {
	  storageStore(user);
		return;
  }

  public void remove(User user) {
	  storageRemove(user);
		return;
  }

  public User load(String login) {
    return storageLoad(login);
  }

  // Storage System dependend Methods

  private User createUser(ResultSet result) throws SQLException {
    String login = result.getString("login");
    String passwd = result.getString("passwd");
		User user = new User(login, passwd);
    return user;
  }

	 private void storageStore(User user) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("UPDATE User SET login=?, passwd=?");
      statement.setString(1, user.getLogin());
      statement.setString(2, user.getPasswd());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private User storageCreate(String login, String passwd) {
    PreparedStatement statement = null;
    ResultSet result = null;

		User user = new User(login, passwd);

    try {
      statement = connection.prepareStatement("INSERT INTO User (login,passwd) VALUES (?,?)");
      statement.setString(1, login);
      statement.setString(2, passwd);

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
    }

    return user;
  }


  private void storageRemove(User user) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("DELETE FROM User WHERE login=?");
      statement.setString(1, user.getLogin());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private User storageLoad(String login) {
    User user = null;
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      statement = connection.prepareStatement("SELECT login, passwd FROM User WHERE login=?");
      statement.setString(1, login);

      result = statement.executeQuery();
      if (result.next()) {
        user = createUser(result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
    }
    return user;
  }

}
