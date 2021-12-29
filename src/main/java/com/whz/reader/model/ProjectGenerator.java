package com.whz.reader.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.whz.reader.dto.Command;
import com.whz.reader.dto.DataType;
import com.whz.reader.dto.DataType.SchemaType;
import com.whz.reader.dto.Event;
import com.whz.reader.dto.Flow;
import com.whz.reader.dto.Placement;
import com.whz.reader.dto.Placement.LaneType;
import com.whz.reader.dto.Placement.NoteType;
import com.whz.reader.dto.ReadModel;
import com.whz.reader.dto.Stream;
import com.whz.reader.util.I18N;
import com.whz.reader.util.InputValidator;
import com.whz.reader.view.ReaderGUI;

/**
 * ProjectGenerator class, which sets up the entire project. Based on the user
 * input this will either be an entirely new project or an existing project
 * which will be implemented with all the information given by the oNote event
 * model. Any Notes and Schemas will be parsed to Java classes and will be
 * implemented with the given information and data fields. Furthermore, creates
 * no-args and all-args constructors, getter and setter, enums, referenced
 * objects, nested objects and a basic toString method for every Object-Schema,
 * non-nested Enum-Schema, Command, Event and Read Model. Commands and Events
 * are made final with all their members being final as they are immutable
 * objects. Therefore, they do not have a no-args constructor and do not have
 * setters.
 * 
 * @author Timon Schwalbe
 */
public class ProjectGenerator {

	private static final Logger log = Logger.getLogger(ProjectGenerator.class.getName());

	/**
	 * Sets up the project by building the namespace based on the information given
	 * by the user. Calls methods to create the project(s) if needed and finally
	 * generates all the Java classes and implements them with the logic given by
	 * the oNote event model.
	 * 
	 * After every class has been generated a dialog window will pop up stating the
	 * success of the project and code generation.
	 * 
	 * @param projectComboBoxIndex   - The project creation option selected by the
	 *                               user
	 * @param projectPath            - The path of the new project
	 * @param namespaceComboBoxIndex - The namespace option selected by the user
	 * @param declaredNamespace      - The separate namespace declared by the user
	 *                               depending on the selected namespace option
	 */
	public static void generateProject(int projectComboBoxIndex, String projectPath, int namespaceComboBoxIndex,
			String declaredNamespace) {
		String namespace = "";

		if (namespaceComboBoxIndex == 0) {
			namespace = declaredNamespace;
		} else if (namespaceComboBoxIndex == 1) {
			Map<String, Integer> namespaceMap = new HashMap<>();
			for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
				String tempNamespace = schemaEntry.getValue().getNamespace();

				if (namespaceMap.containsKey(tempNamespace)) {
					namespaceMap.put(tempNamespace, namespaceMap.get(tempNamespace) + 1);
				} else {
					namespaceMap.put(tempNamespace, 1);
				}
			}
			String newNamespace = namespaceMap.entrySet().stream()
					.max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();
			if (newNamespace.equals("default")) {
				newNamespace = "main";
			}
			namespace = newNamespace;
		} else if (namespaceComboBoxIndex == 2) {
			namespace = "main";
		}

