package errorHandling.alerts;

import util.ReadableFile;

public class MissingFileAlert extends Alert {
	
	public MissingFileAlert(ReadableFile missingFile) {
		super(
			Alert.getProblemPrefix(missingFile, null, null, null) + 
			"is missing.", 
			"Make sure that the file hasn't been moved or renamed."
		);
	}

}
