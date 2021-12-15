package com.whz.reader.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class I18NTest {

	@Test
	@Order(1)
	public void testI18N_Initialized() {
		I18N.init();
		assertNotEquals(I18N.resourceBundle, null);
	}

	@Test
	@Order(2)
	public void testDefaultLocale() {
		assertEquals(I18N.resourceBundle.getLocale(), new Locale("en", "US"));
	}

	@Test
	@Order(3)
	public void testChangeLanguage() {
		Locale germany = new Locale("de", "DE");
		I18N.changeLocale(germany);
		assertEquals(I18N.resourceBundle.getLocale(), new Locale("de", "DE"));
	}

	@Test
	@Order(4)
	public void testStringsOfDifferentLocalesAreNotSame() {
		String localizedKey = "general.directories";
		Locale germany = new Locale("de", "DE");
		Locale usa = new Locale("en", "US");

		I18N.changeLocale(germany);
		String germanString = I18N.resourceBundle.getString(localizedKey);

		I18N.changeLocale(usa);
		String americanString = I18N.resourceBundle.getString(localizedKey);

		assertTrue(!germanString.equals(americanString));
	}

	@Test
	@Order(5)
	public void testChangeToUnknownLanguageShouldChangeToDefault() {
		String localizedKey = "general.directories";
		Locale germany = new Locale("de", "DE");
		Locale russia = new Locale("ru", "RU");

		I18N.changeLocale(germany);
		String knownGermanString = I18N.resourceBundle.getString(localizedKey);

		I18N.changeLocale(russia);
		String unknownRussianString = I18N.resourceBundle.getString(localizedKey);

		assertTrue(unknownRussianString.equals(knownGermanString));
	}

}
