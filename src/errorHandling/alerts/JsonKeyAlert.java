package errorHandling.alerts;

import util.ReadableJsonFile;

// Missing entry
public class JsonKeyAlert extends FileContentAlert {
	
	public JsonKeyAlert(ReadableJsonFile jsonFile, String key) {
		super(
			Alert.getProblemPrefix(jsonFile, null, null, null) + 
			"has no '" + key + "' key."
		);
	}

}
