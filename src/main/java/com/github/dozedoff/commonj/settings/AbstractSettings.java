/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.settings;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.file.FileUtil;

public abstract class AbstractSettings {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractSettings.class);
	protected ISettingsValidator validator = null;
	protected Properties properties = new Properties();
	protected Path settingsPath;

	public AbstractSettings(ISettingsValidator validator) {
		this.validator = validator;
	}

	private void loadPropertiesFromFile(Path filepath) {
		try {
			if (Files.exists(filepath)) {
				settingsPath = filepath;
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
	}

	/**
	 * Load a property file from the working directory.
	 * 
	 * @param propertyFilename
	 *            of the propery file to load
	 * @return
	 */
	public final void loadPropertiesFromFile(String propertyFilename) {
		Path workingDir = FileUtil.WorkingDir().toPath();
		Path propertyfileAbsolute = workingDir.resolve(propertyFilename);
		loadPropertiesFromFile(propertyfileAbsolute);
	}

	/**
	 * Load a property file from a sub directory in the workingdirectory. config/ or data/ for example.
	 * 
	 * @param subdirectory
	 *            subdirectory path to the file
	 * @param propertyFilename
	 *            of the propery file to load
	 * @return
	 */
	public final void loadPropertiesFromFile(Path subdirectory, String propertyFilename) {
		Path workingDir = FileUtil.WorkingDir().toPath();
		Path propertyfileAbsolute = workingDir.resolve(subdirectory).resolve(propertyFilename);
		loadPropertiesFromFile(propertyfileAbsolute);
	}

	/**
	 * Load a property file from the given path.
	 * 
	 * @param propertyFilename
	 *            of the propery file to load
	 * @return
	 */
	public final void loadPropertiesFromFileAbsolute(Path absolute) {
		loadPropertiesFromFile(absolute);
	}

	/**
	 * This method validates the settings.
	 * 
	 * @return true if all settings are valid.
	 */
	public boolean validateSettings() {
		if (validator == null) {
			throw new UnsupportedOperationException("No Validator specified");
		} else {
			return validator.validate(this);
		}
	}

	public void saveSettings() {
		OutputStream os = null;
		try {
			os = Files.newOutputStream(settingsPath);
			properties.store(os, "Settings for SimilarImage");
		} catch (IOException e) {
			logger.warn("Failed to save settings - {}", e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				logger.warn("Failed to close output stream", e);
			}
		}
	}
}
