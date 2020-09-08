package errorHandling.alerts;

import util.ReadableJsonFile;

// Invalid/unexpected value
public class JsonValueAlert extends FileContentAlert {
	
	public JsonValueAlert(ReadableJsonFile jsonFile, String key, String value) {
		super(
			Alert.getProblemPrefix(jsonFile, null, null, null) + 
			"has an invalid value '" + value + "' for the '" + key + "' key."
		);
	}
	
}
