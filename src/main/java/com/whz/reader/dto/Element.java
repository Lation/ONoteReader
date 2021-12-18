package com.whz.reader.dto;

import java.util.UUID;

/**
 * Element DTO needed as a utility class for the Interface DTO. This object
 * cannot exist by itself and can only be contained by an Interface DTO. It
 * consists of basic information regarding the type of REST call and the path
 * name of the corresponding resource represented in the user interface.
 * 
 * @author Timon Schwalbe
 */
public class Element {

	private UUID id;
	private ElementType elementType;
	private String name;

	/**
	 * List of all element types declared by oNote. Each represents a specific REST
	 * call.
	 */
	public enum ElementType {
		CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE
	}

	public Element() {
	}

	public Element(UUID id, ElementType elementType, String name) {
		this.id = id;
		this.elementType = elementType;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
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

	@Override
	public String toString() {
		return "Element [id=" + id + ", elementType=" + elementType + ", name=" + name + "]";
	}

}
