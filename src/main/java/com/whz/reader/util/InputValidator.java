package com.whz.reader.util;

import java.util.List;
import java.util.logging.Logger;

/**
 * InputValidator is a utility class that validates user input in the UI to
 * check if every necessary input is filled in and if inputs are valid.
 * 
 * @author Timon Schwalbe
 */
public class InputValidator {

	private static final Logger log = Logger.getLogger(InputValidator.class.getName());
	private static final String JAVA_KEYWORDS = "files/JavaKeywords.txt";
	private static final String NAMESPACE_REGEX = "(_|\\p{L}|\\p{Sc}){1}(_|\\p{L}|\\p{Sc}|\\p{N})*(\\.(_|\\p{L}|\\p{Sc}){1}(_|\\p{L}|\\p{Sc}|\\p{N})*)*";
	// Regex explanation:
	// '+' = at least once
	// '*' = any number of times
	// (x|y) = 'x' or 'y'
	// '_' = underscore sign
	// '.' = dot sign
	// \p{L} = any kind of letter from any language
	// \p{Sc} = any currency sign
	// \p{N} = any kind of numeric character in any script

	/**
	 * Validates user input for fields 'projectPathTextField', 'jsonPathTextField',
	 * and 'namespaceTextField'. If every input is valid (not blank and proper
	 * file), an empty String is returned. If any input is invalid, a concatenated
	 * String of every violation will be returned.
	 * 
	 * @param projectComboBoxIndex - The index of the project option (0 = new | 1 =
	 *                             existing project)
	 * @param projectPath          - The project path to validate against
	 * @param jsonPath             - The JSON file to validate against
	 * @return String - Empty String if valid user input; String with warning
	 *         message if invalid input(s)
	 */
	public static String validateUserInput(int projectComboBoxIndex, String projectPath, String jsonPath) {
		String warningMessage = "";

		if (!projectPath.isBlank()) {
			if (projectComboBoxIndex == 1) {
				if (!projectPath.contains("src") && !projectPath.contains("sources") && !projectPath.contains("java")) {
					log.warning("Directory: '" + projectPath + "' might not be source folder");
				}
			}
		} else {
			warningMessage += (!warningMessage.isEmpty()) ? "\n" : "";
			warningMessage += I18N.resourceBundle.getString("validator.projectPathEmpty");
		}

		if (!jsonPath.isBlank()) {
			if (!jsonPath.contains(".json") && !jsonPath.contains(".JSON")) {
				warningMessage += (!warningMessage.isEmpty()) ? "\n" : "";
				warningMessage += I18N.resourceBundle.getString("validator.notJSONfile") + " '" + jsonPath + "'";
			}
		} else {
			warningMessage += (!warningMessage.isEmpty()) ? "\n" : "";
			warningMessage += I18N.resourceBundle.getString("validator.JSONPathEmpty");
		}

		return warningMessage;
	}

	/**
	 * Validates if namespace follows Java naming conventions and if namespace does
	 * not contain Java keywords. If any violation is present, a String containing
	 * the warning message is returned. If everything is valid, the String is empty
	 * but not null.
	 * 
	 * @param namespace - The namespace String to validate against
	 * @return String - Empty String if valid namespace; String with warning message
	 *         if invalid namespace
	 */
	public static String validateNamespace(String namespace) {
		String warningMessage = "";

		if (namespace.matches(NAMESPACE_REGEX)) {
			String[] namespaceArray = namespace.split("\\.");
			List<String> keywordList = FileBufferedReader.readFile(JAVA_KEYWORDS);
			boolean isKeyword = false;
			for (String packageName : namespaceArray) {
				for (String keyword : keywordList) {
					if (packageName.equals(keyword)) {
						isKeyword = true;
					}
				}
			}
			if (isKeyword) {
				warningMessage += (!warningMessage.isEmpty()) ? "\n" : "";
				warningMessage += I18N.resourceBundle.getString("validator.noKeywords");
			}
		} else {
			warningMessage += (!warningMessage.isEmpty()) ? "\n" : "";
			warningMessage += I18N.resourceBundle.getString("validator.notFollowingConventions") + " '" + namespace
					+ "'";
		}

		return warningMessage;
	}

}
