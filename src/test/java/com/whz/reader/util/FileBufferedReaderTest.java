package com.whz.reader.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileBufferedReaderTest {

	@Test
	public void testCannotAccessFile() {
		Assertions.assertThrows(NullPointerException.class, () -> FileBufferedReader.readFile(null));
	}

	@Test
	public void testWrongFilePath() {
		Assertions.assertThrows(NullPointerException.class, () -> FileBufferedReader.readFile("ab\\c!.."));
	}

	@Test
	public void testCanAccessFile() {
		Assertions.assertDoesNotThrow(() -> FileBufferedReader.readFile("files/JavaKeywords.txt"));
	}

}
