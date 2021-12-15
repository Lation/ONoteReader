package com.whz.reader.dto;

import java.util.UUID;

/**
 * Command DTO which refers to an oNote Command. A command is a note which
 * represents a user command that has to be processed and will possibly result
 * in a/or multiple event(s).
 * 
 * A Command contains a schemaId which refers to a specific Schema DTO that
 * further describes the structure of the Command with its fields and value
 * types.
 * 
 * @author Timon Schwalbe
 */
public class Command {

	private UUID id;
	private String name;
	private String description;
	private UUID schemaId;

	public Command() {
	}

	public Command(UUID id, String name, String description, UUID schemaId) {
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
		return "Command [id=" + id + ", name=" + name + ", description=" + description + ", schemaId=" + schemaId + "]";
	}

}
