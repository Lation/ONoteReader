package com.whz.reader.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InputValidatorTest {

	@BeforeAll
	public static void initI18N() {
		I18N.init();
	}

	@Test
	public void testEmptyUserInputs() {
		String warningMessage1 = InputValidator.validateUserInput(0, "", "");
		String warningMessage2 = InputValidator.validateUserInput(0, " ", " ");
		String warningMessage3 = InputValidator.validateUserInput(0, "D:/", "");
		String warningMessage4 = InputValidator.validateUserInput(0, "", "test.json");
		assertTrue(!warningMessage1.isEmpty());
		assertTrue(!warningMessage2.isEmpty());
		assertTrue(!warningMessage3.isEmpty());
		assertTrue(!warningMessage4.isEmpty());
	}

	@Test
	public void testWrongUserInputs() {
		String warningMessage = InputValidator.validateUserInput(0, "D:/", "test.txt");
		assertTrue(!warningMessage.isEmpty());
	}

	@Test
	public void testCorrectUserInputs() {
		String warningMessage = InputValidator.validateUserInput(0, "D:/", "test.json");
		assertTrue(warningMessage.isEmpty());
	}

	@Test
	public void testWrongNamespace() {
		String warningMessage1 = InputValidator.validateNamespace("");
		String warningMessage2 = InputValidator.validateNamespace(" ");
		String warningMessage3 = InputValidator.validateNamespace(".");
		String warningMessage4 = InputValidator.validateNamespace("!");
		String warningMessage5 = InputValidator.validateNamespace("1");
		String warningMessage6 = InputValidator.validateNamespace("¯");
		String warningMessage7 = InputValidator.validateNamespace("..");
		String warningMessage8 = InputValidator.validateNamespace(".a");
		String warningMessage9 = InputValidator.validateNamespace("a.");
		String warningMessage10 = InputValidator.validateNamespace("1a");
		String warningMessage11 = InputValidator.validateNamespace("test.!");
		String warningMessage12 = InputValidator.validateNamespace("test..hi");
		String warningMessage13 = InputValidator.validateNamespace("default");
		String warningMessage14 = InputValidator.validateNamespace("boolean");
		String warningMessage15 = InputValidator.validateNamespace("true");
		assertTrue(!warningMessage1.isEmpty());
		assertTrue(!warningMessage2.isEmpty());
		assertTrue(!warningMessage3.isEmpty());
		assertTrue(!warningMessage4.isEmpty());
		assertTrue(!warningMessage5.isEmpty());
		assertTrue(!warningMessage6.isEmpty());
		assertTrue(!warningMessage7.isEmpty());
		assertTrue(!warningMessage8.isEmpty());
		assertTrue(!warningMessage9.isEmpty());
		assertTrue(!warningMessage10.isEmpty());
		assertTrue(!warningMessage11.isEmpty());
		assertTrue(!warningMessage12.isEmpty());
		assertTrue(!warningMessage13.isEmpty());
		assertTrue(!warningMessage14.isEmpty());
		assertTrue(!warningMessage15.isEmpty());
	}

	@Test
	public void testCorrectNamespace() {
		String warningMessage1 = InputValidator.validateNamespace("a");
		String warningMessage3 = InputValidator.validateNamespace("_");
		String warningMessage4 = InputValidator.validateNamespace("_a");
		String warningMessage2 = InputValidator.validateNamespace("a123");
		String warningMessage5 = InputValidator.validateNamespace("Hi");
		String warningMessage6 = InputValidator.validateNamespace("a.b");
		String warningMessage7 = InputValidator.validateNamespace("a.b.c");
		String warningMessage8 = InputValidator.validateNamespace("a_b_c.d");
		String warningMessage9 = InputValidator.validateNamespace("àáÜüÄäÖö");
		String warningMessage10 = InputValidator.validateNamespace("€$¥");
		String warningMessage11 = InputValidator
				.validateNamespace("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.b");
		String warningMessage12 = InputValidator.validateNamespace("你好我的名字是");
		assertTrue(warningMessage1.isEmpty());
		assertTrue(warningMessage2.isEmpty());
		assertTrue(warningMessage3.isEmpty());
		assertTrue(warningMessage4.isEmpty());
		assertTrue(warningMessage5.isEmpty());
		assertTrue(warningMessage6.isEmpty());
		assertTrue(warningMessage7.isEmpty());
		assertTrue(warningMessage8.isEmpty());
		assertTrue(warningMessage9.isEmpty());
		assertTrue(warningMessage10.isEmpty());
		assertTrue(warningMessage11.isEmpty());
		assertTrue(warningMessage12.isEmpty());
	}

}
