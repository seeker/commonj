package io;

import java.util.Properties;

public class SchemaUpdater {
	public static void update(MySQL sql, Properties settings) throws SchemaUpdateException{
		try {
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
					// update from version 1 to 2
					if(! sql.batchExecute(UPDATE_1_TO_2))
						throw new SchemaUpdateException("Batch command UPDATE_1_TO_2 failed");
				default:
			}
		} catch (NumberFormatException  e) {
			throw new SchemaUpdateException(e.getMessage());
		} finally {
			sql.disconnect();
		}
	}
	
	private final static String[] UPDATE_1_TO_2 ={
			"ALTER TABLE `archive` CHANGE COLUMN `hash` `id` VARCHAR(64) NOT NULL COLLATE 'ascii_general_ci' FIRST",
			"ALTER TABLE `hash` CHANGE COLUMN `hash` `id` VARCHAR(64) NOT NULL COLLATE 'ascii_general_ci' FIRST",
			"ALTER TABLE `block` CHANGE COLUMN `hash` `id` VARCHAR(64) NOT NULL COLLATE 'ascii_general_ci' FIRST",
			"ALTER TABLE `dnw` CHANGE COLUMN `hash` `id` VARCHAR(64) NOT NULL COLLATE 'ascii_general_ci' FIRST",
			"UPDATE settings SET param='2' WHERE name ='SchemaVersion'"
	};
}
