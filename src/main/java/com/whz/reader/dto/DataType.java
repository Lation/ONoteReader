package com.whz.reader.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DataType DTO needed as a utility class for the Schema DTO. This object cannot
 * exist by itself and can only be contained by a Schema DTO. It consists of
 * information regarding the type of schema and optionally its properties. The
 * properties field may define further information regarding the DataType like
 * min, default or max values. A DataType can also consist of itself and
 * multiple instances of itself at once in a recursive manner.
 * 
 * A DataType can have one nested DataType if it is a primitive type or multiple
 * nested DataTypes depending on its SchemaType.
 * 
 * If the SchemaType of the DataType is a primitive, it has no nested DataType.
 * 
 * If the DataType is of type 'vector, sequential, set, not, maybe' it has a
 * nested DataType 'listDataType' declaring its type of entries.
 * 
 * If the DataType is of type 'map_of' it has a nested DataTypes for the key
 * 'mapKeyDataType' and value 'mapValueDataType' declaring their types of
 * entries.
 * 
 * If the DataType is of type 'tuple, or, and' it has a List of nested DataTypes
 * declaring the multiple types of entries.
 * 
 * If the DataType is of type 'map' it has a Map of nested DataTypes declaring
 * their types of entries combined with a key name that identifies this
 * DataType.
 * 
 * If the DataType is of a special type 'enum, re, ref' it will had additional
 * fields with corresponding values filled in that would otherwise be null.
 * 
 * A declared Schema can only exist once but with multiple possible references
 * to different oNote notes while a DataType is just an abstract thing that is
 * contained by a Schema and can exist multiple times in different Schemas and
 * in different variations.
 * 
 * @author Timon Schwalbe
 */
public class DataType {

	private SchemaType schemaType;
	private String name; // generated name (usually inherited from the overlaying schema) used for the
							// nested DataType objects
	private Map<String, String> properties; // many default options like 'min, max, default, ...'

	private DataType listDataType; // for 'vector', 'sequential', 'set', 'not', and 'maybe'
	private DataType mapKeyDataType; // for 'map_of' key
	private DataType mapValueDataType; // for 'map_of' value
	private List<DataType> dataTypeTuple; // for 'tuple', 'or', and 'and'
	private Map<String, DataType> objectDataType; // for 'map' <- represents a generic object

	private List<String> enumList; // if the DataType is an 'ENUM' it contains a premade list of at least one
									// symbol
	private String regex; // if the DataType is a 'RE' it contains a regex
	private UUID reference; // if the DataType is a "REF" it contains the uuid of the referenced Schema

	/**
	 * List of all schema types declared by oNote. Each represents either a
	 * primitive type, a collection or a special schema type like enums or regular
	 * expressions.
	 * 
	 * Notable types are: 'RE' = String matching Regular Expression, 'MAP_OF' =
	 * ordinary Map, 'MAP' = generic Object, 'REF' = References to another schema,
	 * 'SEQUENTIAL' = any sequence
	 */
	public enum SchemaType {
		// Primitives
		STRING, INT, DOUBLE, BOOLEAN, SYMBOL, QUALIFIED_SYMBOL, KEYWORD, QUALIFIED_KEYWORD, UUID, NIL,
		// Collections
		MAP, MAP_OF, TUPLE, VECTOR, SEQUENTIAL, SET,
		// Special Schemas
		ENUM, RE, REF, ANY, OR, AND, NOT, MAYBE
	}

	public DataType() {
	}

	public SchemaType getSchemaType() {
		return schemaType;
	}

	public void setSchemaType(SchemaType schemaType) {
		this.schemaType = schemaType;
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

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public DataType getListDataType() {
		return listDataType;
	}

	public void setListDataType(DataType listDataType) {
		this.listDataType = listDataType;
	}

	public DataType getMapKeyDataType() {
		return mapKeyDataType;
	}

	public void setMapKeyDataType(DataType mapKeyDataType) {
		this.mapKeyDataType = mapKeyDataType;
	}

	public DataType getMapValueDataType() {
		return mapValueDataType;
	}

	public void setMapValueDataType(DataType mapValueDataType) {
		this.mapValueDataType = mapValueDataType;
	}

	public List<DataType> getDataTypeTuple() {
		return dataTypeTuple;
	}

	public void setDataTypeTuple(List<DataType> dataTypeTuple) {
		this.dataTypeTuple = dataTypeTuple;
	}

	public Map<String, DataType> getObjectDataType() {
		return objectDataType;
	}

	public void setObjectDataType(Map<String, DataType> objectDataType) {
		this.objectDataType = objectDataType;
	}

	public List<String> getEnumList() {
		return enumList;
	}

	public void setEnumList(List<String> enumList) {
		this.enumList = enumList;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public UUID getReference() {
		return reference;
	}

	public void setReference(UUID reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		String toString = "DataType [schemaType=" + schemaType + ", name=" + name + ", properties=" + properties;

		if (this.schemaType == SchemaType.VECTOR || this.schemaType == SchemaType.SEQUENTIAL
				|| this.schemaType == SchemaType.SET || this.schemaType == SchemaType.NOT
				|| this.schemaType == SchemaType.MAYBE)
			toString += ", listDataType=" + listDataType;
		else if (this.schemaType == SchemaType.MAP_OF)
			toString += ", mapKeyDataType=" + mapKeyDataType + ", mapValueDataType=" + mapValueDataType;
		else if (this.schemaType == SchemaType.TUPLE || this.schemaType == SchemaType.OR
				|| this.schemaType == SchemaType.AND)
			toString += ", dataTypeTuple=" + dataTypeTuple;
		else if (this.schemaType == SchemaType.MAP)
			toString += ", objectDataType=" + objectDataType;
		else if (this.schemaType == SchemaType.ENUM)
			toString += ", enumList=" + enumList;
		else if (this.schemaType == SchemaType.RE)
			toString += ", regex=" + regex;
		else if (this.schemaType == SchemaType.REF)
			toString += ", reference=" + reference;

		return toString + "]";
	}

}
