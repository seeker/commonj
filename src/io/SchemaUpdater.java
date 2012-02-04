package io;

import java.util.Properties;

public class SchemaUpdater {
	public static void update(ConnectionPool connPool, Properties settings) throws SchemaUpdateException{
		try {
			MySQL sql = connPool.getResource();
			String s;
			 s = sql.getSetting(DBsettings.SchemaVersion);
			
			int versionFromRemote = Integer.parseInt(s);
			
			if(versionFromRemote <= 0 ){
				throw new SchemaUpdateException("Remote schema version is invalid: "+versionFromRemote);
			}
			
			s = settings.getProperty(DBsettings.SchemaVersion.toString());
			if(s == null){
				throw new SchemaUpdateException("Local schema version not found");
			}
			
			int versionFromLocal = Integer.parseInt(s);
			if(versionFromLocal <= 0 ){
				throw new SchemaUpdateException("Local schema version is invalid: "+versionFromLocal);
			}
			
			if(versionFromRemote > versionFromLocal){
				throw new SchemaUpdateException("Remote schema in newer than the local version");
			}
			
			// do the actual updates
			
			
		} catch (InterruptedException | ResourceCreationException | NumberFormatException e) {
			throw new SchemaUpdateException(e.getMessage());
		}
	}
}
