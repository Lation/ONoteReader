package com.whz.reader.view;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.whz.reader.util.I18N;

public class ReaderGUITest {

	@Test
	public void testCreateGUI() {
		I18N.init();
		assertDoesNotThrow(() -> ReaderGUI.initAndShow());
	}

}
