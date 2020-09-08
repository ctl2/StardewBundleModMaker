package util;

import java.util.regex.Pattern;

public class AbstractFile {
	
	private class NamingException extends Exception {

		public NamingException(String message) {
			super(message);
		}
		
	}
	
	static private final int pathCharLimit = 200;
	
	static public boolean containsInvalidPathChars(String path) {
		return Pattern.compile("\\\\|:|\\*|\\?|\"|<|>|\\|").matcher(path).find();
	}
	
	static public boolean containsInvalidFileChars(String file) {
		return !containsInvalidPathChars(file) || Pattern.compile("/").matcher(file).find();
	}
	
	private String path;
	
	public AbstractFile(String directoryPath, String name, String extension) {
		try {
			setPath(directoryPath, name, extension);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setPath(String directoryPath, String name, String extension) throws NamingException {
		if (name == null) {
			setPath(directoryPath, extension);
			return;
		}
		if (name.isEmpty())
			throw new NamingException("File name may not be an empty string.");
		if (name.contains("/"))
			throw new NamingException("File names may not contain forward slashes.");
		// Form path string
		String path = (
			directoryPath == null?
			name:
			directoryPath + "/" + name
		);
		if (extension != null) path += "." + extension;
		// Test path validity
		if (path.length() > pathCharLimit)
			throw new NamingException("Path may not be over " + pathCharLimit + " characters in length.");
		if (path.contains("//")) 
			throw new NamingException("Path may not contain '//'.");
		if (path.startsWith("/")) 
			throw new NamingException("Path may not start with '/'.");
		if (containsInvalidPathChars(path))
				throw new NamingException("Path contains an invalid character.");
		this.path = path;
	}

	private void setPath(String directoryPath, String extension) throws NamingException {
		// Ensure that /directoryPath/ and /extension/ aren't invalid.
		setPath(directoryPath, "test", extension);
		// Set path via a user-provided file name
		while (true) {
			System.out.println("Please enter the name of your file.");
			String name = InputReading.getString();
			try {
				if (
					extension != null && 
					name.length() > extension.length() && 
					name.substring(name.length() - extension.length() - 1).equals("." + extension)
				) {
					// Remove the extension if the user has added it
					/* Note: 
					 * If the user inputs the string '.json' (for example),
					 * referring to a file named '.json.json',
					 * their input may be trimmed incorrectly.
					 */
					setPath(directoryPath, name.substring(0, name.length() - extension.length() - 1), extension);
				} else {
					setPath(directoryPath, name, extension);
				}
				break;
			} catch (NamingException e) {
				System.out.println(e.getMessage() + " Please use a valid file name.\n");
			}
		}
	}

	public String getPath() {
		return this.path;
	}

	public String getDirectoryPath() {
		String fileName = getName();
		if (fileName.length() == this.path.length()) return null;
		return path.substring(0, this.path.length() - fileName.length() - 1);
	}

	public String getName() {
		String[] pathNodes = this.path.split("/");
		return pathNodes[pathNodes.length - 1];
	}

	public String getBaseName() {
		String name = getName();
		String[] nameParts = name.split("\\.");
		if (nameParts.length == 1) return name;
		return getName().substring(0, name.length() - nameParts[nameParts.length - 1].length() - 1);
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
