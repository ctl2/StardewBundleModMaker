package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

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
	        Writer fileWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
	        fileWriter.write(fileContents.toString());
	        fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

}
