package com.whz.reader.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.UIManager;

/**
 * I18N is the internationalization class that takes care of changing the
 * displayed language of the application. Any component of the GUI that displays
 * text will need to change its language passed on a given Locale. The locale
 * contains the region and langauge to change into. The needed languages are
 * located in '.properties' files that contain the key of the component and its
 * specific text in each language supported.
 * 
 * @author Timon Schwalbe
 */
public class I18N {

	private static final String I18N_FOLDER = "i18n/bundle";

	public static ResourceBundle resourceBundle;

	/**
	 * Initializes the default application language with the locale for American
	 * English.
	 */
	public static void init() {
		changeLocale(new Locale("en", "US"));
	}

	/**
	 * Changes the language of every displayable component inside a JFileChooser
	 * window based on a given Locale.
	 * 
	 * @param locale - The region and language to change into
	 */
	public static void changeLocale(Locale locale) {
		resourceBundle = ResourceBundle.getBundle(I18N_FOLDER, locale);

		UIManager.put("FileChooser.lookInLabelText", resourceBundle.getString("fileChooser.lookInLabelText"));
		UIManager.put("FileChooser.fileNameLabelText", resourceBundle.getString("fileChooser.fileNameLabelText"));
		UIManager.put("FileChooser.folderNameLabelText", resourceBundle.getString("fileChooser.folderNameLabelText"));
		UIManager.put("FileChooser.filesOfTypeLabelText", resourceBundle.getString("fileChooser.filesOfTypeLabelText"));
		UIManager.put("FileChooser.acceptAllFileFilterText",
				resourceBundle.getString("fileChooser.acceptAllFileFilterText"));
		UIManager.put("FileChooser.upFolderToolTipText", resourceBundle.getString("fileChooser.upFolderToolTipText"));
		UIManager.put("FileChooser.upFolderAccessibleName",
				resourceBundle.getString("fileChooser.upFolderAccessibleName"));
		UIManager.put("FileChooser.homeFolderToolTipText",
				resourceBundle.getString("fileChooser.homeFolderToolTipText"));
		UIManager.put("FileChooser.homeFolderAccessibleName",
				resourceBundle.getString("fileChooser.homeFolderAccessibleName"));
		UIManager.put("FileChooser.newFolderToolTipText", resourceBundle.getString("fileChooser.newFolderToolTipText"));
		UIManager.put("FileChooser.newFolderAccessibleName",
				resourceBundle.getString("fileChooser.newFolderAccessibleName"));
		UIManager.put("FileChooser.listViewButtonToolTipText",
				resourceBundle.getString("fileChooser.listViewButtonToolTipText"));
		UIManager.put("FileChooser.listViewButtonAccessibleName",
				resourceBundle.getString("fileChooser.listViewButtonAccessibleName"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText",
				resourceBundle.getString("fileChooser.detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.detailsViewButtonAccessibleName",
				resourceBundle.getString("fileChooser.detailsViewButtonAccessibleName"));
		UIManager.put("FileChooser.openButtonText", resourceBundle.getString("fileChooser.openButtonText"));
		UIManager.put("FileChooser.openButtonToolTipText",
				resourceBundle.getString("fileChooser.openButtonToolTipText"));
		UIManager.put("FileChooser.saveButtonText", resourceBundle.getString("fileChooser.saveButtonText"));
		UIManager.put("FileChooser.saveButtonToolTipText",
				resourceBundle.getString("fileChooser.saveButtonToolTipText"));
		UIManager.put("FileChooser.cancelButtonText", resourceBundle.getString("fileChooser.cancelButtonText"));
		UIManager.put("FileChooser.cancelButtonToolTipText",
				resourceBundle.getString("fileChooser.cancelButtonToolTipText"));
	}

}
