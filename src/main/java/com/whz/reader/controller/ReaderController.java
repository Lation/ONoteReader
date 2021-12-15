package com.whz.reader.controller;

import com.whz.reader.model.JSONParser;
import com.whz.reader.model.ProjectGenerator;

/**
 * The ReaderController class represents the binder between the model and the
 * view. While the view is containing the GUI of the application and handles
 * action events, the controller takes these action events and executes calls to
 * the business logic.
 * 
 * @author Timon Schwalbe
 */
public class ReaderController {

	/**
	 * Takes the JSON file name to execute a call to the JSONParser class which
	 * generates Java DTOs of the given json objects. Furthermore takes the project
	 * path and the namespace selection of the user to execute a call to the
	 * JavaGenerator which generates Java code at the defined location.
	 * 
	 * @param jsonFileName           - File name/path of the JSON file
	 * @param projectComboBoxIndex   - User selection of where to generate the
	 *                               project
	 * @param projectPath      - Path of the project source folder to generate
	 *                               the Java code into
	 * @param namespaceComboBoxIndex - User selection of how to generate the project
	 *                               namespace
	 * @param declaredNamespace      - Namespace declared by the user if he selected
	 *                               to use a separate namespace
	 */
	public void generateJavaCode(String jsonFileName, int projectComboBoxIndex, String projectPath,
			int namespaceComboBoxIndex, String declaredNamespace) {
		if (JSONParser.parseJson(jsonFileName)) {
			ProjectGenerator.generateProject(projectComboBoxIndex, projectPath, namespaceComboBoxIndex,
					declaredNamespace);
		}
	}

}
