package com.whz.reader.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Simple directory filter class implementing and overriding the FileFilter
 * class to look only for directories (folders) when searching with a
 * JFileChooser.
 * 
 * @author Timon Schwalbe
 */
public class DirectoryFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}

	@Override
	public String getDescription() {
		return I18N.resourceBundle.getString("general.directories");
	}

}