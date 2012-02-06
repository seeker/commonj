package io;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
public void stopPool();
public void startPool() throws Exception;

public void returnConnection(Connection cn);
public Connection getConnection() throws SQLException;
}
