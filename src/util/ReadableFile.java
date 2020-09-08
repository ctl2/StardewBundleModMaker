package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadableFile extends AbstractFile {

	public ReadableFile(String directoryPath, String baseName, String extension) {
		super(directoryPath, baseName, extension);
	}

	public String getFileContents() throws FileNotFoundException {
		// Read contents
		BufferedReader fileReader;
		String fileContents = "";
		fileReader = new BufferedReader(new FileReader(getPath()));
		try {
			while (true) {
				String line = fileReader.readLine();
				if (line == null) break; // Break on EOF
				fileContents += line;
			}
			fileReader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return fileContents;
	}

	public boolean exists() {
		return new File(getPath()).exists();
	}

}
