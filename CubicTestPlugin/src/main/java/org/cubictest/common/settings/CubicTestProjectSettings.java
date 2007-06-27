/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.settings;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.eclipse.core.resources.IProject;

/**
 * Provies access to properties for CubicTest projects.
 * 
 * @author Christian Schwarz
 * 
 */
public class CubicTestProjectSettings {

	/** The property file to load */
	static final String FILE_NAME = "test-project.properties";

	/** The test project's properties */
	Properties properties;

	
	public CubicTestProjectSettings(IProject project) {
		File projectFolder = project.getLocation().toFile();
		File propsFile = getPropsFile(projectFolder);
		loadProperties(propsFile);
	}

	public CubicTestProjectSettings(File projectFolder) {
		File propsFile = getPropsFile(projectFolder);
		loadProperties(propsFile);
	}

	/**
	 * Get boolean property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public Boolean getBoolean(String prefix, String property) {
		Object prop = properties.get(prefix + "." + property);
		if (prop != null) {
			return ((String) prop).equalsIgnoreCase("true");
		}
		return null;
	}
	
	/**
	 * Get boolean property from test-project.properties.
	 * 
	 * @param exporterId
	 * @param property
	 * @return
	 */
	public Integer getInteger(String prefix, String property) {
		Object prop = properties.get(prefix + "." + property);
		if (prop != null) {
			return Integer.parseInt((String) prop);
		}
		return null;
	}
	

	/**
	 * Looks up a resource named test-project.properties in the classpath.
	 * Caches the result.
	 */
	private void loadProperties(File propsFile) {
		InputStream in = null;
		
		try {
			in = FileUtils.openInputStream(propsFile);
			properties = new Properties();
			properties.load(in);
		} 
		catch (Exception e) {
			ErrorHandler.logAndRethrow(e, "Error loading properties");
		} 
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e2) {
					Logger.warn("Error closing input stream from property file.");
				}
			}
		}
	}

	private File getPropsFile(File folder) {
		File[] files = folder.listFiles(getFilenameFilter());
		if (files == null || files.length == 0) {
			throw new CubicException("Did not find " + FILE_NAME);
		}
		return files[0];
	}

	private FilenameFilter getFilenameFilter() {
		return new FilenameFilter() {
			public boolean accept(File dir, String fileName) {
				if (fileName.equals(FILE_NAME)) {
					return true;
				}
				return false;
			}
		};
	}
}