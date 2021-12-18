package com.whz.reader.dto;

import java.util.UUID;

/**
 * Schema DTO which refers to an oNote Schema. A schema describes the structure
 * of a command, read model or event and what fields, values and properties it
 * consists of. A Schema can be as simple as a primitive or as large as an
 * object. It is used to give a detailed description of the corresponding note.
 * A single Schema can be used multiple times by different notes.
 * 
 * A Schema contains a uuid for identification, some basic information and a
 * DataType which describes its type of schema and might contain further nested
 * DataTypes. A DataType can be thought of as a generic data type like
 * primitives, lists, maps or objects.
 * 
 * @author Timon Schwalbe
 */
public class Schema {

	private UUID id;
	private String name;
	private String namespace;
	private String description;
	private DataType dataType;

	public Schema() {
	}

	public Schema(UUID id, String name, String namespace, String description, DataType dataType) {
		this.id = id;
		this.name = name;
		this.namespace = namespace;
		this.description = description;
		this.dataType = dataType;
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "Schema [id=" + id + ", name=" + name + ", namespace=" + namespace + ", description=" + description
				+ ", dataType=" + dataType + "]";
	}

}
