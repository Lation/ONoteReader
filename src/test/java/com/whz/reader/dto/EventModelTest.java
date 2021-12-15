package com.whz.reader.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class EventModelTest {

	@Test
	public void testSimpleConstructor() {
		EventModel eventModelNull = null;
		assertNull(eventModelNull);

		EventModel eventModelNotNull = new EventModel();
		assertNotNull(eventModelNotNull);
	}

	@Test
	public void testSimpleGetterSetter() {
		EventModel eventModel = new EventModel();
		String name = "TestName";
		eventModel.setName(name);

		assertEquals(eventModel.getName(), name);
	}

}
