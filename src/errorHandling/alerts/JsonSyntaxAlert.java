package errorHandling.alerts;

import util.ReadableJsonFile;

public class JsonSyntaxAlert extends FileContentAlert {
	
	public JsonSyntaxAlert(ReadableJsonFile jsonFile) {
		super(
			Alert.getProblemPrefix(jsonFile, null, null, null) + 
			"does not contain valid JSON."
		);
	}

}
