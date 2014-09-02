/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

import java.sql.Connection;
import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

public interface ConnectionPool {
	public void stopPool();

	public void startPool() throws Exception;

	public void returnConnection(Connection cn);

	public Connection getConnection() throws SQLException;

	public ConnectionSource getConnectionSource() throws SQLException;
}
