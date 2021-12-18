package com.whz.reader.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * EventModel DTO which refers to an entire oNote project. It contains all the
 * other DTOs as it is the graphical representation of for example an entire
 * bounded context.
 * 
 * @author Timon Schwalbe
 */
public class EventModel {

	private UUID id;
	private String name;
	private String description;

	private List<Audience> audiences = new ArrayList<>();
	private List<Stream> streams = new ArrayList<>();

	private Map<UUID, Interface> interfaces = new HashMap<>();
	private Map<UUID, ReadModel> readModels = new HashMap<>();
	private Map<UUID, Event> events = new HashMap<>();
	private Map<UUID, Flow> flows = new HashMap<>();
	private Map<UUID, Placement> placements = new HashMap<>();
	private Map<UUID, Command> commands = new HashMap<>();
	private Map<UUID, Schema> schemas = new HashMap<>();

	public EventModel() {
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

	public List<Audience> getAudiences() {
		return audiences;
	}

	public void setAudiences(List<Audience> audiences) {
		this.audiences = audiences;
	}

	public List<Stream> getStreams() {
		return streams;
	}

	public void setStreams(List<Stream> streams) {
		this.streams = streams;
	}

	public Map<UUID, Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Map<UUID, Interface> interfaces) {
		this.interfaces = interfaces;
	}

	public Map<UUID, ReadModel> getReadModels() {
		return readModels;
	}

	public void setReadModels(Map<UUID, ReadModel> readModels) {
		this.readModels = readModels;
	}

	public Map<UUID, Event> getEvents() {
		return events;
	}

	public void setEvents(Map<UUID, Event> events) {
		this.events = events;
	}

	public Map<UUID, Flow> getFlows() {
		return flows;
	}

	public void setFlows(Map<UUID, Flow> flows) {
		this.flows = flows;
	}

	public Map<UUID, Placement> getPlacements() {
		return placements;
	}

	public void setPlacements(Map<UUID, Placement> placements) {
		this.placements = placements;
	}

	public Map<UUID, Command> getCommands() {
		return commands;
	}

	public void setCommands(Map<UUID, Command> commands) {
		this.commands = commands;
	}

	public Map<UUID, Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(Map<UUID, Schema> schemas) {
		this.schemas = schemas;
	}

	@Override
	public String toString() {
		return "EventModel [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
