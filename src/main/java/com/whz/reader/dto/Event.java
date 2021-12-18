package com.whz.reader.dto;

import java.util.UUID;

/**
 * Event DTO which refers to an oNote Event. An event is a note which is usually
 * triggered by a command and contains final, immutable and time-dependent
 * information about an incident that happened in the past. A single event
 * cannot be referred to as an actual object but rather a change that happened
 * to an object.
 * 
 * An Event contains a schemaId which refers to a specific Schema DTO that
 * further describes the structure of the Event with its fields and value types.
 * 
 * @author Timon Schwalbe
 */
public class Event {

	private UUID id;
	private String name;
	private String description;
	private UUID schemaId;

	public Event() {
	}

	public Event(UUID id, String name, String description, UUID schemaId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.schemaId = schemaId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Strips leading and trailing whitespaces off the name and removes spaces
	 * within the name. This is a handy utility class for the Java file creation
	 * where spaces are not allowed.
	 * 
	 * @return String - Gets the formatted version of the 'name' field
	 */
	public String getFormattedName() {
		return name.strip().replace(" ", "");
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UUID getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(UUID schemaId) {
		this.schemaId = schemaId;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", name=" + name + ", description=" + description + ", schemaId=" + schemaId + "]";
	}

}
