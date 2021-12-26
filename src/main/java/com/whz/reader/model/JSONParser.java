package com.whz.reader.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.whz.reader.dto.Audience;
import com.whz.reader.dto.Command;
import com.whz.reader.dto.DataType;
import com.whz.reader.dto.DataType.SchemaType;
import com.whz.reader.dto.Element;
import com.whz.reader.dto.Element.ElementType;
import com.whz.reader.dto.Event;
import com.whz.reader.dto.EventModel;
import com.whz.reader.dto.Flow;
import com.whz.reader.dto.Interface;
import com.whz.reader.dto.Interface.InterfaceType;
import com.whz.reader.dto.Placement;
import com.whz.reader.dto.Placement.LaneType;
import com.whz.reader.dto.Placement.NoteType;
import com.whz.reader.dto.ReadModel;
import com.whz.reader.dto.Schema;
import com.whz.reader.dto.Stream;
import com.whz.reader.util.I18N;
import com.whz.reader.view.ReaderGUI;

/**
 * The JSONParser class is used to parse the given JSON file of an oNote project
 * into usable Java DTOs. Any information will be retained, even if it is not
 * necessary for the final Java project.
 * 
 * @author Timon Schwalbe
 */
public class JSONParser {

	private static final Logger log = Logger.getLogger(JSONParser.class.getName());

	private static final String JSON_VERSION = "0.1.0-beta";

	public static EventModel eventModel;

	/**
	 * Parses the JSON file into usable Java DTOs using Gson. Before actually
	 * parsing the file a lot of checks are done to validate the JSON file and
	 * verify its usability. If successful, a method is called to parse the found
	 * root JSON Object.
	 * 
	 * It should be noted, that the parser will also try to parse older or newer
	 * 'spec-verions' of the JSON file. This is an optimistic approach in which I
	 * hope that the newer JSON versions provided by oNote may be backwards
	 * compatible and may only have minor changes, bug fixes or handy additions
	 * added to them instead of severely changing the underlying structure.
	 * 
	 * @param fileName - File name/path of the JSON file
	 * @return boolean - true if could find and read file; false if could not find
	 *         or read file
	 */
	public static boolean parseJson(String jsonFileName) {
		try (JsonReader jsonReader = new JsonReader(new FileReader(jsonFileName))) {
			JsonElement jsonRootElement = JsonParser.parseReader(jsonReader);

			if (jsonRootElement.isJsonObject()) {
				JsonObject jsonObject = jsonRootElement.getAsJsonObject();
				String currentVersion = jsonObject.get("spec-version").getAsString();
				if (!currentVersion.equals(JSON_VERSION)) {
					log.severe("Different JSON version! Expected: '" + JSON_VERSION + "' but was: '" + currentVersion
							+ "' Parser might not work properly");
				}

				parseIntoEventModel(jsonObject);
				log.info("Finished parsing JSON file");
				return true;
			} else {
				String jsonType = "Unknown";
				if (jsonRootElement.isJsonNull())
					jsonType = "Null";
				else if (jsonRootElement.isJsonPrimitive())
					jsonType = "Primitive";
				else if (jsonRootElement.isJsonArray())
					jsonType = "Array";
				log.warning("Expected file to start with Object but is: '" + jsonType + "'");
			}
		} catch (FileNotFoundException e) {
			log.warning("Could not find file: " + e.getMessage());
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			log.warning("Not valid JSON: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not read JSON file: " + e.getMessage());
			e.printStackTrace();
		}

		ReaderGUI.showWarningDialog(
				I18N.resourceBundle.getString("jsonParser.couldNotReadFile") + " '" + jsonFileName + "'");
		return false;
	}

	/**
	 * Takes the root JSON Object containing all other objects, arrays, data fields,
	 * etc., traversing it and parsing it into Java DTOs
	 * 
	 * @param jsonObject - JsonObject to be traversed and parsed into Java DTOs
	 */
	private static void parseIntoEventModel(JsonObject jsonObject) {
		eventModel = new EventModel();

		JsonObject jsonEventModel = jsonObject.get("event-model").getAsJsonObject();

		parseBasicInformation(jsonEventModel);
		parseAudiences(jsonEventModel);
		parseStreams(jsonEventModel);
		parseInterfaces(jsonEventModel);
		parseReadModels(jsonEventModel);
		parseEvents(jsonEventModel);
		parseCommands(jsonEventModel);
		parseFlows(jsonEventModel);
		parsePlacements(jsonEventModel);
		parseSchemas(jsonEventModel);
	}

