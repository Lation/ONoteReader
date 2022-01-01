package com.whz.reader.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageIconReaderTest {

	@Test
	public void testCanAccessProjectIcon() {
		Assertions.assertDoesNotThrow(() -> ImageIconReader.readImage("images/ONoteReader_icon.png"));
	}

	@Test
	public void testCanAccessFolderIcon() {
		Assertions.assertDoesNotThrow(() -> ImageIconReader.readImage("images/folder_icon.png"));
	}

}