		String warningMessage = InputValidator.validateNamespace(namespace);
		if (warningMessage.isEmpty()) {
			if (createBoundedContexts(projectComboBoxIndex, projectPath, namespace)) {
				log.info("Finished code generation");
				ReaderGUI.showInfoDialog(I18N.resourceBundle.getString("projectGenerator.projectSuccess"));
			}
		} else {
			ReaderGUI.showWarningDialog(warningMessage);
		}
	}

	/**
	 * This method separates the oNote Event Model into separate Bounded Contexts
	 * based on each Stream available in the Event Model. Each project will consist
	 * of its Events (in the same Stream) and every other Note that is connected to
	 * at least one of these specific Events.
	 * 
	 * If the oNote Event Model contains a "Default" Stream, all Events inside this
	 * Stream and all related Notes will be added to a separate "DefaultStream"
	 * project. This should be avoided by the user since a Bounded Context should
	 * have a proper name.
	 * 
	 * @param projectComboBoxIndex - The project creation option selected by the
	 *                             user
	 * @param projectPath          - The path of the new project
	 * @param namespace            - Namespace provided by the user
	 * @return boolean - true if Streams are following Java naming conventions;
	 *         false if not
	 */
	private static boolean createBoundedContexts(int projectComboBoxIndex, String projectPath, String namespace) {
		// Adding "Default"-Stream to Stream-List
		JSONParser.eventModel.getStreams().add(new Stream(null, "DefaultStream"));

		for (Stream stream : JSONParser.eventModel.getStreams()) {
			UUID projectID = stream.getId();
			String projectName = stream.getFormattedName().substring(0, 1).toUpperCase()
					+ stream.getFormattedName().substring(1);

			String warningMessage = InputValidator.validateNamespace(projectName);
			List<Placement> placementsOfProject = findPlacementsOfCurrentProject(projectID);

			if (warningMessage.isEmpty()) {
				if (!placementsOfProject.isEmpty()) {
					String projectNamespace = namespace + "." + projectName.substring(0, 1).toLowerCase()
							+ projectName.substring(1);
					String projectSourcePath = null;
					boolean canCreateProject = false;

					if (projectComboBoxIndex == 0) {
						projectSourcePath = projectPath + "/" + projectName + "/src";
						canCreateProject = createBasicProject(projectSourcePath.replace("\\", "/"));
					} else if (projectComboBoxIndex == 1) {
						projectSourcePath = projectPath;
						canCreateProject = true;
					} else {
						log.warning("Only two project options possible but index was neither: '" + projectComboBoxIndex
								+ "'");
					}

					if (canCreateProject) {
						generateClasses(projectID, projectSourcePath, projectNamespace, placementsOfProject);
					}
				}
			} else {
				ReaderGUI.showWarningDialog(warningMessage);
				return false;
			}
		}
		return true;
	}

	/**
	 * If user declared that a new project shall be initiated, a basic project
	 * structure will be generated containing the main folder and a 'src' folder for
	 * each Bounded Context.
	 * 
	 * @param projectSourcePath - Path of the project source folder
	 * @return boolean - true if project could be generated; false if it could not,
	 *         i.e. if it already exists
	 */
	private static boolean createBasicProject(String projectSourcePath) {
		if (new File(projectSourcePath).mkdirs()) {
			log.info("Created directory: '" + projectSourcePath + "'");
			return true;
		} else {
			ReaderGUI.showWarningDialog(I18N.resourceBundle.getString("projectGenerator.couldNotCreateProject") + " '"
					+ projectSourcePath + "'");
			return false;
		}
	}

	/**
	 * Will call all the other methods to generate all the Java classes through the
	 * given Event Model. Furthermore, adds a proper package name to the respective
	 * namespace.
	 * 
	 * @param projectID         - ID of the Bounded Context (Stream) and therefore
	 *                          of a separate project/namespace
	 * @param projectSourcePath - Path of the project source folder
	 * @param projectNamespace  - Namespace of the project
	 */
	private static void generateClasses(UUID projectID, String projectSourcePath, String projectNamespace,
			List<Placement> placementsOfProject) {
		createNamespace(projectSourcePath.replace("\\", "/") + "/" + projectNamespace.replace(".", "/"));

		createEvents(placementsOfProject, projectSourcePath, projectNamespace + ".events");
		createCommands(placementsOfProject, projectSourcePath, projectNamespace + ".commands");
		createReadModels(placementsOfProject, projectSourcePath, projectNamespace + ".readModels");
	}

	/**
	 * Creates the given namespace path as a new directory and adds further key
	 * folders for the to every project available. Any non-existent parent folders
	 * will be created as well in the process.
	 * 
	 * @param namespacePath - The namespace with path to be generated as a
	 *                      directory/package structure
	 */
	private static void createNamespace(String namespacePath) {
		if (new File(namespacePath + "/commands").mkdirs())
			log.info("Created directory: '" + namespacePath + "/commands'");
		if (new File(namespacePath + "/events").mkdirs())
			log.info("Created directory: '" + namespacePath + "/events'");
		if (new File(namespacePath + "/readModels").mkdirs())
			log.info("Created directory: '" + namespacePath + "/readModels'");
		if (new File(namespacePath + "/schemas").mkdirs())
			log.info("Created directory: '" + namespacePath + "/schemas'");
	}

	/**
	 * First goes through all Events in the Event Model and checks if the current
	 * Event is part of the Bounded Context (Stream).
	 * 
	 * Then calls the JavaFileWriter to write Event classes for every Event. They
	 * may be fully implemented or an empty class. Since Commands and Events share
	 * similar immutability, they are generated via the same method call.
	 * 
	 * @param placementsOfProject - UUIDs of all Events in the current Bounded
	 *                            Context (Stream)
	 * @param projectSourcePath   - Path of the project source folder
	 * @param projectNamespace    - Namespace of the event classes
	 */
	private static void createEvents(List<Placement> placementsOfProject, String projectSourcePath,
			String projectNamespace) {
		for (var eventEntry : JSONParser.eventModel.getEvents().entrySet()) {
			for (Placement placementOfProject : placementsOfProject) {
				if (placementOfProject.getNoteType().equals(NoteType.EVENT)
						&& placementOfProject.getNoteId().equals(eventEntry.getKey())) {
					Event event = eventEntry.getValue();
					DataType dataType = null;
					String eventName = event.getFormattedName();
					String description = event.getDescription();

					for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
						if (schemaEntry.getKey().equals(event.getSchemaId())) {
							dataType = schemaEntry.getValue().getDataType();
							createSchemasRecursively(projectSourcePath,
									projectNamespace.substring(0, projectNamespace.lastIndexOf(".")) + ".schemas",
									dataType, schemaEntry.getValue().getDescription());
						}
					}
					JavaFileWriter.writeCommandOrEvent(projectSourcePath, projectNamespace, dataType, eventName,
							description);
				}
			}
		}
	}

	/**
	 * First goes through all Commands in the Event Model and checks if the current
	 * Command is part of the Bounded Context by checking if a Flow is connected
	 * between this Command and an Event that belongs to the Bounded Context. (Can
	 * be bidirectional)
	 * 
	 * Calls the JavaFileWriter to write Command classes for every command contained
	 * in the Event Model. They may be fully implemented or an empty class. Since
	 * Commands and Events share similar immutability, they are generated via the
	 * same method call.
	 * 
	 * @param placementsOfProject - UUIDs of all Events in the current Bounded
	 *                            Context (Stream)
	 * @param projectSourcePath   - Path of the project source folder
	 * @param projectNamespace    - Namespace of the command classes
	 */
	private static void createCommands(List<Placement> placementsOfProject, String projectSourcePath,
			String projectNamespace) {
		for (var commandEntry : JSONParser.eventModel.getCommands().entrySet()) {
			for (Placement placementOfProject : placementsOfProject) {
				if (placementOfProject.getNoteType().equals(NoteType.COMMAND)
						&& placementOfProject.getNoteId().equals(commandEntry.getKey())) {
					Command command = commandEntry.getValue();
					DataType dataType = null;
					String commandName = command.getFormattedName();
					String description = command.getDescription();

					for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
						if (schemaEntry.getKey().equals(command.getSchemaId())) {
							dataType = schemaEntry.getValue().getDataType();
							createSchemasRecursively(projectSourcePath,
									projectNamespace.substring(0, projectNamespace.lastIndexOf(".")) + ".schemas",
									dataType, schemaEntry.getValue().getDescription());
						}
					}
					JavaFileWriter.writeCommandOrEvent(projectSourcePath, projectNamespace, dataType, commandName,
							description);
				}
			}
		}
	}

	/**
	 * First goes through all Read Models in the Event Model and checks if the
	 * current Read Model is part of the Bounded Context by checking if a Flow is
	 * connected between this Read Model and an Event that belongs to the Bounded
	 * Context. (Can only be unidirectional: from Event -> to Read Model)
	 * 
	 * Calls the JavaFileWriter to write Read Model classes for every read model
	 * contained in the Event Model. They may be fully implemented or an empty
	 * class. Since Read Models share similar mutability with Schema Entities, like
	 * having setter, a no-args constructor and their fields not being final, they
	 * are generated via the same method call.
	 * 
	 * @param placementsOfProject - UUIDs of all Events in the current Bounded
	 *                            Context (Stream)
	 * @param projectSourcePath   - Path of the project source folder
	 * @param projectNamespace    - Namespace of the read model classes
	 */
	private static void createReadModels(List<Placement> placementsOfProject, String projectSourcePath,
			String projectNamespace) {
		for (var readModelEntry : JSONParser.eventModel.getReadModels().entrySet()) {
			for (Placement placementOfProject : placementsOfProject) {
				if (placementOfProject.getNoteType().equals(NoteType.READ_MODEL)
						&& placementOfProject.getNoteId().equals(readModelEntry.getKey())) {
					ReadModel readModel = readModelEntry.getValue();
					DataType dataType = null;
					String readModelName = readModel.getFormattedName();
					String description = readModel.getDescription();

					for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
						if (schemaEntry.getKey().equals(readModel.getSchemaId())) {
							dataType = schemaEntry.getValue().getDataType();
							createSchemasRecursively(projectSourcePath,
									projectNamespace.substring(0, projectNamespace.lastIndexOf(".")) + ".schemas",
									dataType, schemaEntry.getValue().getDescription());
						}
					}
					JavaFileWriter.writeEntity(projectSourcePath, projectNamespace, dataType, readModelName,
							description);
				}
			}
		}
	}

	/**
	 * This method will be called by every Command, Event and Read Model that will
	 * be generated. This method additionally creates an Entity for each of these
	 * classes containing the base object and any nested objects or references of
	 * objects or enums within it. These Entities may be simple Value Objects,
	 * Primitives, Enums, Entities or even Aggregates.
	 * 
	 * Note: If a reference points to another 'MAP' (object) this behavior is quite
	 * reasonable. If the reference points to a primitive, it might be
	 * counterintuitive as to why a single primitive is made into an Object
	 * (separate Java class). However, this is purely the decision of the user has
	 * he clearly declared that this object may have a reference to a single
	 * primitive type.
	 * 
	 * @param projectSourcePath - Path of the project source folder
	 * @param projectNamespace  - Namespace of the schema classes
	 * @param dataType          - The current DataType which may contain further
	 *                          nested DataTypes. If the DataType is a map or a
	 *                          reference it will be generated into a separate
	 *                          class.
	 * @param schemaDescription - Description of the class to be written
	 */
	private static void createSchemasRecursively(String projectSourcePath, String projectNamespace, DataType dataType,
			String schemaDescription) {
		switch (dataType.getSchemaType()) {
		case STRING:
		case INT:
		case DOUBLE:
		case BOOLEAN:
		case UUID:
		case ENUM:
		case RE:
			break;

		case VECTOR:
		case SEQUENTIAL:
		case SET:
			createSchemasRecursively(projectSourcePath, projectNamespace, dataType.getListDataType(), null);
			break;

		case MAP_OF:
			createSchemasRecursively(projectSourcePath, projectNamespace, dataType.getMapKeyDataType(), null);
			createSchemasRecursively(projectSourcePath, projectNamespace, dataType.getMapValueDataType(), null);
			break;

		case MAP:
			JavaFileWriter.writeEntity(projectSourcePath, projectNamespace, dataType, null, schemaDescription);

			for (var nestedDataType : dataType.getObjectDataType().entrySet()) {
				createSchemasRecursively(projectSourcePath, projectNamespace, nestedDataType.getValue(), null);
			}
			break;

		case REF:
			for (var schemaEntry : JSONParser.eventModel.getSchemas().entrySet()) {
				if (dataType.getReference().equals(schemaEntry.getKey())) {
					DataType referencedDataType = schemaEntry.getValue().getDataType();

					if (referencedDataType.getSchemaType().equals(SchemaType.ENUM)) {
						JavaFileWriter.writeEnum(projectSourcePath, projectNamespace, referencedDataType,
								schemaDescription);
					} else {
						JavaFileWriter.writeEntity(projectSourcePath, projectNamespace, referencedDataType, null,
								schemaDescription);
					}
				}
			}
			break;

		default:
			log.warning("SchemaType not supported: '" + dataType.getSchemaType() + "'");
			break;
		}
	}

	/**
	 * Goes through all Placements and checks if they belong to the current Bounded
	 * Context. First searches for all Event-Placements that are in the correct
	 * Stream (projectID), then searches for all Placements that are Commands and
	 * Read Models that are connected to these specific Event-Placements.
	 * 
	 * If an Event belongs to the "Default"-Stream (if given projectID is 'null')
	 * all related Objects will be added to a "DefaultStream" project.
	 * 
	 * @param projectID - ID of the current Bounded Context (Stream)
	 * @return List<Placement> - A list containing all Placements belong to the
	 *         current Bounded Context (Stream)
	 */
	private static List<Placement> findPlacementsOfCurrentProject(UUID projectID) {
		List<Placement> placementsInCurrentProject = new ArrayList<>();
		List<UUID> eventPlacementIDs = new ArrayList<>();

		// search IDs of Placements that are Events and belong to current Stream
		for (var placementEntry : JSONParser.eventModel.getPlacements().entrySet()) {
			Placement placement = placementEntry.getValue();

			if (placement.getNoteType().equals(NoteType.EVENT) && placement.getLaneType().equals(LaneType.STREAM)) {
				if (projectID == null) {
					if (placement.getLaneId() == null) {
						placementsInCurrentProject.add(placement);
						eventPlacementIDs.add(placementEntry.getKey());
					}
				} else {
					if (placement.getLaneId() != null && placement.getLaneId().equals(projectID)) {
						placementsInCurrentProject.add(placement);
						eventPlacementIDs.add(placementEntry.getKey());
					}
				}
			}
		}
		// search for Commands that are connected to Event of current Stream
		for (var placementEntry : JSONParser.eventModel.getPlacements().entrySet()) {
			Placement placement = placementEntry.getValue();

			if (placement.getNoteType().equals(NoteType.COMMAND)) {
				for (var flowEntry : JSONParser.eventModel.getFlows().entrySet()) {
					Flow flow = flowEntry.getValue();

					if ((flow.getFrom().equals(placementEntry.getKey()) && eventPlacementIDs.contains(flow.getTo()))
							|| (flow.getTo().equals(placementEntry.getKey())
									&& eventPlacementIDs.contains(flow.getFrom()))) {
						placementsInCurrentProject.add(placement);
					}
				}
			}
		}
		// search for ReadModels that are connected to Event of current Stream
		for (var placementEntry : JSONParser.eventModel.getPlacements().entrySet()) {
			Placement placement = placementEntry.getValue();

			if (placement.getNoteType().equals(NoteType.READ_MODEL)) {
				for (var flowEntry : JSONParser.eventModel.getFlows().entrySet()) {
					Flow flow = flowEntry.getValue();

					if (flow.getTo().equals(placementEntry.getKey()) && eventPlacementIDs.contains(flow.getFrom())) {
						placementsInCurrentProject.add(placement);
					}
				}
			}
		}

		return placementsInCurrentProject;
	}

}
