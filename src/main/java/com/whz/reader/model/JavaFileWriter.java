package com.whz.reader.model;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.whz.reader.dto.DataType;
import com.whz.reader.dto.DataType.SchemaType;

/**
 * JavaFileWriter class parsing and generating all the Java classes provided by
 * the oNote event model. Commands, Events, Read Model-Entities, Schema-Entities
 * and Enums are generated this way. These may be mutable or immutable objects
 * and depending on their type they will consist of member variables, no-args
 * and all-args constructors, getters, setters and a basic toString method.
 * 
 * Nested Enums will reside inside the overlying object while Schema root enums
 * may be a separate object.
 * 
 * Any Schema whose DataType as well as any nested DataType is of SchemaType
 * 'MAP' (careful: MAP = Java-Object, MAP_OF = Java-Map) may be a separate
 * object as well.
 * 
 * Lastly, any object referenced through the SchemaType 'REF' may also be a
 * separate object even if it is just a primitive type since there had to be a
 * reason as to why it was distinctively declared as a reference.
 * 
 * @author Timon Schwalbe
 */
public class JavaFileWriter {

	private static final Logger log = Logger.getLogger(JavaFileWriter.class.getName());

	/**
	 * Writes the Entity classes into the project source folder at the given
	 * namespace location. This method is used for both Schema Entities and Read
	 * Model Entities since they both share a very similar object structure. The
	 * main difference is, that a Read Model would need an identifier but since
	 * oNote does not yet provide the option to distinctively select a member as the
	 * identifier, both Entities are structurally very similar. Schema Entities are
	 * just objects that can exist any number of times and provided their members
	 * are exactly the same, one object may be equal to another object.
	 * 
	 * Schema Entities and Read Model Entities unlike Commands and Events are both
	 * mutable. Therefore, they are not final and consist of all their member
	 * fields, a no-args constructor, an all-args constructor, getters, setters and
	 * a basic toString method.
	 * 
	 * @param projectSourcePath - Path of the project source folder
	 * @param namespace         - Namespace of the Entity class
	 * @param dataType          - The DataType containing all the necessary
	 *                          information of the Entity
	 * @param className         - The class name of the Java class
	 * @param description       - The description of the Java class
	 */
	public static void writeEntity(String projectSourcePath, String namespace, DataType dataType, String className,
			String description) {
		Map<String, DataType> objectDataType = new HashMap<>();
		if (dataType != null) {
			if (dataType.getSchemaType().equals(SchemaType.MAP)) {
				if (dataType.getObjectDataType() != null) {
					objectDataType.putAll(dataType.getObjectDataType());
				}
			} else {
				objectDataType.put(dataType.getName(), dataType);
			}
		}

		Map<String, String> fields = new HashMap<>();
		String capitalizedClassName;
		if (className != null) {
			String tempClassName = className.strip().replace(" ", "");
			capitalizedClassName = tempClassName.substring(0, 1).toUpperCase() + tempClassName.substring(1);
		} else {
			capitalizedClassName = dataType.getFormattedName().substring(0, 1).toUpperCase()
					+ dataType.getFormattedName().substring(1);
		}

		String filePath = projectSourcePath + "/" + namespace.replace(".", "/") + "/" + capitalizedClassName + ".java";

		try (BufferedWriter bw = new BufferedWriter(Files.newBufferedWriter(Paths.get(filePath)))) {
			// namespace
			bw.write("package " + namespace + ";");
			bw.newLine();
			bw.newLine();

			// imports
			// - external
			boolean importsNeeded = false;
			Set<String> uniqueImports = new HashSet<>();
			for (var entry : objectDataType.entrySet()) {
				DataType tempDataType = entry.getValue();
				uniqueImports.addAll(parseImports(tempDataType));
			}
			if (!uniqueImports.isEmpty()) {
				List<String> sortedImports = new ArrayList<>(uniqueImports);
				Collections.sort(sortedImports);
				for (String importString : sortedImports) {
					bw.write("import java.util." + importString + ";");
					bw.newLine();
				}
				importsNeeded = true;
			}
			if (importsNeeded) {
				bw.newLine();
			}
			// - internal (only needed if not same namespace)
			if (!namespace.substring(namespace.lastIndexOf(".") + 1, namespace.length()).equals("schemas")) {
				boolean schemaImportsNeeded = false;
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();
					Set<String> schemaImports = parseSchemaImports(tempDataType);

					if (!schemaImports.isEmpty()) {
						List<String> sortedSchemaImports = new ArrayList<>(schemaImports);
						Collections.sort(sortedSchemaImports);
						for (String importString : sortedSchemaImports) {
							bw.write("import " + namespace.substring(0, namespace.lastIndexOf(".")) + ".schemas."
									+ importString + ";");
							bw.newLine();
						}
						schemaImportsNeeded = true;
					}
				}
				if (schemaImportsNeeded) {
					bw.newLine();
				}
			}

			// javadoc
			if (description != null || (dataType != null && dataType.getProperties() != null
					&& dataType.getSchemaType().equals(SchemaType.MAP))) {
				bw.write("/**");
				bw.newLine();
				if (description != null) {
					bw.write(" * " + description);
					bw.newLine();
				}
				if (dataType != null && dataType.getProperties() != null) {
					if (dataType.getSchemaType().equals(SchemaType.MAP)) {
						if (description != null) {
							bw.write(" * ");
							bw.newLine();
						}
						for (var propertyEntry : dataType.getProperties().entrySet()) {
							bw.write(" * Property: " + propertyEntry.getKey() + " - " + propertyEntry.getValue());
							bw.newLine();
						}
					}
				}
				bw.write(" */");
				bw.newLine();
			}

			// class definition (not 'final' because entity)
			bw.write("public class " + capitalizedClassName + " {");
			bw.newLine();
			bw.newLine();

			// field definition
			if (!objectDataType.isEmpty()) {
				// regex constants
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					Map<String, String> regexMap = parseRegexs(tempDataType);
					if (!regexMap.isEmpty()) {
						for (var regexEntry : regexMap.entrySet()) {
							bw.write("\t" + "public static final String " + entry.getKey().toUpperCase() + "_REGEX"
									+ " = \"" + regexEntry.getValue().replace("\\", "\\\\") + "\";");
							bw.newLine();
						}
						bw.newLine();
					}
				}

				// fields
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					String field = parseTypeArgument(tempDataType, false);
					if (!field.isBlank()) {
						fields.put(entry.getKey().strip().replace(" ", ""), field);

						bw.write("\t" + "private ");
						bw.write(field);
						bw.write(" " + entry.getKey().strip().replace(" ", "") + ";");

						if (tempDataType.getProperties() != null) {
							bw.write(" //");
							for (var tempPropertyEntry : tempDataType.getProperties().entrySet()) {
								bw.write(" | " + tempPropertyEntry.getKey() + " - " + tempPropertyEntry.getValue());
							}
						}
						bw.newLine();
					}
				}
				bw.newLine();