	/**
	 * Parses the basic information of the oNote model and saves them to the Java
	 * EventModel DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseBasicInformation(JsonObject jsonEventModel) {
		eventModel.setId(UUID.fromString(jsonEventModel.get("event-model/id").getAsString()));
		eventModel.setName(jsonEventModel.get("event-model/name").getAsString());
		if (jsonEventModel.has("event-model/description")) {
			eventModel.setDescription(jsonEventModel.get("event-model/description").getAsString());
		}
	}

	/**
	 * Parses the audiences of the oNote model and saves them to the Java EventModel
	 * DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseAudiences(JsonObject jsonEventModel) {
		JsonArray jsonAudiences = jsonEventModel.get("event-model/audiences").getAsJsonArray();
		for (JsonElement element : jsonAudiences) {
			JsonObject jsonAudience = element.getAsJsonObject();

			Audience tempAudience = new Audience(UUID.fromString(jsonAudience.get("audience/id").getAsString()),
					jsonAudience.get("audience/name").getAsString());
			eventModel.getAudiences().add(tempAudience);
		}
	}

	/**
	 * Parses the streams of the oNote model and saves them to the Java EventModel
	 * DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseStreams(JsonObject jsonEventModel) {
		JsonArray jsonStreams = jsonEventModel.get("event-model/streams").getAsJsonArray();
		for (JsonElement element : jsonStreams) {
			JsonObject jsonStream = element.getAsJsonObject();

			Stream tempStream = new Stream(UUID.fromString(jsonStream.get("stream/id").getAsString()),
					jsonStream.get("stream/name").getAsString());
			eventModel.getStreams().add(tempStream);
		}
	}

	/**
	 * Parses the interfaces of the oNote model and saves them to the Java
	 * EventModel DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseInterfaces(JsonObject jsonEventModel) {
		JsonObject jsonInterfaceMap = jsonEventModel.get("event-model/interfaces").getAsJsonObject();
		Set<String> jsonInterfaces = jsonInterfaceMap.keySet();
		for (String interfaceId : jsonInterfaces) {
			JsonObject jsonInterface = jsonInterfaceMap.get(interfaceId).getAsJsonObject();

			Interface tempInterface = new Interface();
			tempInterface.setId(UUID.fromString(jsonInterface.get("interface/id").getAsString()));
			tempInterface.setName(jsonInterface.get("interface/name").getAsString());
			if (jsonInterface.has("interface/description")) {
				tempInterface.setDescription(jsonInterface.get("interface/description").getAsString());
			}
			tempInterface.setType(InterfaceType.valueOf(jsonInterface.get("interface/type").getAsString()
					.substring(jsonInterface.get("interface/type").getAsString().lastIndexOf("/") + 1).toUpperCase()));
			if (tempInterface.getType().equals(InterfaceType.FIGMA)) {
				tempInterface.setFigmaURL(jsonInterface.get("interface.type.figma/url").getAsString());
			}
			if (jsonInterface.has("interface/elements")) {
				JsonObject jsonElementMap = jsonInterface.get("interface/elements").getAsJsonObject();
				Set<String> jsonElements = jsonElementMap.keySet();
				for (String elementId : jsonElements) {
					JsonObject jsonElement = jsonElementMap.get(elementId).getAsJsonObject();

					Element tempElement = new Element(UUID.fromString(jsonElement.get("element/id").getAsString()),
							ElementType.valueOf(jsonElement.get("element/type").getAsString()
									.substring(jsonElement.get("element/type").getAsString().lastIndexOf("/") + 1)
									.toUpperCase()),
							jsonElement.get("element/name").getAsString());
					tempInterface.getElements().put(UUID.fromString(elementId), tempElement);
				}
			}
			eventModel.getInterfaces().put(UUID.fromString(interfaceId), tempInterface);
		}
	}

	/**
	 * Parses the read models of the oNote model and saves them to the Java
	 * EventModel DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseReadModels(JsonObject jsonEventModel) {
		JsonObject jsonReadModelMap = jsonEventModel.get("event-model/read-models").getAsJsonObject();
		Set<String> jsonReadModels = jsonReadModelMap.keySet();
		for (String readModelId : jsonReadModels) {
			JsonObject jsonReadModel = jsonReadModelMap.get(readModelId).getAsJsonObject();

			ReadModel tempReadModel = new ReadModel();
			tempReadModel.setId(UUID.fromString(jsonReadModel.get("read-model/id").getAsString()));
			tempReadModel.setName(jsonReadModel.get("read-model/name").getAsString());
			if (jsonReadModel.has("read-model/description")) {
				tempReadModel.setDescription(jsonReadModel.get("read-model/description").getAsString());
			}
			if (jsonReadModel.has("read-model/schemas")) {
				JsonObject jsonSchemaMap = jsonReadModel.get("read-model/schemas").getAsJsonObject();
				Set<String> jsonSchemas = jsonSchemaMap.keySet();
				if (jsonSchemas.size() == 1) {
					for (String schemaId : jsonSchemas) {
						tempReadModel.setSchemaId(UUID.fromString(jsonSchemaMap.get(schemaId).getAsString()));
					}
				} else {
					log.severe("Multiple schemas (AVRO) not supported!");
				}
			}
			eventModel.getReadModels().put(UUID.fromString(readModelId), tempReadModel);
		}
	}

	/**
	 * Parses the events of the oNote model and saves them to the Java EventModel
	 * DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseEvents(JsonObject jsonEventModel) {
		JsonObject jsonEventMap = jsonEventModel.get("event-model/events").getAsJsonObject();
		Set<String> jsonEvents = jsonEventMap.keySet();
		for (String eventId : jsonEvents) {
			JsonObject jsonEvent = jsonEventMap.get(eventId).getAsJsonObject();

			Event tempEvent = new Event();
			tempEvent.setId(UUID.fromString(jsonEvent.get("event/id").getAsString()));
			tempEvent.setName(jsonEvent.get("event/name").getAsString());
			if (jsonEvent.has("event/description")) {
				tempEvent.setDescription(jsonEvent.get("event/description").getAsString());
			}
			if (jsonEvent.has("event/schemas")) {
				JsonObject jsonSchemaMap = jsonEvent.get("event/schemas").getAsJsonObject();
				Set<String> jsonSchemas = jsonSchemaMap.keySet();
				if (jsonSchemas.size() == 1) {
					for (String schemaId : jsonSchemas) {
						tempEvent.setSchemaId(UUID.fromString(jsonSchemaMap.get(schemaId).getAsString()));
					}
				} else {
					log.severe("Multiple schemas (AVRO) not supported!");
				}
			}
			eventModel.getEvents().put(UUID.fromString(eventId), tempEvent);
		}
	}

	/**
	 * Parses the commands of the oNote model and saves them to the Java EventModel
	 * DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseCommands(JsonObject jsonEventModel) {
		JsonObject jsonCommandMap = jsonEventModel.get("event-model/commands").getAsJsonObject();
		Set<String> jsonCommands = jsonCommandMap.keySet();
		for (String commandId : jsonCommands) {
			JsonObject jsonCommand = jsonCommandMap.get(commandId).getAsJsonObject();

			Command tempCommand = new Command();
			tempCommand.setId(UUID.fromString(jsonCommand.get("command/id").getAsString()));
			tempCommand.setName(jsonCommand.get("command/name").getAsString());
			if (jsonCommand.has("command/description")) {
				tempCommand.setDescription(jsonCommand.get("command/description").getAsString());
			}
			if (jsonCommand.has("command/schemas")) {
				JsonObject jsonSchemaMap = jsonCommand.get("command/schemas").getAsJsonObject();
				Set<String> jsonSchemas = jsonSchemaMap.keySet();
				if (jsonSchemas.size() == 1) {
					for (String schemaId : jsonSchemas) {
						tempCommand.setSchemaId(UUID.fromString(jsonSchemaMap.get(schemaId).getAsString()));
					}
				} else {
					log.severe("Multiple schemas (AVRO) not supported!");
				}
			}
			eventModel.getCommands().put(UUID.fromString(commandId), tempCommand);
		}
	}

	/**
	 * Parses the flows between notes within oNote model and saves them to the Java
	 * EventModel DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseFlows(JsonObject jsonEventModel) {
		JsonObject jsonFlowMap = jsonEventModel.get("event-model/flows").getAsJsonObject();
		Set<String> jsonFlows = jsonFlowMap.keySet();
		for (String flowId : jsonFlows) {
			JsonObject jsonFlow = jsonFlowMap.get(flowId).getAsJsonObject();

			Flow tempFlow = new Flow(UUID.fromString(jsonFlow.get("flow/from").getAsString()),
					UUID.fromString(jsonFlow.get("flow/to").getAsString()));
			eventModel.getFlows().put(UUID.fromString(flowId), tempFlow);
		}
	}

	/**
	 * Parses the placements of all notes within the oNote model and saves them to
	 * the Java EventModel DTO.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parsePlacements(JsonObject jsonEventModel) {
		JsonObject jsonPlacementMap = jsonEventModel.get("event-model/placements").getAsJsonObject();
		Set<String> jsonPlacements = jsonPlacementMap.keySet();
		for (String placementId : jsonPlacements) {
			JsonObject jsonPlacement = jsonPlacementMap.get(placementId).getAsJsonObject();

			Placement tempPlacement = new Placement();
			tempPlacement.setId(UUID.fromString(jsonPlacement.get("placement/id").getAsString()));
			tempPlacement.setIndex(jsonPlacement.get("placement/index").getAsInt());
			if (jsonPlacement.has("interface/id")) {
				tempPlacement.setNoteType(NoteType.INTERFACE);
				tempPlacement.setNoteId(UUID.fromString(jsonPlacement.get("interface/id").getAsString()));
				tempPlacement.setLaneType(LaneType.AUDIENCE);
				if (jsonPlacement.has("interface/audience")) {
					tempPlacement.setLaneId(UUID.fromString(jsonPlacement.get("interface/audience").getAsString()));
				}
			} else if (jsonPlacement.has("command/id")) {
				tempPlacement.setNoteType(NoteType.COMMAND);
				tempPlacement.setNoteId(UUID.fromString(jsonPlacement.get("command/id").getAsString()));
				tempPlacement.setLaneType(LaneType.TIMELINE);
			} else if (jsonPlacement.has("read-model/id")) {
				tempPlacement.setNoteType(NoteType.READ_MODEL);
				tempPlacement.setNoteId(UUID.fromString(jsonPlacement.get("read-model/id").getAsString()));
				tempPlacement.setLaneType(LaneType.TIMELINE);
			} else if (jsonPlacement.has("event/id")) {
				tempPlacement.setNoteType(NoteType.EVENT);
				tempPlacement.setNoteId(UUID.fromString(jsonPlacement.get("event/id").getAsString()));
				tempPlacement.setLaneType(LaneType.STREAM);
				if (jsonPlacement.has("event/stream")) {
					tempPlacement.setLaneId(UUID.fromString(jsonPlacement.get("event/stream").getAsString()));
				}
			}
			eventModel.getPlacements().put(UUID.fromString(placementId), tempPlacement);
		}
	}

	/**
	 * Parses the schemas of the oNote model and saves them to the Java EventModel
	 * DTO.
	 * 
	 * A Schema can consist of basic information and a DataType which represents its
	 * type of schema. DataTypes can consist of other DataTypes in a nested way.
	 * Therefore the parser method for parsing these types has to be recursive to
	 * call itself whenever another nested DataType is found within the JSON object.
	 * 
	 * @param jsonEventModel - JsonObject containing all information of the event
	 *                       model.
	 */
	private static void parseSchemas(JsonObject jsonEventModel) {
		JsonObject jsonSchemaMap = jsonEventModel.get("event-model/schemas").getAsJsonObject();
		Set<String> jsonPlacements = jsonSchemaMap.keySet();
		for (String schemaId : jsonPlacements) {
			JsonObject jsonSchema = jsonSchemaMap.get(schemaId).getAsJsonObject();

			Schema tempSchema = new Schema();
			tempSchema.setId(UUID.fromString(jsonSchema.get("schema/id").getAsString()));
			tempSchema.setName(jsonSchema.get("schema/name").getAsString()
					.substring(jsonSchema.get("schema/name").getAsString().lastIndexOf("/") + 1));
			tempSchema.setNamespace(jsonSchema.get("schema/name").getAsString().substring(0,
					jsonSchema.get("schema/name").getAsString().lastIndexOf("/")));
			if (jsonSchema.has("schema/description")) {
				tempSchema.setDescription(jsonSchema.get("schema/description").getAsString());
			}

			JsonObject jsonDataType = jsonSchema.get("schema/schema").getAsJsonObject();
			DataType dataType = parseDataTypesRecursively(jsonDataType, tempSchema.getName(), false);
			tempSchema.setDataType(dataType);

			eventModel.getSchemas().put(UUID.fromString(schemaId), tempSchema);
		}
	}

