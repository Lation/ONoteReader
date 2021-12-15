package com.whz.reader.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Interface DTO which refers to an oNote Interface. A interface is the
 * visual/graphical component the user interacts with. These usually trigger
 * commands, for example via button presses from the user. There are different
 * type of interfaces defined in the enum InterfaceType. If the InterfaceType is
 * 'REST', an additional Map called 'elements' is needed to include further
 * informations about the REST call.
 * 
 * @author Timon Schwalbe
 */
public class Interface {

	private UUID id;
	private String name;
	private String description;
	private InterfaceType type;
	private String figmaURL;
	private Map<UUID, Element> elements;

	/**
	 * List of all interface types declared by oNote.
	 */
	public enum InterfaceType {
		BLANK, REST, HTML, JOB, FIGMA
	}

	public Interface() {
		elements = new HashMap<>();
	}

	public Interface(UUID id, String name, String description, InterfaceType type, String figmaURL,
			Map<UUID, Element> elements) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.figmaURL = figmaURL;
		this.elements = elements;
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

	public InterfaceType getType() {
		return type;
	}

	public void setType(InterfaceType type) {
		this.type = type;
	}

	public String getFigmaURL() {
		return figmaURL;
	}

	public void setFigmaURL(String figmaURL) {
		this.figmaURL = figmaURL;
	}

	public Map<UUID, Element> getElements() {
		return elements;
	}

	public void setElements(Map<UUID, Element> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		return "Interface [id=" + id + ", name=" + name + ", description=" + description + ", type=" + type + "]";
	}

}
