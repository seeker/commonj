/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import java.sql.Connection;
import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Interface for database connection pools.
 * 
 * @author Nicholas Wright
 *
 */
public interface ConnectionPool {
	/**
	 * Stop the pool and clean up resources.
	 */
	public void stopPool();

	/**
	 * Start the pool so it can provide connections.
	 * 
	 * @throws Exception
	 *             if there is an error starting the pool
	 */
	public void startPool() throws Exception;

	/**
	 * Return a connection to the pool.
	 * 
	 * @param cn
	 *            to return
	 */
	public void returnConnection(Connection cn);

	/**
	 * Get a connection from the pool.
	 * 
	 * @return a database connection
	 * @throws SQLException
	 *             if there is an error getting a connection
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * Get a connection from the pool.
	 * 
	 * @return a database connection
	 * @throws SQLException
	 *             if there is an error getting a connection
	 */
	public ConnectionSource getConnectionSource() throws SQLException;
}
