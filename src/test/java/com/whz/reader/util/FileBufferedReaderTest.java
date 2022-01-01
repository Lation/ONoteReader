package com.whz.reader.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileBufferedReaderTest {

	@Test
	public void testCanAccessTextFile() {
		Assertions.assertDoesNotThrow(() -> FileBufferedReader.readFile("files/JavaKeywords.txt"));
	}

}
