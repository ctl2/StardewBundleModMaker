package errorHandling.alerts;

import util.ReadableJsonFile;

// Invalid/unexpected value
public class ItemIdAlert extends FileContentAlert {
	
	public ItemIdAlert(ReadableJsonFile bundlesFile, String bundleName, String category, int id) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, category + " " + id) + 
			"is not a recognised item ID."
		);
	}
	
}
