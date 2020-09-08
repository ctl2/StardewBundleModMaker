package errorHandling.alerts;

import util.ReadableJsonFile;

// Should only be passed to the BundleException class
public class ValueTypeAlert extends FileContentAlert {
	
	public ValueTypeAlert(ReadableJsonFile jsonFile, String bundleName, String data, String value, Class<?> expectedType) {
		super(
			Alert.getProblemPrefix(jsonFile, bundleName, data, value) + 
			"should be of " + expectedType.getSimpleName() + " type."
		);
	}

}
