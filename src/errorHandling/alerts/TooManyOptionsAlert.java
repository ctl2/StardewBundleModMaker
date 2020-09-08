package errorHandling.alerts;

import util.ReadableJsonFile;

// Should only be passed to the BundleException class
public class TooManyOptionsAlert extends FileContentAlert {
	
	public TooManyOptionsAlert(ReadableJsonFile jsonFile, String bundleName, int optionQuantity) {
		super(
			Alert.getProblemPrefix(jsonFile, bundleName, null, null) + 
			"has " + optionQuantity + " options. " + 
			"Anything over 12 will crash the bundle menu in-game."
		);
	}

}
