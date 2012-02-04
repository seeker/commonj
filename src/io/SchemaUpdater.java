package io;

import java.util.Properties;

public class SchemaUpdater {
	private static String UPDATE_1_TO_2 = "put update code here";
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
			// note: missing breaks are intentional, to create cascade
			switch(versionFromRemote){
				case 1:
					// initial version, do nothing
				case 2:
					// update from version 1 to 2
					sql.sendStatement(UPDATE_1_TO_2);
				default:
			}
		} catch (InterruptedException | ResourceCreationException | NumberFormatException e) {
			throw new SchemaUpdateException(e.getMessage());
		}
	}
}
