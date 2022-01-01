package com.whz.reader.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventModelTest {

	@Test
	public void testSimpleConstructor() {
		EventModel eventModelNull = null;
		EventModel eventModelNotNull = new EventModel();

		assertNull(eventModelNull);
		assertNotNull(eventModelNotNull);
	}

	@Test
	public void testGetterSetterToString() {
		EventModel eventModel = new EventModel();
		String name = "TestName";

		assertNotEquals(eventModel.getName(), name);
		eventModel.setName(name);
		assertEquals(eventModel.getName(), name);
		assertTrue(eventModel.toString().contains("name=" + name));
	}

}
