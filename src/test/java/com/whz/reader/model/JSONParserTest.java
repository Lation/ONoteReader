package com.whz.reader.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class JSONParserTest {

	@Test
	public void testCanParseJson() {
		boolean parsable = JSONParser.parseJson("apprenticeship_system_example.0.1.0-beta.json");
		assertTrue(parsable);
	}

	@Test
	public void testCorrectlyParsesJson() {
		JSONParser.parseJson("apprenticeship_system_example.0.1.0-beta.json");

		assertTrue(JSONParser.eventModel.getName().equals("Apprenticeship System"));
		assertTrue(JSONParser.eventModel.getFormattedName().equals("ApprenticeshipSystem"));
		assertTrue(JSONParser.eventModel.getDescription().contains("administrative system"));
		assertTrue(JSONParser.eventModel.getStreams().size() == 3);
		assertTrue(JSONParser.eventModel.getAudiences().size() == 3);
		assertTrue(JSONParser.eventModel.getInterfaces().size() == 5);
		assertTrue(JSONParser.eventModel.getCommands().size() == 11);
		assertTrue(JSONParser.eventModel.getReadModels().size() == 5);
		assertTrue(JSONParser.eventModel.getEvents().size() == 10);
		assertTrue(JSONParser.eventModel.getFlows().size() == 33);
		assertTrue(JSONParser.eventModel.getPlacements().size() == 31);
		assertTrue(JSONParser.eventModel.getSchemas().size() == 7);
	}

}