	/**
	 * This method might call itself in a recursive manner if the JSON file declares
	 * that a DataType consists of further nested DataType(s). It returns the nested
	 * DataType so that it can be set in the parent DataType.
	 * 
	 * Warning!: 'TUPLE' and 'MAP' are exceptions since they can be completely empty
	 * solely containing their respective SchemaType!
	 * 
	 * @param jsonDataType - JsonObject containing all information of the schema.
	 * @param tempName     - generic name for DataType needed for nested Objects
	 *                     (='MAP') and Enums (='ENUM')
	 * @param isNested     - Indicates if the DataType object is the root or a
	 *                     nested DataType which is relevant for the designation of
	 *                     nested objects and enums
	 * @return DataType - The DataType which can be used by the parent DataType to
	 *         set this as its nested child.
	 */
	private static DataType parseDataTypesRecursively(JsonObject jsonDataType, String tempName, boolean isNested) {
		DataType tempDataType = new DataType();
		tempDataType.setName(tempName);
		tempDataType.setSchemaType(SchemaType
				.valueOf(jsonDataType.get("type").getAsString().substring(1).replace("-", "_").toUpperCase()));

		// Set new DataType name if object is a nested MAP or ENUM which would not have
		// a name otherwise since oNote "forgot"? that they should have one or else they
		// cannot be referenced.
		String newName;
		if (isNested) {
			if (tempDataType.getSchemaType().equals(SchemaType.MAP)) {
				newName = tempDataType.getName() + "Object";
				tempDataType.setName(newName);
			} else if (tempDataType.getSchemaType().equals(SchemaType.ENUM)) {
				newName = tempDataType.getName() + "Enum";
				tempDataType.setName(newName);
			}
		}

		if (jsonDataType.has("properties")) {
			JsonArray jsonPropertiesArray = jsonDataType.get("properties").getAsJsonArray();

			Map<String, String> tempProperties = new HashMap<>();
			for (JsonElement element : jsonPropertiesArray) {
				JsonObject jsonProperty = element.getAsJsonObject();
				tempProperties.put(jsonProperty.get("key").getAsString(), jsonProperty.get("value").getAsString());
			}
			tempDataType.setProperties(tempProperties);
		}

		if (jsonDataType.has("children")) {
			JsonArray jsonChildrenArray = jsonDataType.get("children").getAsJsonArray();

			switch (tempDataType.getSchemaType()) {
			case VECTOR:
			case SEQUENTIAL:
			case SET:
			case NOT:
			case MAYBE:
				JsonObject vectorObject = jsonChildrenArray.get(0).getAsJsonObject();
				DataType vectorDataType = parseDataTypesRecursively(vectorObject, tempDataType.getName(), true);
				tempDataType.setListDataType(vectorDataType);
				break;

			case MAP_OF:
				JsonObject mapKeyObject = jsonChildrenArray.get(0).getAsJsonObject();
				JsonObject mapValueObject = jsonChildrenArray.get(1).getAsJsonObject();
				// "Key" and "Value" are needed to separate both recursive paths with unique
				// names if they have nested objects to differentiate between them since oNote
				// does not give the option to name nested Objects or Enums.
				DataType keyDataType = parseDataTypesRecursively(mapKeyObject, tempDataType.getName() + "Key", true);
				DataType valueDataType = parseDataTypesRecursively(mapValueObject, tempDataType.getName() + "Value",
						true);
				tempDataType.setMapKeyDataType(keyDataType);
				tempDataType.setMapValueDataType(valueDataType);
				break;

			case TUPLE:
			case OR:
			case AND:
				List<DataType> dataTypes = new ArrayList<>();
				for (JsonElement element : jsonChildrenArray) {
					JsonObject child = element.getAsJsonObject();
					DataType childDataType = parseDataTypesRecursively(child, tempDataType.getName(), true);
					dataTypes.add(childDataType);
				}
				tempDataType.setDataTypeTuple(dataTypes);
				break;

			case MAP:
				Map<String, DataType> objectDataType = new HashMap<>();
				for (JsonElement element : jsonChildrenArray) {
					JsonObject child = element.getAsJsonObject();
					String childKey = child.get("entry").getAsString();
					JsonObject nestedChild = child.get("schema").getAsJsonObject();
					DataType nestedChildDataType = parseDataTypesRecursively(nestedChild, tempDataType.getName(), true);
					objectDataType.put(childKey, nestedChildDataType);
				}
				tempDataType.setObjectDataType(objectDataType);
				break;

			case ENUM:
				List<String> tempEnumList = new ArrayList<>();
				for (JsonElement element : jsonChildrenArray) {
					tempEnumList.add(element.getAsJsonObject().get("symbol").getAsString());
				}
				tempDataType.setEnumList(tempEnumList);
				break;

			case RE:
				JsonObject regexObject = jsonChildrenArray.get(0).getAsJsonObject();
				tempDataType.setRegex(regexObject.get("expression").getAsString());
				break;

			case REF:
				JsonObject referenceObject = jsonChildrenArray.get(0).getAsJsonObject();
				UUID uuid = UUID.fromString(referenceObject.get("reference").getAsString());
				tempDataType.setReference(uuid);
				break;

			default:
				log.warning("The SchemaType is either unknown or not supposed to have children: '"
						+ tempDataType.getSchemaType() + "'");
				break;
			}
		}

		return tempDataType;
	}
}
