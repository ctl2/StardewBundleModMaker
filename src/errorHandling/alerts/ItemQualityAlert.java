package errorHandling.alerts;

import util.ReadableJsonFile;

// Invalid/unexpected value
public class ItemQualityAlert extends FileContentAlert {
	
	public ItemQualityAlert(ReadableJsonFile bundlesFile, String bundleName, String quality) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, quality) + 
			"is not a recognised quality value."
		);
	}
	
	public ItemQualityAlert(ReadableJsonFile bundlesFile, String bundleName, String optionString, String quality) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, optionString, quality) + 
			"is not a recognised quality value."
		);
	}
	
}