				// nested enum definition
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					Map<String, List<String>> enumMap = parseEnums(tempDataType);
					if (!enumMap.isEmpty()) {
						for (var enumEntry : enumMap.entrySet()) {
							String enumName = enumEntry.getKey().substring(0, 1).toUpperCase()
									+ enumEntry.getKey().substring(1);
							List<String> symbols = enumEntry.getValue();

							bw.write("\t" + "public enum " + enumName + " {");
							bw.newLine();
							bw.write("\t\t");
							for (int i = 0; i < symbols.size(); i++) {
								bw.write(symbols.get(i));
								if (i < symbols.size() - 1) {
									bw.write(", ");
								}
							}
							bw.newLine();
							bw.write("\t" + "}");
							bw.newLine();
							bw.newLine();
						}
					}
				}
			}

			// no-args constructor
			bw.write("\t" + "public " + capitalizedClassName + "() {");
			bw.newLine();
			bw.write("\t" + "}");
			bw.newLine();
			bw.newLine();

			if (!fields.isEmpty()) {
				// all-args constructor
				bw.write("\t" + "public " + capitalizedClassName + "(");

				Iterator<Entry<String, String>> fieldsIterator = fields.entrySet().iterator();
				while (fieldsIterator.hasNext()) {
					Entry<String, String> fieldEntry = fieldsIterator.next();
					bw.write(fieldEntry.getValue());
					bw.write(" " + fieldEntry.getKey());
					if (!fieldsIterator.hasNext()) {
						break;
					} else {
						bw.write(", ");
					}
				}

				bw.write(") {");
				bw.newLine();

				for (var fieldEntry : fields.entrySet()) {
					bw.write("\t\t" + "this." + fieldEntry.getKey() + " = " + fieldEntry.getKey() + ";");
					bw.newLine();
				}

				bw.write("\t" + "}");
				bw.newLine();
				bw.newLine();

				// getter & setter
				for (var fieldEntry : fields.entrySet()) {
					String capitalizedFieldEntryKey = fieldEntry.getKey().substring(0, 1).toUpperCase()
							+ fieldEntry.getKey().substring(1);
					bw.write("\t" + "public " + fieldEntry.getValue() + " get" + capitalizedFieldEntryKey + "() {");
					bw.newLine();
					bw.write("\t\t" + "return " + fieldEntry.getKey() + ";");
					bw.newLine();
					bw.write("\t" + "}");
					bw.newLine();
					bw.newLine();

					bw.write("\t" + "public void set" + capitalizedFieldEntryKey + "(" + fieldEntry.getValue() + " "
							+ fieldEntry.getKey() + ") {");
					bw.newLine();
					bw.write("\t\t" + "this." + fieldEntry.getKey() + " = " + fieldEntry.getKey() + ";");
					bw.newLine();
					bw.write("\t" + "}");
					bw.newLine();
					bw.newLine();
				}

				// toString
				bw.write("\t" + "@Override");
				bw.newLine();
				bw.write("\t" + "public String toString() {");
				bw.newLine();
				bw.write("\t\t" + "return \"" + capitalizedClassName + " [");

				fieldsIterator = fields.entrySet().iterator();
				while (fieldsIterator.hasNext()) {
					Entry<String, String> fieldEntry = fieldsIterator.next();

					bw.write(fieldEntry.getKey() + "=\" + " + fieldEntry.getKey() + " + ");
					if (!fieldsIterator.hasNext()) {
						break;
					} else {
						bw.write("\", ");
					}
				}
				bw.write("\"]\";");
				bw.newLine();
				bw.write("\t" + "}");
				bw.newLine();
				bw.newLine();
			}

			bw.write("}");
			bw.flush();
		} catch (FileNotFoundException e) {
			log.warning("Could not find file: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not write file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Writes the Command and Event classes into the project source folder at the
	 * given namespace location. Since Commands and Events are (and should be)
	 * immutable they are made final and all their members are final as well.
	 * Therefore they have no no-args constructor and no setter methods.
	 * 
	 * @param projectSourcePath - Path of the project source folder
	 * @param namespace         - Namespace of the Command/Event class
	 * @param dataType          - The DataType containing all the necessary
	 *                          information of the Command/Event
	 * @param className         - The class name of the Java class
	 * @param description       - The description of the Java class
	 */
	public static void writeCommandOrEvent(String projectSourcePath, String namespace, DataType dataType,
			String className, String description) {
		Map<String, DataType> objectDataType = new HashMap<>();
		if (dataType != null) {
			if (dataType.getSchemaType().equals(SchemaType.MAP)) {
				if (dataType.getObjectDataType() != null) {
					objectDataType.putAll(dataType.getObjectDataType());
				}
			} else {
				objectDataType.put(dataType.getName(), dataType);
			}
		}

		Map<String, String> fields = new HashMap<>();
		String tempClassName = className.strip().replace(" ", "");
		String capitalizedClassName = tempClassName.substring(0, 1).toUpperCase() + tempClassName.substring(1);

		String filePath = projectSourcePath + "/" + namespace.replace(".", "/") + "/" + capitalizedClassName + ".java";

		try (BufferedWriter bw = new BufferedWriter(Files.newBufferedWriter(Paths.get(filePath)))) {
			// namespace
			bw.write("package " + namespace + ";");
			bw.newLine();
			bw.newLine();

			// imports
			// - external
			boolean importsNeeded = false;
			Set<String> uniqueImports = new HashSet<>();
			for (var entry : objectDataType.entrySet()) {
				DataType tempDataType = entry.getValue();
				uniqueImports.addAll(parseImports(tempDataType));
			}
			if (!uniqueImports.isEmpty()) {
				List<String> sortedImports = new ArrayList<>(uniqueImports);
				Collections.sort(sortedImports);
				for (String importString : sortedImports) {
					bw.write("import java.util." + importString + ";");
					bw.newLine();
				}
				importsNeeded = true;
			}
			if (importsNeeded) {
				bw.newLine();
			}
			// - internal
			boolean schemaImportsNeeded = false;
			for (var entry : objectDataType.entrySet()) {
				DataType tempDataType = entry.getValue();
				Set<String> schemaImports = parseSchemaImports(tempDataType);

				if (!schemaImports.isEmpty()) {
					List<String> sortedSchemaImports = new ArrayList<>(schemaImports);
					Collections.sort(sortedSchemaImports);
					for (String importString : sortedSchemaImports) {
						bw.write("import " + namespace.substring(0, namespace.lastIndexOf(".")) + ".schemas."
								+ importString + ";");
						bw.newLine();
					}
					schemaImportsNeeded = true;
				}
			}
			if (schemaImportsNeeded) {
				bw.newLine();
			}

			// javadoc
			if (description != null || (dataType != null && dataType.getProperties() != null
					&& dataType.getSchemaType().equals(SchemaType.MAP))) {
				bw.write("/**");
				bw.newLine();
				if (description != null) {
					bw.write(" * " + description);
					bw.newLine();
				}
				if (dataType != null && dataType.getProperties() != null) {
					if (dataType.getSchemaType().equals(SchemaType.MAP)) {
						if (description != null) {
							bw.write(" * ");
							bw.newLine();
						}
						for (var propertyEntry : dataType.getProperties().entrySet()) {
							bw.write(" * Property: " + propertyEntry.getKey() + " - " + propertyEntry.getValue());
							bw.newLine();
						}
					}
				}
				bw.write(" */");
				bw.newLine();
			}

			// class definition ('final' because Command/Event/ReadModel)
			bw.write("public final class " + capitalizedClassName + " {");
			bw.newLine();
			bw.newLine();

			// field definition
			if (!objectDataType.isEmpty()) {
				// regex constants
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					Map<String, String> regexMap = parseRegexs(tempDataType);
					if (!regexMap.isEmpty()) {
						for (var regexEntry : regexMap.entrySet()) {
							bw.write("\t" + "public static final String " + entry.getKey().toUpperCase() + "_REGEX"
									+ " = \"" + regexEntry.getValue().replace("\\", "\\\\") + "\";");
							bw.newLine();
						}
						bw.newLine();
					}
				}

				// fields
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					String field = parseTypeArgument(tempDataType, false);
					if (!field.isBlank()) {
						fields.put(entry.getKey().strip().replace(" ", ""), field);

						bw.write("\t" + "private final ");
						bw.write(field);
						bw.write(" " + entry.getKey().strip().replace(" ", "") + ";");

						if (tempDataType.getProperties() != null) {
							bw.write(" //");
							for (var tempPropertyEntry : tempDataType.getProperties().entrySet()) {
								bw.write(" | " + tempPropertyEntry.getKey() + " - " + tempPropertyEntry.getValue());
							}
						}
						bw.newLine();
					}
				}
				bw.newLine();

				// nested enum definition
				for (var entry : objectDataType.entrySet()) {
					DataType tempDataType = entry.getValue();

					Map<String, List<String>> enumMap = parseEnums(tempDataType);
					if (!enumMap.isEmpty()) {
						for (var enumEntry : enumMap.entrySet()) {
							String enumName = enumEntry.getKey().substring(0, 1).toUpperCase()
									+ enumEntry.getKey().substring(1);
							List<String> symbols = enumEntry.getValue();

							bw.write("\t" + "public enum " + enumName + " {");
							bw.newLine();
							bw.write("\t\t");
							for (int i = 0; i < symbols.size(); i++) {
								bw.write(symbols.get(i));
								if (i < symbols.size() - 1) {
									bw.write(", ");
								}
							}
							bw.newLine();
							bw.write("\t" + "}");
							bw.newLine();
							bw.newLine();
						}
					}
				}
			}

			if (!fields.isEmpty()) {
				// all-args constructor
				bw.write("\t" + "public " + capitalizedClassName + "(");

				Iterator<Entry<String, String>> fieldsIterator = fields.entrySet().iterator();
				while (fieldsIterator.hasNext()) {
					Entry<String, String> fieldEntry = fieldsIterator.next();
					bw.write(fieldEntry.getValue());
					bw.write(" " + fieldEntry.getKey());
					if (!fieldsIterator.hasNext()) {
						break;
					} else {
						bw.write(", ");
					}
				}

				bw.write(") {");
				bw.newLine();

				for (var fieldEntry : fields.entrySet()) {
					bw.write("\t\t" + "this." + fieldEntry.getKey() + " = " + fieldEntry.getKey() + ";");
					bw.newLine();
				}

				bw.write("\t" + "}");
				bw.newLine();
				bw.newLine();

				// getter & setter
				for (var fieldEntry : fields.entrySet()) {
					String capitalizedFieldEntryKey = fieldEntry.getKey().substring(0, 1).toUpperCase()
							+ fieldEntry.getKey().substring(1);
					bw.write("\t" + "public " + fieldEntry.getValue() + " get" + capitalizedFieldEntryKey + "() {");
					bw.newLine();
					bw.write("\t\t" + "return " + fieldEntry.getKey() + ";");
					bw.newLine();
					bw.write("\t" + "}");
					bw.newLine();
					bw.newLine();
				}

				// toString
				bw.write("\t" + "@Override");
				bw.newLine();
				bw.write("\t" + "public String toString() {");
				bw.newLine();
				bw.write("\t\t" + "return \"" + capitalizedClassName + " [");

				fieldsIterator = fields.entrySet().iterator();
				while (fieldsIterator.hasNext()) {
					Entry<String, String> fieldEntry = fieldsIterator.next();

					bw.write(fieldEntry.getKey() + "=\" + " + fieldEntry.getKey() + " + ");
					if (!fieldsIterator.hasNext()) {
						break;
					} else {
						bw.write("\", ");
					}
				}
				bw.write("\"]\";");
				bw.newLine();
				bw.write("\t" + "}");
				bw.newLine();
				bw.newLine();
			}

			bw.write("}");
			bw.flush();
		} catch (FileNotFoundException e) {
			log.warning("Could not find file: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not write file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Writes the Enum object into the project source folder at the given namespace
	 * location. Since this is not a nested Enum it is quite simplistic only
	 * consisting of its name, a description and the Symbols provided by the
	 * DataType.
	 * 
	 * @param projectSourcePath - Path of the project source folder
	 * @param namespace         - Namespace of the Enum
	 * @param dataType          - The DataType containing all the necessary
	 *                          information of the Enum
	 * @param description       - The description of the Java enum
	 */
	public static void writeEnum(String projectSourcePath, String namespace, DataType dataType, String description) {
		String capitalizedEnumName = dataType.getFormattedName().substring(0, 1).toUpperCase()
				+ dataType.getFormattedName().substring(1);

		String filePath = projectSourcePath + "/" + namespace.replace(".", "/") + "/" + capitalizedEnumName + ".java";

		try (BufferedWriter bw = new BufferedWriter(Files.newBufferedWriter(Paths.get(filePath)))) {
			// namespace
			bw.write("package " + namespace + ";");
			bw.newLine();
			bw.newLine();

			// javadoc
			if (description != null || dataType.getProperties() != null) {
				bw.write("/**");
				bw.newLine();
				if (description != null) {
					bw.write(" * " + description);
					bw.newLine();
				}
				if (dataType.getProperties() != null) {
					if (description != null) {
						bw.write(" * ");
						bw.newLine();
					}
					for (var propertyEntry : dataType.getProperties().entrySet()) {
						bw.write(" * Property: " + propertyEntry.getKey() + " - " + propertyEntry.getValue());
						bw.newLine();
					}
				}
				bw.write(" */");
				bw.newLine();
			}

			// enum definition
			List<String> formattedSymbols = new ArrayList<>();
			for (String enumName : dataType.getEnumList()) {
				formattedSymbols.add(enumName.strip().replace(" ", ""));
			}

			bw.write("public enum " + capitalizedEnumName + " {");
			bw.newLine();
			bw.write("\t");
			for (int i = 0; i < formattedSymbols.size(); i++) {
				bw.write(formattedSymbols.get(i));
				if (i < formattedSymbols.size() - 1) {
					bw.write(", ");
				}
			}
			bw.newLine();
			bw.write("}");

			bw.flush();
		} catch (FileNotFoundException e) {
			log.warning("Could not find file: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not write file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Examines a DataType object for any needed util imports present within it.
	 * Util imports are external imports which are provided by the java.util
	 * libraries. This method may call itself recursively to search for any imports
	 * inside the nested DataType objects. E.g. List<Vector<Map<UUID, Integer>>>
	 * 
	 * @param dataType - The DataType to examine for enums
	 * @return - Set<String> containing the nested entity imports of deeper DataType
	 *         objects passed onto its parent recursively
	 */
	private static Set<String> parseImports(DataType dataType) {
		Set<String> imports = new HashSet<>();

		switch (dataType.getSchemaType()) {
		case UUID:
			imports.add("UUID");
			break;
		case VECTOR:
			imports.add("Vector");
			imports.addAll(parseImports(dataType.getListDataType()));
			break;
		case SEQUENTIAL:
			imports.add("List");
			imports.addAll(parseImports(dataType.getListDataType()));
			break;
		case SET:
			imports.add("Set");
			imports.addAll(parseImports(dataType.getListDataType()));
			break;
		case MAP_OF:
			imports.add("Map");
			imports.addAll(parseImports(dataType.getMapKeyDataType()));
			imports.addAll(parseImports(dataType.getMapValueDataType()));
			break;
		default:
			break;
		}

		return imports;
	}

	/**
	 * Examines a DataType object for any needed schema imports present within it.
	 * Schema imports are internal imports referencing Entities from another
	 * package. This method may call itself recursively to search for any entity
	 * imports inside the nested DataType objects. E.g. List<Vector<Map<Entity_A,
	 * Entity_B>>>
	 * 
	 * @param dataType - The DataType to examine for enums
	 * @return - Set<String> containing the nested entity imports of deeper DataType
	 *         objects passed onto its parent recursively
	 */
	private static Set<String> parseSchemaImports(DataType dataType) {
		Set<String> schemaImports = new HashSet<>();

		switch (dataType.getSchemaType()) {
		case VECTOR:
		case SEQUENTIAL:
		case SET:
			schemaImports.addAll(parseSchemaImports(dataType.getListDataType()));
			break;
		case MAP_OF:
			schemaImports.addAll(parseSchemaImports(dataType.getMapKeyDataType()));
			schemaImports.addAll(parseSchemaImports(dataType.getMapValueDataType()));
			break;
		case MAP:
			schemaImports.add(dataType.getFormattedName().substring(0, 1).toUpperCase()
					+ dataType.getFormattedName().substring(1));
			break;
		case REF:
			for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
				if (dataType.getReference().equals(schemaEntry.getKey())) {
					schemaImports.add(schemaEntry.getValue().getFormattedName().substring(0, 1).toUpperCase()
							+ schemaEntry.getValue().getFormattedName().substring(1));
					break;
				}
			}
			break;
		default:
			break;
		}

		return schemaImports;
	}

	/**
	 * Examines a DataType object for any regex' present within it. This method may
	 * call itself recursively to search for any regex' inside the nested DataType
	 * objects. E.g. List<Vector<Map<Integer, Regex>>>
	 * 
	 * @param dataType - The DataType to examine for enums
	 * @return - Map<String, String> containing the nested regex' of deeper DataType
	 *         objects passed onto its parent recursively
	 */
	private static Map<String, String> parseRegexs(DataType dataType) {
		Map<String, String> regexMap = new HashMap<>();

		switch (dataType.getSchemaType()) {
		case RE:
			regexMap.put(dataType.getFormattedName(), dataType.getRegex());
			break;
		case VECTOR:
		case SEQUENTIAL:
		case SET:
			regexMap.putAll(parseRegexs(dataType.getListDataType()));
			break;
		case MAP_OF:
			regexMap.putAll(parseRegexs(dataType.getMapKeyDataType()));
			regexMap.putAll(parseRegexs(dataType.getMapValueDataType()));
			break;
		default:
			break;
		}

		return regexMap;
	}

	/**
	 * Examines a DataType object for any type arguments present within it. This
	 * method may call itself recursively to search for any argument inside the
	 * nested DataType objects. E.g. List<Vector<Map<Integer, String>>>
	 * 
	 * @param dataType - The DataType to examine for enums
	 * @return - String containing the nested type arguments of deeper DataType
	 *         objects passed onto its parent recursively
	 */
	private static String parseTypeArgument(DataType dataType, boolean isNested) {
		String typeArgument = "";

		switch (dataType.getSchemaType()) {
		case STRING:
		case RE:
			typeArgument = "String";
			break;
		case INT:
			if (isNested)
				typeArgument = "Integer";
			else
				typeArgument = "int";
			break;
		case DOUBLE:
			if (isNested)
				typeArgument = "Double";
			else
				typeArgument = "double";
			break;
		case BOOLEAN:
			if (isNested)
				typeArgument = "Boolean";
			else
				typeArgument = "boolean";
			break;
		case UUID:
			typeArgument = "UUID";
			break;
		case VECTOR:
			typeArgument = "Vector<" + parseTypeArgument(dataType.getListDataType(), true) + ">";
			break;
		case SEQUENTIAL:
			typeArgument = "List<" + parseTypeArgument(dataType.getListDataType(), true) + ">";
			break;
		case SET:
			typeArgument = "Set<" + parseTypeArgument(dataType.getListDataType(), true) + ">";
			break;
		case MAP_OF:
			typeArgument = "Map<" + parseTypeArgument(dataType.getMapKeyDataType(), true) + ", "
					+ parseTypeArgument(dataType.getMapValueDataType(), true) + ">";
			break;
		case MAP:
			typeArgument = dataType.getFormattedName().substring(0, 1).toUpperCase()
					+ dataType.getFormattedName().substring(1);
			break;
		case ENUM:
			typeArgument = dataType.getFormattedName().substring(0, 1).toUpperCase()
					+ dataType.getFormattedName().substring(1);
			break;
		case REF:
			for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
				if (dataType.getReference().equals(schemaEntry.getKey())) {
					typeArgument = schemaEntry.getValue().getFormattedName();
					break;
				}
			}
			break;
		default:
			log.warning("SchemaType not supported: '" + dataType.getSchemaType() + "'");
			break;
		}

		return typeArgument;
	}

	/**
	 * Examines a DataType object for any enums present within it. This method may
	 * call itself recursively to search for any enums inside the nested DataType
	 * objects. E.g. List<Vector<Map<Integer, Enum>>>
	 * 
	 * Additionally formats the String names of the enums inside the enum list to
	 * delete unwanted spaces which would otherwise result in errors when naming
	 * things in Java.
	 * 
	 * @param dataType - The DataType to examine for enums
	 * @return - Map<String, List<String>> containing the nested enums of deeper
	 *         DataType objects passed onto its parent recursively
	 */
	private static Map<String, List<String>> parseEnums(DataType dataType) {
		Map<String, List<String>> enumMap = new HashMap<>();

		switch (dataType.getSchemaType()) {
		case ENUM:
			List<String> formattedEnumList = new ArrayList<>();
			for (String enumName : dataType.getEnumList()) {
				formattedEnumList.add(enumName.strip().replace(" ", ""));
			}
			enumMap.put(dataType.getFormattedName(), formattedEnumList);
			break;
		case VECTOR:
		case SEQUENTIAL:
		case SET:
			enumMap.putAll(parseEnums(dataType.getListDataType()));
			break;
		case MAP_OF:
			enumMap.putAll(parseEnums(dataType.getMapKeyDataType()));
			enumMap.putAll(parseEnums(dataType.getMapValueDataType()));
			break;
		default:
			break;
		}

		return enumMap;
	}

}
