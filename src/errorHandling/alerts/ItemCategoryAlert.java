package errorHandling.alerts;

import util.ReadableJsonFile;

// Invalid/unexpected value
public class ItemCategoryAlert extends FileContentAlert {
	
	public ItemCategoryAlert(ReadableJsonFile bundlesFile, String bundleName, String category) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, category) + 
			"is not a recognised item category."
		);
	}
	
	public ItemCategoryAlert(ReadableJsonFile bundlesFile, String bundleName, String itemName, String category) {
		super(
			Alert.getProblemPrefix(bundlesFile, bundleName, null, category) + 
			"is not a recognised category for item '" + itemName + "'."
		);
	}
	
}
