package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ReadableFile extends AbstractFile {

	public ReadableFile(String directoryPath, String baseName, String extension) {
		super(directoryPath, baseName, extension);
	}

	public String getFileContents() throws FileNotFoundException {
		// Read contents
		String fileContents = "";
		Reader fileReader = new InputStreamReader(new FileInputStream(getPath()), StandardCharsets.UTF_8);
		try {
			while (true) {
				int nextChar = fileReader.read();
				if (nextChar == -1) break; // Break on EOF
				fileContents += (char) nextChar;
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
