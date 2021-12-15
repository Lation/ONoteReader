package com.whz.reader.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageIconReaderTest {

	@Test
	public void testCannotAccessFile() {
		Assertions.assertThrows(NullPointerException.class, () -> ImageIconReader.readImage(null));
	}

	@Test
	public void testCanAccessFile() {
		Assertions.assertDoesNotThrow(() -> ImageIconReader.readImage("images/folder_icon.png"));
	}

}
