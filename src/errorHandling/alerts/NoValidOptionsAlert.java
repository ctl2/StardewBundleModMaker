package errorHandling.alerts;

import util.ReadableJsonFile;

// Should only be passed to the BundleException class
public class NoValidOptionsAlert extends FileContentAlert {
	
	public NoValidOptionsAlert(ReadableJsonFile bundlesFile, String bundleName) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, null) + 
			"has no valid options."
		);
	}

}
