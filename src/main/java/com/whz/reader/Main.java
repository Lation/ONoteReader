package com.whz.reader;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.whz.reader.util.I18N;
import com.whz.reader.view.ReaderGUI;

/**
 * Main class that starts the application by executing a new thread which runs
 * the ReaderGUI class to display the app while waiting for further user inputs
 * to be processed.
 * 
 * @author Timon Schwalbe
 */
public class Main {

	private static final Logger log = Logger.getLogger(Main.class.getName());

	private static final int TTM_INITIAL_DELAY = 300;
	private static final int TTM_DISMISS_DELAY = 60000;

	public static void main(String[] args) {
		log.info("Started application");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ToolTipManager.sharedInstance().setInitialDelay(TTM_INITIAL_DELAY);
				ToolTipManager.sharedInstance().setDismissDelay(TTM_DISMISS_DELAY);
				UIManager.put("swing.boldMetal", Boolean.FALSE);

				I18N.init();
				ReaderGUI.initAndShow();
			}
		});
	}

}
