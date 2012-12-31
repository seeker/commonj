/*  Copyright (C) 2012  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dozedoff.commonj.io;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

/**
 * Connection pool for database connections.
 */
public class BoneConnectionPool implements ConnectionPool {
	private Properties dbProps;

	static final Logger LOGGER = LoggerFactory.getLogger(BoneConnectionPool.class);
	BoneCP connectionPool = null;
	BoneCPConfig config = null;
	DataSourceConnectionSource bcpConnSource = null;
	Properties sqlConfig;
	int maxResources;
	
	public BoneConnectionPool(Properties dbProps,int maxResources) {
		this.dbProps = dbProps;
		this.maxResources = maxResources;
	}
	
	public BoneConnectionPool(String url, String username, String password, int maxResources) {
		this.maxResources = maxResources;
		Properties props = new Properties();
		
		props.setProperty("url", url);
		props.setProperty("password", password);
		props.setProperty("user", username);
		
		this.dbProps = props;
	} 
	
	public void startPool() throws Exception{
		Class.forName( "com.jolbox.bonecp.BoneCP" );
		Class.forName( "com.jolbox.bonecp.BoneCPConfig" );
		
		config = new BoneCPConfig();
		config.setUsername(dbProps.getProperty("user"));
		config.setPassword(dbProps.getProperty("password"));
		config.setJdbcUrl(dbProps.getProperty("url"));
	//	config.setCloseConnectionWatch(true); //DEBUG
		config.setStatementsCacheSize(1024);
		config.setDriverProperties(dbProps);
		
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(maxResources);
		config.setPartitionCount(1);
		connectionPool = new BoneCP(config); // setup the connection pool
		BoneCPDataSource bcpDataSource = new BoneCPDataSource(config);
		bcpConnSource = new DataSourceConnectionSource(bcpDataSource, dbProps.getProperty("url"));
	}

	public void stopPool(){
		LOGGER.info("Closing all database connections...");
		connectionPool.shutdown();
	}
	
	/**
	 * Gets a database connection.
	 * 
	 * @return a database connection
	 * @throws SQLException 
	 */
	public Connection getConnection() throws SQLException {
		return connectionPool.getConnection();
	}
	
	public DataSourceConnectionSource getConnectionSource() throws SQLException {
		return bcpConnSource;
	}

	public void returnConnection(Connection cn) {
		try {
			cn.close();
		} catch (SQLException e) {
			LOGGER.warn("Error trying to close connection: "+e.getMessage());
		}
	}
}
