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
package com.github.dozedoff.commonj.settings;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.file.FileUtil;

public abstract class AbstractSettings {
	Logger logger = LoggerFactory.getLogger(AbstractSettings.class);
	ISettingsValidator validator = null;
	Properties properties = new Properties();
	
	public AbstractSettings(ISettingsValidator validator) {
		this.validator = validator;
	}
	
	private Properties loadPropertiesFromFile(Path filepath) {
		try {
			if (Files.exists(filepath)) {
				FileReader fr = new FileReader(filepath.toFile());
				properties.load(fr);
				fr.close();
				logger.info("Loaded property file {} with {} entries", filepath, properties.size());
			} else {
				logger.error("Could not find {}", filepath);
			}
		} catch (IOException e) {
			logger.error("Failed to load {}", filepath, e);
		}

		return properties;
	}
	
	/**
	 * Load a property file from the working directory.
	 * @param propertyFilename of the propery file to load
	 * @return 
	 */
	protected final Properties loadPropertiesFromFile(String propertyFilename){
		Path workingDir = FileUtil.WorkingDir().toPath();
		Path propertyfileAbsolute = workingDir.resolve(propertyFilename);
		return loadPropertiesFromFile(propertyfileAbsolute);
	}
	
	/**
	 * Load a property file from a sub directory in the workingdirectory.
	 * config/ or data/ for example.
	 * @param subdirectory subdirectory path to the file
	 * @param propertyFilename of the propery file to load
	 * @return 
	 */
	protected final Properties loadPropertiesFromFile(Path subdirectory, String propertyFilename){
		Path workingDir = FileUtil.WorkingDir().toPath();
		Path propertyfileAbsolute = workingDir.resolve(subdirectory).resolve(propertyFilename);
		return loadPropertiesFromFile(propertyfileAbsolute);
	}
	
	/**
	 * Load a property file from the given path.
	 * @param propertyFilename of the propery file to load
	 * @return 
	 */
	protected final Properties loadPropertiesFromFileAbsolute(Path absolute){
		return loadPropertiesFromFile(absolute);
	}

	/**
	 * This method validates the settings.
	 * @return true if all settings are valid.
	 */
	public boolean validateSettings(){
		if(validator == null){
		return false;
		}else{
			return validator.validate(properties);
		}
	}
}