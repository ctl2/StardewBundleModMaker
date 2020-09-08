package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteableFile extends AbstractFile {

	public WriteableFile(String directoryPath, String baseName, String extension) {
		super(directoryPath, baseName, extension);
	}

	public void write(Object fileContents) {
		String directoryPath = this.getDirectoryPath();
		if (directoryPath != null) new File(directoryPath).mkdirs(); // Make any directories that don't yet exist
        File file = new File(getPath());
        try {
			file.createNewFile();
	        // Write to output file
	        FileWriter fileWriter = new FileWriter(getPath());
	        fileWriter.write(fileContents.toString());
	        fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

}
