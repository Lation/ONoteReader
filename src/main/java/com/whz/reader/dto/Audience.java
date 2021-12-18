package com.whz.reader.dto;

import java.util.UUID;

/**
 * Audience DTO which refers to an oNote Audience. An audience is a lane in
 * which interfaces reside.
 * 
 * @author Timon Schwalbe
 */
public class Audience {

	private UUID id;
	private String name;

	public Audience() {
	}

	public Audience(UUID id, String name) {
		this.id = id;
		this.name = name;
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

	@Override
	public String toString() {
		return "Audience [id=" + id + ", name=" + name + "]";
	}

}
