package com.whz.reader.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ReaderControllerTest {

	@Test
	public void testControllerBinding() {
		ReaderController rc = new ReaderController();
		assertThrows(NullPointerException.class, () -> rc.generateJavaCode("test.json", 1, "D:/", 0, "main"));
	}

}
