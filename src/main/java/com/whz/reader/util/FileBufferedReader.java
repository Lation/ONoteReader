package com.whz.reader.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple file reader class which takes a file name and returns a List of
 * Strings read from the file located in the resources folder. Each String
 * represents a single line inside the file.
 * 
 * @author Timon Schwalbe
 */
public class FileBufferedReader {

	private static final Logger log = Logger.getLogger(FileBufferedReader.class.getName());

	/**
	 * Finds a file in the defined resource folder based on the given file name and
	 * reads it line by line saving each line as a String in a List.
	 * 
	 * @param fileName - File name of the file to search for
	 * @return List<String> - A List of Strings containing every line of the read
	 *         file; Or Null if the file could not be found
	 */
	public static List<String> readFile(String fileName) {
		List<String> lines = null;
		InputStream inputStream = FileBufferedReader.class.getClassLoader().getResourceAsStream(fileName);
		InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		try (BufferedReader br = new BufferedReader(streamReader)) {
			lines = new ArrayList<>();
			String line;

			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			log.warning("Could not find file: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.warning("Could not read file: " + e.getMessage());
			e.printStackTrace();
		}
		return lines;
	}
}
