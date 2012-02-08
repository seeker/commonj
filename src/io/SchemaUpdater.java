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
